package com.planit.planit.domain.bootcamp.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planit.planit.domain.bootcamp.dto.BootcampDTO;
import com.planit.planit.domain.bootcamp.exception.BootcampInvalidClassDatesException;
import com.planit.planit.domain.bootcamp.exception.BootcampNotFoundException;
import com.planit.planit.domain.bootcamp.mapper.BootcampMapper;
import com.planit.planit.domain.session.dto.SessionDTO;
import com.planit.planit.domain.session.mapper.SessionMapper;
import com.planit.planit.domain.unitperiod.dto.UnitPeriodDTO;
import com.planit.planit.domain.unitperiod.mapper.UnitPeriodMapper;

@Service
public class BootcampService {
  private final BootcampMapper bootcampMapper;
  private final SessionMapper sessionMapper;
  private final UnitPeriodMapper unitPeriodMapper;

  public BootcampService(BootcampMapper bootcampMapper, SessionMapper sessionMapper,
      UnitPeriodMapper unitPeriodMapper) {
    this.bootcampMapper = bootcampMapper;
    this.sessionMapper = sessionMapper;
    this.unitPeriodMapper = unitPeriodMapper;
  }

  public List<BootcampDTO> getAllBootcamps() {
    return bootcampMapper.findAll();
  }

  public BootcampDTO getBootcamp(Long id) {
    BootcampDTO bootcamp = bootcampMapper.findById(id);
    if (bootcamp == null) {
      throw new BootcampNotFoundException("ID가 " + id + "인 부트캠프를 찾을 수 없습니다.");
    }
    return bootcamp;
  }

  @Transactional
  public void addBootcamp(BootcampDTO bootcamp) {
    // 1. 부트캠프 저장
    bootcampMapper.insert(bootcamp);

    // 2. 교육일이 있으면 단위기간 및 세션 생성
    createUnitPeriodsAndSessions(bootcamp);
  }

  /**
   * 교육일이 속한 단위기간 번호를 계산
   *
   * @param baseDate 기준일 (첫 교육일)
   * @param targetDate 계산할 날짜
   * @param baseDay 기준일의 일(day)
   * @return 단위기간 번호 (1부터 시작)
   */
  private int calculateUnitNo(LocalDate baseDate, LocalDate targetDate, int baseDay) {
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
   * 교육일을 기반으로 단위기간과 세션을 생성
   *
   * @param bootcamp 부트캠프 정보 (교육일 포함)
   */
  private void createUnitPeriodsAndSessions(BootcampDTO bootcamp) {
    // 교육일이 없으면 처리하지 않음
    if (bootcamp.getClassDates() == null || bootcamp.getClassDates().isEmpty()) {
      return;
    }

    // 교육일 유효성 검증
    validateClassDates(bootcamp.getClassDates());

    // 교육일 정렬
    List<LocalDate> sortedDates = new ArrayList<>(bootcamp.getClassDates());
    sortedDates.sort(LocalDate::compareTo);

    // 첫 날짜의 일(day)을 기준으로 단위기간 생성
    LocalDate firstDate = sortedDates.get(0);
    int baseDay = firstDate.getDayOfMonth();

    // 각 교육일이 속한 단위기간을 계산하고 생성
    java.util.Map<Integer, UnitPeriodDTO> periodMap =
        createUnitPeriods(bootcamp.getId(), sortedDates, firstDate, baseDay);

    // 세션 생성 (각 교육일을 해당 단위기간에 연결)
    createSessions(bootcamp.getId(), sortedDates, periodMap, firstDate, baseDay);
  }

  /**
   * 교육일 유효성 검증
   *
   * @param classDates 교육일 목록
   */
  private void validateClassDates(List<LocalDate> classDates) {
    if (classDates.stream().anyMatch(date -> date == null)) {
      throw new BootcampInvalidClassDatesException("교육일에 null 값이 포함되어 있습니다.");
    }
  }

  /**
   * 단위기간 생성
   *
   * @param bootcampId 부트캠프 ID
   * @param sortedDates 정렬된 교육일 목록
   * @param firstDate 첫 교육일
   * @param baseDay 기준일
   * @return 단위기간 번호별 단위기간 맵
   */
  private java.util.Map<Integer, UnitPeriodDTO> createUnitPeriods(Long bootcampId,
      List<LocalDate> sortedDates, LocalDate firstDate, int baseDay) {
    java.util.Map<Integer, UnitPeriodDTO> periodMap = new java.util.HashMap<>();

    for (LocalDate classDate : sortedDates) {
      int unitNo = calculateUnitNo(firstDate, classDate, baseDay);

      // 해당 단위기간이 아직 생성되지 않았으면 생성
      if (!periodMap.containsKey(unitNo)) {
        UnitPeriodDTO unitPeriod = new UnitPeriodDTO();
        unitPeriod.setBootcampId(bootcampId);
        unitPeriod.setUnitNo(unitNo);

        // 단위기간의 시작/종료일 계산 (baseDay 기준, 연속성 보장)
        YearMonth ym = YearMonth.from(firstDate).plusMonths(unitNo - 1);
        int startDay = Math.min(baseDay, ym.lengthOfMonth());
        LocalDate periodStart = ym.atDay(startDay);
        YearMonth nextYm = ym.plusMonths(1);
        int nextStartDay = Math.min(baseDay, nextYm.lengthOfMonth());
        LocalDate nextStart = nextYm.atDay(nextStartDay);
        LocalDate periodEnd = nextStart.minusDays(1);

        unitPeriod.setStartDate(periodStart);
        unitPeriod.setEndDate(periodEnd);
        unitPeriodMapper.insert(unitPeriod);
        periodMap.put(unitNo, unitPeriod);
      }
    }

    return periodMap;
  }

  /**
   * 세션 생성
   *
   * @param bootcampId 부트캠프 ID
   * @param sortedDates 정렬된 교육일 목록
   * @param periodMap 단위기간 맵
   * @param firstDate 첫 교육일
   * @param baseDay 기준일
   */
  private void createSessions(Long bootcampId, List<LocalDate> sortedDates,
      java.util.Map<Integer, UnitPeriodDTO> periodMap, LocalDate firstDate, int baseDay) {
    List<SessionDTO> sessions = new ArrayList<>();

    for (LocalDate classDate : sortedDates) {
      int unitNo = calculateUnitNo(firstDate, classDate, baseDay);
      UnitPeriodDTO period = periodMap.get(unitNo);

      SessionDTO session = new SessionDTO();
      session.setBootcampId(bootcampId);
      session.setPeriodId(period.getId());
      session.setClassDate(classDate);
      sessions.add(session);
    }

    sessionMapper.insertBatch(sessions);
  }

  @Transactional
  public void updateBootcamp(BootcampDTO bootcamp) {
    // 부트캠프 존재 확인
    BootcampDTO existingBootcamp = bootcampMapper.findById(bootcamp.getId());
    if (existingBootcamp == null) {
      throw new BootcampNotFoundException("ID가 " + bootcamp.getId() + "인 부트캠프를 찾을 수 없습니다.");
    }

    bootcampMapper.update(bootcamp);

    // 기존 세션 및 단위기간 삭제
    sessionMapper.deleteByBootcampId(bootcamp.getId());
    unitPeriodMapper.deleteByBootcampId(bootcamp.getId());

    // 교육일이 있으면 단위기간 및 세션 재생성
    createUnitPeriodsAndSessions(bootcamp);
  }

  @Transactional
  public void deleteBootcamp(Long id) {
    // 부트캠프 존재 확인
    BootcampDTO existingBootcamp = bootcampMapper.findById(id);
    if (existingBootcamp == null) {
      throw new BootcampNotFoundException("ID가 " + id + "인 부트캠프를 찾을 수 없습니다.");
    }

    // 연관된 세션 먼저 삭제
    sessionMapper.deleteByBootcampId(id);
    // 연관된 단위기간 삭제
    unitPeriodMapper.deleteByBootcampId(id);
    // 부트캠프 삭제
    bootcampMapper.delete(id);
  }
}
