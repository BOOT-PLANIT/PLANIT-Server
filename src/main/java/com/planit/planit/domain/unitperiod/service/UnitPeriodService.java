package com.planit.planit.domain.unitperiod.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planit.planit.domain.bootcamp.service.BootcampService;
import com.planit.planit.domain.unitperiod.dto.UnitPeriodDTO;
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