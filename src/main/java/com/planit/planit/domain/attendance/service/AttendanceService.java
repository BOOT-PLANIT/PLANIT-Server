package com.planit.planit.domain.attendance.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planit.planit.domain.attendance.dto.AttendanceDTO;
import com.planit.planit.domain.attendance.dto.AttendanceDailyResponseDTO;
import com.planit.planit.domain.attendance.dto.AttendanceRegistRequestDTO;
import com.planit.planit.domain.attendance.dto.AttendanceTotalResponseDTO;
import com.planit.planit.domain.attendance.dto.SessionSimpleDTO;
import com.planit.planit.domain.attendance.mapper.AttendanceMapper;
import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceService {

  @NotNull
  private final AttendanceMapper mapper;

  /**
   * 일일 기간 출결 정보 조회
   * 
   * @param date 선택한 날짜(YYYY-MM-DD)
   * @return 선택한 날짜로 조회한 출결 정보
   */
  public AttendanceDailyResponseDTO getDaily(Long userId, Long bootcampId, String date) {
    AttendanceDailyResponseDTO daily = mapper.getDaily(userId, bootcampId, date);
    if (daily == null) {
      // 특정 날짜에 강의가 있는지 없는지 체크 sessionId,periodId 받아옴
      Map<String, Object> result = mapper.getDailySession(bootcampId, date);

      if (result == null) { // 해당 날짜에 강의 일정이 없음
        throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "해당 날짜에는 강의가 없습니다.") {};
      } else { // 해당 날짜에 강의는 있지만 출석등록을 아직 하지않음
        throw new BaseException(ErrorCode.PARAMETER_NOT_FOUND, "출결 등록이 필요합니다.") {};
      }
    }
    return daily;
  }

  /**
   * 출결 정보 등록
   * 
   * @param attendance 출결 정보 classDates는 List<String>으로 받음
   */

  @Transactional
  public void regist(AttendanceRegistRequestDTO requestDTO) {
    log.info("[출결 등록 시작] attendance={}", requestDTO);

    List<String> classDates = requestDTO.getClassDates();
    List<String> invalidFormatDates = new ArrayList<>();

    for (String dateStr : classDates) {
      try {
        LocalDate.parse(dateStr); // 형식이 ISO(yyyy-MM-dd)가 아니면 예외 발생
      } catch (DateTimeParseException e) {
        invalidFormatDates.add(dateStr);
      }
    }

    if (!invalidFormatDates.isEmpty()) {
      throw new BaseException(ErrorCode.FORBIDDEN,
          "잘못된 날짜 형식이 있습니다. 형식은 YYYY-MM-DD 이어야 합니다: " + invalidFormatDates) {};
    }

    // 현재 날짜
    LocalDate today = LocalDate.now();

    // 미래 날짜 필터링
    List<String> invalidFutureDates = requestDTO.getClassDates().stream()
        .filter(date -> LocalDate.parse(date).isAfter(today)).toList();

    if (!invalidFutureDates.isEmpty()) {
      throw new BaseException(ErrorCode.FORBIDDEN,
          "미래 날짜는 출결 등록이 불가능합니다. 잘못된 날짜: " + invalidFutureDates) {};
    }

    // ️날짜 기반 세션 + 기간 정보 조회
    List<SessionSimpleDTO> sessions =
        mapper.getSession(requestDTO.getBootcampId(), requestDTO.getClassDates());

    if (sessions.isEmpty()) {
      throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "선택한 날짜에 해당하는 강의가 없습니다.") {};
    }

    if (sessions.size() != requestDTO.getClassDates().size()) {
      throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "선택한 날짜중에 강의가 없는날짜가 포함되어있습니다.") {};

    } else {
      // 이미 등록된 출결 확인
      List<String> existingDates = mapper.findAttendanceDates(requestDTO.getUserId(),
          requestDTO.getBootcampId(), requestDTO.getClassDates());

      if (!existingDates.isEmpty()) {
        throw new BaseException(ErrorCode.CONFLICT, "이미 출결이 등록된 날짜가 있습니다: " + existingDates) {};
      }

      // AttendanceDTO 리스트 생성
      List<AttendanceDTO> attendanceList =
          sessions.stream().map(s -> new AttendanceDTO(requestDTO.getUserId(), s.getSessionId(),
              s.getPeriodId(), requestDTO.getStatus())).toList();

      // 출결 등록
      mapper.regist(attendanceList);
    }
  }

  /**
   * 일일 기간 출결 정보 수정
   * 
   * @param attendance 출결 정보 (출결상태값,attendanceId 만 포함)
   */
  public void update(AttendanceDTO attendance) {
    log.info("[출결 수정 시작] attendance={}", attendance);
    mapper.update(attendance);
  }

  /**
   * 단위 기간 출결 정보 조회
   * 
   * @param userId 사용자 ID
   * @param bootcampId 부트캠프 ID
   * @param unitNo 단위 기간 번호
   * @return attendance 기간단위 출결 현황 (출석,조퇴,휴가 등등 및 전체 출석일수 카운트, 훈련지원금 계산)
   */
  public AttendanceTotalResponseDTO getPeriod(Long userId, Long bootcampId, Integer unitNo) {
    List<Integer> unitList = mapper.getBootcampUnitno(bootcampId);
    if (!unitList.contains(unitNo)) { // 부트캠프에 존재하는 단위기간인지 검사
      throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "해당 부트캠프에 존재하지 않는 단위기간입니다.") {};
    }
    AttendanceTotalResponseDTO attendance = mapper.getPeriod(userId, bootcampId, unitNo);
    if (attendance == null) { // 단위기간은 있지만 단위기간에 아직 등록된 출결이 없음
      throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "해당 단위기간에 등록된 출결이 없습니다.") {};
    }

    // 출석,지각,조퇴,연차,휴가의 합
    Integer totalPresentCount = attendance.getPresentCount() + attendance.getLateCount()
        + attendance.getLeftEarlyCount() + attendance.getAnnualCount() + attendance.getLeaveCount();

    // 지각+조퇴 3회 쌓인거 출석수에 반영
    totalPresentCount -= (attendance.getLateCount() + attendance.getLeftEarlyCount()) / 3;

    // 실제 총 결석수
    Integer totalAbsentCount = attendance.getAbsentCount()
        + (attendance.getLateCount() + attendance.getLeftEarlyCount()) / 3;

    attendance.setTotalPresentCount(totalPresentCount);
    attendance.setTotalAbsentCount(totalAbsentCount);

    // KDT 여부 확인 (훈련비 단가 결정)
    Integer subsidy = mapper.Iskdt(bootcampId) ? 15800 : 5800;

    // 단위기간별 훈련비 계산 (최대 20일)
    int count = Math.min(totalPresentCount, 20);
    attendance.setTotalSubsidy(count * subsidy);


    return attendance;
  }

  /**
   * 오늘까지 전체출결 정보 조회
   * 
   * @param userId 사용자 ID
   * @param bootcampId 부트캠프 ID
   * @return attendance 처음부터 오늘까지 전체 출결 현황 (출석,조퇴,휴가 등등 및 전체 출석일수 카운트)
   */
  public AttendanceTotalResponseDTO getTotal(Long userId, Long bootcampId) {

    // 현재까지 전체 기간 출결 정보 받아오기
    List<AttendanceTotalResponseDTO> totalAttendance = mapper.getTotal(userId, bootcampId);
    if (totalAttendance == null || totalAttendance.isEmpty()) {
      throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "불러올 출결정보가 없습니다.") {};
    }

    AttendanceTotalResponseDTO attendance = new AttendanceTotalResponseDTO();
    attendance.setUserId(userId);

    int presentCount = 0;
    int absentCount = 0;
    int lateCount = 0;
    int leftEarlyCount = 0;
    int annualCount = 0;
    int leaveCount = 0;

    int totalPresentCount = 0;
    int totalAbsentCount = 0;
    int totalSubsidy = 0;

    // KDT 여부 확인 (훈련비 단가 결정)
    int subsidyPerDay = mapper.Iskdt(bootcampId) ? 15800 : 5800;

    // 각 단위기간별로 출석, 결석, 훈련비 계산
    for (AttendanceTotalResponseDTO dto : totalAttendance) {

      int periodLate = dto.getLateCount();
      int periodLeftEarly = dto.getLeftEarlyCount();
      int periodAbsent = dto.getAbsentCount();
      int periodPresent = dto.getPresentCount();
      int periodAnnual = dto.getAnnualCount();
      int periodLeave = dto.getLeaveCount();

      // 단위기간별 지각/조퇴 3회 = 결석 1회
      int additionalAbsents = (periodLate + periodLeftEarly) / 3;

      // 단위기간별 실제 출석/결석
      int periodTotalPresent = periodPresent + periodLate + periodLeftEarly + periodAnnual
          + periodLeave - additionalAbsents;
      int periodTotalAbsent = periodAbsent + additionalAbsents;

      // 단위기간별 훈련비 계산 (최대 20일)
      int count = Math.min(periodTotalPresent, 20);
      int periodSubsidy = count * subsidyPerDay;

      // 누적합산
      presentCount += periodPresent;
      absentCount += periodAbsent;
      lateCount += periodLate;
      leftEarlyCount += periodLeftEarly;
      annualCount += periodAnnual;
      leaveCount += periodLeave;

      totalPresentCount += periodTotalPresent;
      totalAbsentCount += periodTotalAbsent;
      totalSubsidy += periodSubsidy;
    }

    // 합계 결과 저장
    attendance.setPresentCount(presentCount);
    attendance.setAbsentCount(absentCount);
    attendance.setLateCount(lateCount);
    attendance.setLeftEarlyCount(leftEarlyCount);
    attendance.setAnnualCount(annualCount);
    attendance.setLeaveCount(leaveCount);
    attendance.setTotalPresentCount(totalPresentCount);
    attendance.setTotalAbsentCount(totalAbsentCount);
    attendance.setTotalSubsidy(totalSubsidy);

    // 부트캠프 전체 수강일 저장
    attendance.setTotalSessions(mapper.bootcampTotalSession(bootcampId));

    return attendance;
  }
}
