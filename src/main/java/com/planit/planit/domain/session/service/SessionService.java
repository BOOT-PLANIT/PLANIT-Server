package com.planit.planit.domain.session.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planit.planit.domain.bootcamp.service.BootcampService;
import com.planit.planit.domain.session.dto.SessionDTO;
import com.planit.planit.domain.session.exception.SessionNotFoundException;
import com.planit.planit.domain.session.mapper.SessionMapper;
import com.planit.planit.domain.unitperiod.dto.UnitPeriodDTO;
import com.planit.planit.domain.unitperiod.service.UnitPeriodService;

@Service
public class SessionService {
  private final SessionMapper sessionMapper;
  private final BootcampService bootcampService;
  private final UnitPeriodService unitPeriodService;

  public SessionService(SessionMapper sessionMapper, BootcampService bootcampService,
      UnitPeriodService unitPeriodService) {
    this.sessionMapper = sessionMapper;
    this.bootcampService = bootcampService;
    this.unitPeriodService = unitPeriodService;
  }

  public List<SessionDTO> getAllSessions() {
    return sessionMapper.findAll();
  }

  public List<SessionDTO> getSessionsByBootcampId(Long bootcampId) {
    // 부트캠프 존재 여부 검증 (존재하지 않으면 BootcampNotFoundException 발생)
    bootcampService.getBootcamp(bootcampId);
    return sessionMapper.findByBootcampId(bootcampId);
  }

  public SessionDTO getSession(Long id) {
    SessionDTO session = sessionMapper.findById(id);
    if (session == null) {
      throw new SessionNotFoundException("ID가 " + id + "인 세션을 찾을 수 없습니다.");
    }
    return session;
  }

  @Transactional
  public void addSession(SessionDTO session) {
    // 부트캠프 존재 여부 검증 (존재하지 않으면 BootcampNotFoundException 발생)
    bootcampService.getBootcamp(session.getBootcampId());

    // 단위기간 정보 검증
    if (session.getUnitNo() == null) {
      throw new IllegalArgumentException("단위기간 번호(unitNo)는 필수입니다.");
    }

    // 단위기간 찾거나 생성
    UnitPeriodDTO unitPeriod = new UnitPeriodDTO();
    unitPeriod.setBootcampId(session.getBootcampId());
    unitPeriod.setUnitNo(session.getUnitNo());
    unitPeriod.setStartDate(session.getPeriodStartDate());
    unitPeriod.setEndDate(session.getPeriodEndDate());

    Long periodId = unitPeriodService.findOrCreateUnitPeriod(unitPeriod);
    session.setPeriodId(periodId);

    sessionMapper.insert(session);

    // 부트캠프의 시작일/종료일 갱신
    bootcampService.updateBootcampDates(session.getBootcampId());
  }

  @Transactional
  public void updateSession(SessionDTO session) {
    SessionDTO existingSession = sessionMapper.findById(session.getId());
    if (existingSession == null) {
      throw new SessionNotFoundException("ID가 " + session.getId() + "인 세션을 찾을 수 없습니다.");
    }
    // 부트캠프 존재 여부 검증 (존재하지 않으면 BootcampNotFoundException 발생)
    bootcampService.getBootcamp(session.getBootcampId());

    // 단위기간 찾거나 생성 (unitNo가 제공된 경우)
    if (session.getUnitNo() != null) {
      UnitPeriodDTO unitPeriod = new UnitPeriodDTO();
      unitPeriod.setBootcampId(session.getBootcampId());
      unitPeriod.setUnitNo(session.getUnitNo());
      unitPeriod.setStartDate(session.getPeriodStartDate());
      unitPeriod.setEndDate(session.getPeriodEndDate());

      Long periodId = unitPeriodService.findOrCreateUnitPeriod(unitPeriod);
      session.setPeriodId(periodId);
    }

    sessionMapper.update(session);

    // 부트캠프의 시작일/종료일 갱신 (classDate가 변경될 수 있으므로)
    bootcampService.updateBootcampDates(session.getBootcampId());
  }

  @Transactional
  public void deleteSession(Long id) {
    SessionDTO existingSession = sessionMapper.findById(id);
    if (existingSession == null) {
      throw new SessionNotFoundException("ID가 " + id + "인 세션을 찾을 수 없습니다.");
    }
    
    Long bootcampId = existingSession.getBootcampId();
    sessionMapper.delete(id);

    // 부트캠프의 시작일/종료일 갱신
    bootcampService.updateBootcampDates(bootcampId);
  }
}
