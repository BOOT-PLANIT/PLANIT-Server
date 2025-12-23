package com.planit.planit.domain.attendance.dto;

import lombok.Data;

@Data
public class LeaveBalanceResponseDTO {
  private int usedAnnual;// 사용한 연차
  private int totalAnnual;// 현재까지 받은 모든 연차
  private int remainingAnnual;// 남은 연차
  private int usedSickLeave;// 사용한 병가
  private int totalSickLeave;// 훈련기간중 사용할수있는 병가 수
  private int remainingSickLeave;// 남은 병가

}
