package com.planit.planit.domain.attendance.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.planit.planit.domain.attendance.dto.AttendanceDTO;
import com.planit.planit.domain.attendance.dto.AttendanceDailyResponseDto;

@Mapper
public interface AttendanceMapper {
  // 일단위 출결 조회
  public AttendanceDailyResponseDto getDaily(Long userId, Long bootcampId, String date);

  // 일단위 출결 등록
  public void regist(AttendanceDTO dto);

  // 일단위 출결 수정
  public void update(AttendanceDTO dto);
}
