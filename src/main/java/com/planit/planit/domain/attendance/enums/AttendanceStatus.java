package com.planit.planit.domain.attendance.enums;

public enum AttendanceStatus {
  present, // 출석
  absent, // 결석
  late, // 지각
  left_early, // 조퇴
  outing, // 외출
  annual, // 연차
  leave, // 공가
  sick_leave, // 병가
  no_session, // 강의없음
  no_attendance // 미출결
}
