package com.planit.planit.domain.attendance.service;

import java.sql.SQLException;
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


  // 일단위 출결 조회
  public AttendanceDailyResponseDto getDaily(Long userId, Long bootcampId, String date) {
    try {
      AttendanceDailyResponseDto daily = mapper.getDaily(userId, bootcampId, date);
      return daily;
    } catch (SQLException e) {
      log.error("[일일 출결 조회 실패]  원인={}", e.getMessage(), e);
      return null;
    }
  }

  // 일단위 출결 등록
  public void regist(AttendanceDTO attendance) {
    log.info("[출결 등록 시작] attendance={}", attendance);
    try {
      mapper.regist(attendance);

    } catch (SQLException e) {
      log.error("[출결 등록 실패] userId={}, 원인={}", attendance.getUserId(), e.getMessage(), e);
    }
  }

  // 일단위 출결 등록
  public void update(AttendanceDTO attendance) {
    log.info("[출결 수정 시작] attendance={}", attendance);
    try {
      mapper.update(attendance);

    } catch (SQLException e) {
      log.error("[출결 수정 실패] userId={}, 원인={}", attendance.getUserId(), e.getMessage(), e);
    }
  }

}
