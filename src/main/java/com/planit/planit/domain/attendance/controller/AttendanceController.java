package com.planit.planit.domain.attendance.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.planit.planit.domain.attendance.dto.AttendanceDTO;
import com.planit.planit.domain.attendance.dto.AttendanceDailyResponseDto;
import com.planit.planit.domain.attendance.service.AttendanceService;

@RestController
@RequestMapping("api/v1/attendance")
public class AttendanceController {

  private AttendanceService service;

  public AttendanceController(AttendanceService service) {
    this.service = service;
  }

  // 일단위 출결 조회
  @GetMapping("/{userId}")
  public ResponseEntity<?> getDailyAttendance(@PathVariable("userId") long userId,
      @RequestParam(value = "date") String date,
      @RequestParam(value = "bootcampId") long bootcampId) {
    AttendanceDailyResponseDto daily = service.getDaily(userId, bootcampId, date);

    if (daily == null) {
      return new ResponseEntity<String>("등록된출결없음 등록하세요", HttpStatus.OK);

    } else {
      return new ResponseEntity<AttendanceDailyResponseDto>(daily, HttpStatus.OK);
    }

  }


  // 일단위 출결 등록
  @PostMapping
  public ResponseEntity<String> AttendanceRegist(@RequestBody AttendanceDTO attendance) {
    service.regist(attendance);
    return new ResponseEntity<String>("SUCCESS", HttpStatus.CREATED);
  }

  // 일단위 출결 수정
  @PatchMapping
  public ResponseEntity<String> AttendanceUpdate(@RequestBody AttendanceDTO attendance) {
    service.update(attendance);
    return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
  }
}
