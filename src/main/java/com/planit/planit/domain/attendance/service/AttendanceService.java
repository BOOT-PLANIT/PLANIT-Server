package com.planit.planit.domain.attendance.service;

import org.springframework.stereotype.Service;
import com.planit.planit.domain.attendance.dto.AttendanceDTO;
import com.planit.planit.domain.attendance.dto.AttendanceDailyResponseDto;
import com.planit.planit.domain.attendance.mapper.AttendanceMapper;
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
  public AttendanceDailyResponseDto getDaily(Long userId, Long bootcampId, String date) {
    AttendanceDailyResponseDto daily = mapper.getDaily(userId, bootcampId, date);
    return daily;
  }

  /**
   * 일일 기간 출결 정보 등록
   * 
   * @param attendance 출결 정보 (테이블 전체정보)
   */

  public void regist(AttendanceDTO attendance) {
    log.info("[출결 등록 시작] attendance={}", attendance);
    mapper.regist(attendance);

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

}
