package com.planit.planit.domain.session.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planit.planit.domain.bootcamp.service.BootcampService;
import com.planit.planit.domain.session.dto.SessionDTO;
import com.planit.planit.domain.session.dto.SessionCreateItemDTO;
import com.planit.planit.domain.session.dto.SessionDeleteRequestDTO;
import com.planit.planit.domain.session.exception.SessionBeforeBootcampStartException;
import com.planit.planit.domain.session.exception.SessionIsBootcampStartDateException;
import com.planit.planit.domain.session.exception.SessionNotFoundException;
import com.planit.planit.domain.session.exception.SessionEmptyDeleteListException;
import com.planit.planit.domain.session.exception.SessionDifferentBootcampException;
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
	public List<SessionDTO> addSession(com.planit.planit.domain.session.dto.SessionCreateRequestDTO request) {
		// 부트캠프 비관적 락으로 조회 (동시성 문제 방지)
		com.planit.planit.domain.bootcamp.dto.BootcampDTO bootcamp = 
			bootcampService.getBootcampForUpdate(request.getBootcampId());

		// 기준일 결정: startedAt이 설정되어 있으면 사용, 없으면 첫 번째 세션의 classDate를 기준으로 함
		LocalDate baseDate = (bootcamp.getStartedAt() != null) 
			? bootcamp.getStartedAt() 
			: request.getSessions().get(0).getClassDate();
		
		int baseDay = unitPeriodCalculator.getBaseDay(baseDate);

		List<SessionDTO> createdSessions = new java.util.ArrayList<>();

		for (SessionCreateItemDTO sessionItem : request.getSessions()) {
			// 부트캠프 시작일 이후인지 검증 (엄격한 검증)
			if (bootcamp.getStartedAt() != null && sessionItem.getClassDate().isBefore(bootcamp.getStartedAt())) {
				throw new SessionBeforeBootcampStartException(
					"세션 날짜(" + sessionItem.getClassDate() + ")는 부트캠프 시작일(" 
					+ bootcamp.getStartedAt() + ") 이후여야 합니다.");
			}

			// SessionDTO 생성
			SessionDTO session = new SessionDTO();
			session.setBootcampId(request.getBootcampId());
			session.setClassDate(sessionItem.getClassDate());

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

			// 생성된 세션 조회하여 리스트에 추가
			createdSessions.add(sessionMapper.findById(session.getId()));
		}

		// 부트캠프의 시작일/종료일 갱신
		bootcampService.updateBootcampDates(request.getBootcampId());

		return createdSessions;
	}


	@Transactional
	public void deleteSessions(SessionDeleteRequestDTO request) {
		// 중복 ID 제거
		List<Long> uniqueSessionIds = request.getSessionIds().stream()
			.distinct()
			.collect(java.util.stream.Collectors.toList());
		
		if (uniqueSessionIds.isEmpty()) {
			throw new SessionEmptyDeleteListException("삭제할 세션이 없습니다.");
		}

		// 모든 세션이 존재하는지 확인하고 부트캠프 ID 수집
		Long bootcampId = null;
		for (Long sessionId : uniqueSessionIds) {
			SessionDTO existingSession = sessionMapper.findById(sessionId);
			if (existingSession == null) {
				throw new SessionNotFoundException("ID가 " + sessionId + "인 세션을 찾을 수 없습니다.");
			}
			
			// 첫 번째 세션에서 부트캠프 ID 설정
			if (bootcampId == null) {
				bootcampId = existingSession.getBootcampId();
			} else {
				// 서로 다른 부트캠프의 세션이 섞여있는지 검증
				if (!bootcampId.equals(existingSession.getBootcampId())) {
					throw new SessionDifferentBootcampException(
						"서로 다른 부트캠프의 세션들을 함께 삭제할 수 없습니다. " +
						"세션 ID " + sessionId + "는 부트캠프 ID " + existingSession.getBootcampId() + 
						"에 속하지만, 다른 세션들은 부트캠프 ID " + bootcampId + "에 속합니다.");
				}
			}
		}

		// 부트캠프 조회하여 시작일 확인
		com.planit.planit.domain.bootcamp.dto.BootcampDTO bootcamp = 
			bootcampService.getBootcampForUpdate(bootcampId);

		// 각 세션에 대해 부트캠프 시작일 검증
		for (Long sessionId : uniqueSessionIds) {
			SessionDTO existingSession = sessionMapper.findById(sessionId);
			
			// 부트캠프 시작일에 해당하는 세션인지 확인
			if (bootcamp.getStartedAt() != null && 
				existingSession.getClassDate().equals(bootcamp.getStartedAt())) {
				throw new SessionIsBootcampStartDateException(
					"부트캠프 시작일(" + bootcamp.getStartedAt() + ")에 해당하는 세션은 삭제할 수 없습니다.");
			}
		}

		// 모든 세션 삭제
		for (Long sessionId : uniqueSessionIds) {
			sessionMapper.delete(sessionId);
		}

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
