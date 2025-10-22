package com.planit.planit.domain.attendance.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planit.planit.domain.attendance.dto.AttendanceDTO;
import com.planit.planit.domain.attendance.dto.AttendanceDailyResponseDTO;
import com.planit.planit.domain.attendance.dto.AttendanceRegistRequestDTO;
import com.planit.planit.domain.attendance.mapper.AttendanceMapper;
import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class AttendanceService {

  @NotNull
  private AttendanceMapper mapper;


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
   * 일일 기간 출결 정보 등록
   * 
   * @param attendance 출결 정보 (테이블 전체정보)
   */

  @Transactional
  public void regist(AttendanceRegistRequestDTO requestDTO) {
    log.info("[출결 등록 시작] attendance={}", requestDTO);

    long bootcampId = requestDTO.getBootcampId();
    String date = requestDTO.getDate();
    // 특정 날짜에 강의가 있는지 없는지 체크 sessionId,periodId 받아옴
    Map<String, Object> result = mapper.getDailySession(bootcampId, date);

    if (result == null) {
      throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "해당 날짜에는 강의가 없습니다.") {};

    } else {
      AttendanceDTO attendance = new AttendanceDTO();
      attendance.setUserId(requestDTO.getUserId());
      attendance.setSessionId(((Number) result.get("id")).longValue());
      attendance.setPeriodId(((Number) result.get("period_id")).longValue());
      attendance.setStatus(requestDTO.getStatus());

      // 중복 방지: 기존 출결 존재 여부 확인
      if (mapper.getDaily(requestDTO.getUserId(), requestDTO.getBootcampId(),
          requestDTO.getDate()) != null) {
        throw new BaseException(ErrorCode.CONFLICT, "이미 등록된 출결입니다.") {};
      }
      // 출결 등록
      mapper.regist(attendance);
    }


  }

  /**
   * 일일 기간 출결 정보 등록
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
   * @param periodId
   */
  public List<AttendanceDailyResponseDTO> getPeriod(Long userId, Long bootcampId, Integer unitNo) {
    List<AttendanceDailyResponseDTO> attendance = mapper.getPeriod(userId, bootcampId, unitNo);
    return attendance;
  }

}
