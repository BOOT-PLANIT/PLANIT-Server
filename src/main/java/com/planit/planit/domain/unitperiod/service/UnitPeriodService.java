package com.planit.planit.domain.unitperiod.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planit.planit.domain.bootcamp.service.BootcampService;
import com.planit.planit.domain.unitperiod.dto.UnitPeriodDTO;
import com.planit.planit.domain.unitperiod.exception.UnitPeriodDatesRequiredException;
import com.planit.planit.domain.unitperiod.exception.UnitPeriodNotFoundException;
import com.planit.planit.domain.unitperiod.mapper.UnitPeriodMapper;

@Service
public class UnitPeriodService {
	private final UnitPeriodMapper unitPeriodMapper;
	private final BootcampService bootcampService;

	public UnitPeriodService(UnitPeriodMapper unitPeriodMapper, BootcampService bootcampService) {
		this.unitPeriodMapper = unitPeriodMapper;
		this.bootcampService = bootcampService;
	}

	public List<UnitPeriodDTO> getAllUnitPeriods() {
		return unitPeriodMapper.findAll();
	}

	public List<UnitPeriodDTO> getUnitPeriodsByBootcampId(Long bootcampId) {
		// 부트캠프 존재 여부 검증 (존재하지 않으면 BootcampNotFoundException 발생)
		bootcampService.getBootcamp(bootcampId);
		return unitPeriodMapper.findByBootcampId(bootcampId);
	}

	public UnitPeriodDTO getUnitPeriod(Long id) {
		UnitPeriodDTO unitPeriod = unitPeriodMapper.findById(id);
		if (unitPeriod == null) {
			throw new UnitPeriodNotFoundException("ID가 " + id + "인 단위기간을 찾을 수 없습니다.");
		}
		return unitPeriod;
	}

	@Transactional
	public void addUnitPeriod(UnitPeriodDTO unitPeriod) {
		// 부트캠프 존재 여부 검증 (존재하지 않으면 BootcampNotFoundException 발생)
		bootcampService.getBootcamp(unitPeriod.getBootcampId());
		unitPeriodMapper.insert(unitPeriod);
	}

	/**
	 * 단위기간을 찾거나 생성합니다.
	 *
	 * @param unitPeriod 단위기간 정보 (bootcampId, unitNo 필수, startDate/endDate 선택)
	 * @return 생성되거나 찾은 단위기간의 ID
	 */
	@Transactional
	public Long findOrCreateUnitPeriod(UnitPeriodDTO unitPeriod) {
		// 부트캠프 존재 여부 검증
		bootcampService.getBootcamp(unitPeriod.getBootcampId());

		// 기존 단위기간 조회
		UnitPeriodDTO existingPeriod =
			unitPeriodMapper.findByBootcampIdAndUnitNo(unitPeriod.getBootcampId(), unitPeriod.getUnitNo());

		if (existingPeriod != null) {
			// 기존 단위기간이 있으면 해당 ID 반환
			return existingPeriod.getId();
		} else {
			// 없으면 새로 생성
			// startDate와 endDate가 제공되지 않으면 오류
			if (unitPeriod.getStartDate() == null || unitPeriod.getEndDate() == null) {
				throw new UnitPeriodDatesRequiredException(
					"새로운 단위기간을 생성하려면 시작일(periodStartDate)과 종료일(periodEndDate)이 필요합니다.");
			}
			unitPeriodMapper.insert(unitPeriod);
			return unitPeriod.getId();
		}
	}

	@Transactional
	public void updateUnitPeriod(UnitPeriodDTO unitPeriod) {
		UnitPeriodDTO existingUnitPeriod = unitPeriodMapper.findById(unitPeriod.getId());
		if (existingUnitPeriod == null) {
			throw new UnitPeriodNotFoundException("ID가 " + unitPeriod.getId() + "인 단위기간을 찾을 수 없습니다.");
		}
		// 부트캠프 존재 여부 검증 (존재하지 않으면 BootcampNotFoundException 발생)
		bootcampService.getBootcamp(unitPeriod.getBootcampId());
		unitPeriodMapper.update(unitPeriod);
	}

	@Transactional
	public void deleteUnitPeriod(Long id) {
		UnitPeriodDTO existingUnitPeriod = unitPeriodMapper.findById(id);
		if (existingUnitPeriod == null) {
			throw new UnitPeriodNotFoundException("ID가 " + id + "인 단위기간을 찾을 수 없습니다.");
		}
		unitPeriodMapper.delete(id);
	}
}
