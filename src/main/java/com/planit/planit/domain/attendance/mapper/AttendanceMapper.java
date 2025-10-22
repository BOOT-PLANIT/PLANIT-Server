package com.planit.planit.domain.attendance.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import com.planit.planit.domain.attendance.dto.AttendanceDTO;
import com.planit.planit.domain.attendance.dto.AttendanceDailyResponseDTO;
import com.planit.planit.domain.attendance.dto.AttendanceTotalResponseDTO;

@Mapper
public interface AttendanceMapper {
  // 일단위 출결 조회
  public AttendanceDailyResponseDTO getDaily(Long userId, Long bootcampId, String date);

  // 특정 날짜 수강일정 체크
  public Map<String, Object> getDailySession(Long bootcampId, String date);

  // 일단위 출결 등록
  public void regist(AttendanceDTO dto);

  // 일단위 출결 수정
  public void update(AttendanceDTO dto);

  // 단위기간 출결 조회
  public AttendanceTotalResponseDTO getPeriod(Long userId, Long bootcampId, Integer unitNo);

  // 특정 부트캠프에 단위기간 조회ㅇ
  public List<Integer> getBootcampUnitno(Long bootcampId);

  // 오늘까지의 전체출결 조회
  public AttendanceTotalResponseDTO getTotal(Long userId, Long bootcampId, String today);
}
