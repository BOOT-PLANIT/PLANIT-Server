package com.planit.planit.domain.attendance.mapper;

import java.sql.SQLException;
import org.apache.ibatis.annotations.Mapper;
import com.planit.planit.domain.attendance.dto.AttendanceDTO;
import com.planit.planit.domain.attendance.dto.AttendanceDailyResponseDto;

@Mapper
public interface AttendanceMapper {
  // 일단위 출결 조회
  public AttendanceDailyResponseDto getDaily(Long userId, Long bootcampId, String date)
      throws SQLException;

  // 일단위 출결 등록
  public void regist(AttendanceDTO dto) throws SQLException;

  // 일단위 출결 수정
  public void update(AttendanceDTO dto) throws SQLException;
}
