package com.planit.planit.domain.unitperiod.util;

import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.stereotype.Component;

/**
 * 단위기간 계산 유틸리티
 * - unitNo 계산
 * - 단위기간 시작일/종료일 계산
 */
@Component
public class UnitPeriodCalculator {

  /**
   * 교육일이 속한 단위기간 번호를 계산합니다.
   * 
   * @param baseDate 기준일 (첫 교육일)
   * @param targetDate 계산할 날짜
   * @param baseDay 기준일의 일(day)
   * @return 단위기간 번호 (1부터 시작)
   */
  public int calculateUnitNo(LocalDate baseDate, LocalDate targetDate, int baseDay) {
    // 월 차이 계산
    int monthsDiff = (targetDate.getYear() - baseDate.getYear()) * 12
        + (targetDate.getMonthValue() - baseDate.getMonthValue());

    // 해당 월의 실제 기준일 계산 (월말 처리)
    YearMonth targetYm = YearMonth.from(targetDate);
    int actualBaseDay = Math.min(baseDay, targetYm.lengthOfMonth());

    // 기준일보다 이전이면 이전 단위기간
    if (targetDate.getDayOfMonth() < actualBaseDay) {
      return Math.max(1, monthsDiff); // 최소 1
    }

    return monthsDiff + 1;
  }

  /**
   * 단위기간의 시작일을 계산합니다.
   * 
   * @param baseDate 기준일 (첫 교육일)
   * @param unitNo 단위기간 번호
   * @param baseDay 기준일의 일(day)
   * @return 단위기간 시작일
   */
  public LocalDate calculatePeriodStartDate(LocalDate baseDate, int unitNo, int baseDay) {
    YearMonth startYm = YearMonth.from(baseDate).plusMonths(unitNo - 1);
    int startDay = Math.min(baseDay, startYm.lengthOfMonth());
    return startYm.atDay(startDay);
  }

  /**
   * 단위기간의 종료일을 계산합니다.
   * 
   * @param baseDate 기준일 (첫 교육일)
   * @param unitNo 단위기간 번호
   * @param baseDay 기준일의 일(day)
   * @return 단위기간 종료일
   */
  public LocalDate calculatePeriodEndDate(LocalDate baseDate, int unitNo, int baseDay) {
    YearMonth startYm = YearMonth.from(baseDate).plusMonths(unitNo - 1);
    YearMonth endYm = startYm.plusMonths(1);
    int endDay = Math.min(baseDay, endYm.lengthOfMonth());
    LocalDate nextStart = endYm.atDay(endDay);
    return nextStart.minusDays(1);
  }

  /**
   * 기준일(baseDay)을 계산합니다.
   * 
   * @param baseDate 기준일 (첫 교육일)
   * @return 기준일의 일(day)
   */
  public int getBaseDay(LocalDate baseDate) {
    return baseDate.getDayOfMonth();
  }
}

