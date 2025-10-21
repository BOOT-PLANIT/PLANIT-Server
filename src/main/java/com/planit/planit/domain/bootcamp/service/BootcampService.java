package com.planit.planit.domain.bootcamp.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planit.planit.domain.bootcamp.dto.BootcampDTO;
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
    return bootcampMapper.findById(id);
  }

  @Transactional
  public void addBootcamp(BootcampDTO bootcamp) {
    // 1. 부트캠프 저장
    bootcampMapper.insert(bootcamp);

    // 2. 교육일이 있으면 단위기간 및 세션 생성
    if (bootcamp.getClassDates() != null && !bootcamp.getClassDates().isEmpty()) {
      // 교육일 정렬
      List<LocalDate> sortedDates = new ArrayList<>(bootcamp.getClassDates());
      sortedDates.sort(LocalDate::compareTo);

      // 첫 날짜의 일(day)을 기준으로 단위기간 생성
      LocalDate firstDate = sortedDates.get(0);
      int baseDay = firstDate.getDayOfMonth();

      // 각 교육일이 속한 단위기간을 계산하고 생성
      java.util.Map<Integer, UnitPeriodDTO> periodMap = new java.util.HashMap<>();

      for (LocalDate classDate : sortedDates) {
        // 해당 날짜가 속한 단위기간 번호 계산
        int unitNo = calculateUnitNo(firstDate, classDate, baseDay);

        // 해당 단위기간이 아직 생성되지 않았으면 생성
        if (!periodMap.containsKey(unitNo)) {
          UnitPeriodDTO unitPeriod = new UnitPeriodDTO();
          unitPeriod.setBootcampId(bootcamp.getId());
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

      // 3. 세션 생성 (각 교육일을 해당 단위기간에 연결)
      List<SessionDTO> sessions = new ArrayList<>();
      for (LocalDate classDate : sortedDates) {
        int unitNo = calculateUnitNo(firstDate, classDate, baseDay);
        UnitPeriodDTO period = periodMap.get(unitNo);

        SessionDTO session = new SessionDTO();
        session.setBootcampId(bootcamp.getId());
        session.setPeriodId(period.getId());
        session.setClassDate(classDate);
        sessions.add(session);
      }
      sessionMapper.insertBatch(sessions);
    }
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

  @Transactional
  public void updateBootcamp(BootcampDTO bootcamp) {
    bootcampMapper.update(bootcamp);

    // 기존 세션 및 단위기간 삭제
    sessionMapper.deleteByBootcampId(bootcamp.getId());
    unitPeriodMapper.deleteByBootcampId(bootcamp.getId());

    // 교육일이 있으면 단위기간 및 세션 재생성
    if (bootcamp.getClassDates() != null && !bootcamp.getClassDates().isEmpty()) {
      // 교육일 정렬
      List<LocalDate> sortedDates = new ArrayList<>(bootcamp.getClassDates());
      sortedDates.sort(LocalDate::compareTo);

      // 첫 날짜의 일(day)을 기준으로 단위기간 생성
      LocalDate firstDate = sortedDates.get(0);
      int baseDay = firstDate.getDayOfMonth();

      // 각 교육일이 속한 단위기간을 계산하고 생성
      java.util.Map<Integer, UnitPeriodDTO> periodMap = new java.util.HashMap<>();

      for (LocalDate classDate : sortedDates) {
        // 해당 날짜가 속한 단위기간 번호 계산
        int unitNo = calculateUnitNo(firstDate, classDate, baseDay);

        // 해당 단위기간이 아직 생성되지 않았으면 생성
        if (!periodMap.containsKey(unitNo)) {
          UnitPeriodDTO unitPeriod = new UnitPeriodDTO();
          unitPeriod.setBootcampId(bootcamp.getId());
          unitPeriod.setUnitNo(unitNo);

          // 단위기간의 시작일과 종료일 계산
          LocalDate periodStart = firstDate.plusMonths(unitNo - 1);
          LocalDate periodEnd = periodStart.plusMonths(1).minusDays(1);

          unitPeriod.setStartDate(periodStart);
          unitPeriod.setEndDate(periodEnd);
          unitPeriodMapper.insert(unitPeriod);
          periodMap.put(unitNo, unitPeriod);
        }
      }

      // 세션 생성 (각 교육일을 해당 단위기간에 연결)
      List<SessionDTO> sessions = new ArrayList<>();
      for (LocalDate classDate : sortedDates) {
        int unitNo = calculateUnitNo(firstDate, classDate, baseDay);
        UnitPeriodDTO period = periodMap.get(unitNo);

        SessionDTO session = new SessionDTO();
        session.setBootcampId(bootcamp.getId());
        session.setPeriodId(period.getId());
        session.setClassDate(classDate);
        sessions.add(session);
      }
      sessionMapper.insertBatch(sessions);
    }
  }

  @Transactional
  public void deleteBootcamp(Long id) {
    // 연관된 세션 먼저 삭제
    sessionMapper.deleteByBootcampId(id);
    // 연관된 단위기간 삭제
    unitPeriodMapper.deleteByBootcampId(id);
    // 부트캠프 삭제
    bootcampMapper.delete(id);
  }
}


