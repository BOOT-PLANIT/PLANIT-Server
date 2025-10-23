package com.planit.planit.domain.session.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planit.planit.domain.bootcamp.service.BootcampService;
import com.planit.planit.domain.session.dto.SessionDTO;
import com.planit.planit.domain.session.exception.SessionBeforeBootcampStartException;
import com.planit.planit.domain.session.exception.SessionIsBootcampStartDateException;
import com.planit.planit.domain.session.exception.SessionNotFoundException;
import com.planit.planit.domain.session.mapper.SessionMapper;
import com.planit.planit.domain.unitperiod.dto.UnitPeriodDTO;
import com.planit.planit.domain.unitperiod.service.UnitPeriodService;

@Service
public class SessionService {
	private final SessionMapper sessionMapper;
	private final BootcampService bootcampService;
	private final UnitPeriodService unitPeriodService;
	private final com.planit.planit.domain.unitperiod.util.UnitPeriodCalculator unitPeriodCalculator;

	public SessionService(SessionMapper sessionMapper, BootcampService bootcampService,
		UnitPeriodService unitPeriodService,
		com.planit.planit.domain.unitperiod.util.UnitPeriodCalculator unitPeriodCalculator) {
		this.sessionMapper = sessionMapper;
		this.bootcampService = bootcampService;
		this.unitPeriodService = unitPeriodService;
		this.unitPeriodCalculator = unitPeriodCalculator;
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
	public SessionDTO addSession(com.planit.planit.domain.session.dto.SessionCreateRequestDTO request) {
		// 부트캠프 비관적 락으로 조회 (동시성 문제 방지)
		com.planit.planit.domain.bootcamp.dto.BootcampDTO bootcamp = 
			bootcampService.getBootcampForUpdate(request.getBootcampId());

		// 부트캠프 시작일 이후인지 검증 (엄격한 검증)
		if (bootcamp.getStartedAt() != null && request.getClassDate().isBefore(bootcamp.getStartedAt())) {
			throw new SessionBeforeBootcampStartException(
				"세션 날짜(" + request.getClassDate() + ")는 부트캠프 시작일(" 
				+ bootcamp.getStartedAt() + ") 이후여야 합니다.");
		}

		// SessionDTO 생성
		SessionDTO session = new SessionDTO();
		session.setBootcampId(request.getBootcampId());
		session.setClassDate(request.getClassDate());

		// 기준일 결정: startedAt이 설정되어 있으면 사용, 없으면 현재 classDate를 기준으로 함
		LocalDate baseDate = (bootcamp.getStartedAt() != null) 
			? bootcamp.getStartedAt() 
			: request.getClassDate();
		
		int baseDay = unitPeriodCalculator.getBaseDay(baseDate);

		// unitNo 자동 계산
		calculateUnitNo(session, baseDate, baseDay);

		// periodStartDate와 periodEndDate 자동 계산
		calculatePeriodDates(session, baseDate, baseDay);

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

		// 생성된 세션 조회하여 반환
		return sessionMapper.findById(session.getId());
	}

	@Transactional
	public void deleteSession(Long id) {
		SessionDTO existingSession = sessionMapper.findById(id);
		if (existingSession == null) {
			throw new SessionNotFoundException("ID가 " + id + "인 세션을 찾을 수 없습니다.");
		}

		Long bootcampId = existingSession.getBootcampId();

		// 부트캠프 조회하여 시작일 확인
		com.planit.planit.domain.bootcamp.dto.BootcampDTO bootcamp = 
			bootcampService.getBootcampForUpdate(bootcampId);

		// 부트캠프 시작일에 해당하는 세션인지 확인
		if (bootcamp.getStartedAt() != null && 
			existingSession.getClassDate().equals(bootcamp.getStartedAt())) {
			throw new SessionIsBootcampStartDateException(
				"부트캠프 시작일(" + bootcamp.getStartedAt() + ")에 해당하는 세션은 삭제할 수 없습니다.");
		}

		sessionMapper.delete(id);

		// 부트캠프의 시작일/종료일 갱신
		bootcampService.updateBootcampDates(bootcampId);
	}

	/**
	 * 단위기간 번호를 자동으로 계산합니다.
	 * 
	 * @param session 세션 정보 (classDate 필요)
	 * @param baseDate 기준일
	 * @param baseDay 기준일의 일(day)
	 */
	private void calculateUnitNo(SessionDTO session, LocalDate baseDate, int baseDay) {
		// unitNo 계산 (공통 유틸리티 사용)
		int unitNo = unitPeriodCalculator.calculateUnitNo(baseDate, session.getClassDate(), baseDay);
		session.setUnitNo(unitNo);
	}

	/**
	 * 단위기간의 시작일/종료일을 자동으로 계산합니다.
	 * 
	 * @param session 세션 정보 (unitNo 필요)
	 * @param baseDate 기준일
	 * @param baseDay 기준일의 일(day)
	 */
	private void calculatePeriodDates(SessionDTO session, LocalDate baseDate, int baseDay) {
		// 단위기간 시작일/종료일 계산 (공통 유틸리티 사용)
		LocalDate periodStart = unitPeriodCalculator.calculatePeriodStartDate(baseDate, session.getUnitNo(), baseDay);
		LocalDate periodEnd = unitPeriodCalculator.calculatePeriodEndDate(baseDate, session.getUnitNo(), baseDay);

		session.setPeriodStartDate(periodStart);
		session.setPeriodEndDate(periodEnd);
	}
}
