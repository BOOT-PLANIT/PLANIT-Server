package com.planit.planit.domain.attendance.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planit.planit.domain.attendance.dto.AttendanceDTO;
import com.planit.planit.domain.attendance.dto.AttendanceDailyResponseDTO;
import com.planit.planit.domain.attendance.dto.AttendanceRegistRequestDTO;
import com.planit.planit.domain.attendance.mapper.AttendanceMapper;
import com.planit.planit.global.common.exception.BaseException;

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
    //특정 날짜에 강의가 있는지 없는지 체크
    Map<String, Object> result = mapper.getDailySession(bootcampId,date);
    
    if(result==null) {
//    	BaseException(INTERNAL_SERVER_ERROR,"강의가 없는날");
    	System.out.println("강의없");
    } else {
    	AttendanceDTO attendance = new AttendanceDTO();
    	attendance.setUserId(requestDTO.getUserId());
    	attendance.setSessionId(((Number) result.get("id")).longValue());
    	attendance.setPeriodId(((Number) result.get("period_id")).longValue());
    	attendance.setStatus(requestDTO.getStatus());
    	//강의있는날 출결 등록
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

}
