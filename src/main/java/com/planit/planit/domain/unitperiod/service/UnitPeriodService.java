package com.planit.planit.domain.unitperiod.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.planit.planit.domain.unitperiod.mapper.UnitPeriodMapper;
import com.planit.planit.domain.unitperiod.dto.UnitPeriodDTO;

@Service
public class UnitPeriodService {
    private final UnitPeriodMapper unitPeriodMapper;

    public UnitPeriodService(UnitPeriodMapper unitPeriodMapper) {
        this.unitPeriodMapper = unitPeriodMapper;
    }

    public List<UnitPeriodDTO> getAllUnitPeriods() {
        return unitPeriodMapper.findAll();
    }

    public List<UnitPeriodDTO> getUnitPeriodsByBootcampId(Long bootcampId) {
        return unitPeriodMapper.findByBootcampId(bootcampId);
    }

    public UnitPeriodDTO getUnitPeriod(Long id) {
        return unitPeriodMapper.findById(id);
    }

    public void addUnitPeriod(UnitPeriodDTO unitPeriod) {
        unitPeriodMapper.insert(unitPeriod);
    }

    public void updateUnitPeriod(UnitPeriodDTO unitPeriod) {
        unitPeriodMapper.update(unitPeriod);
    }

    public void deleteUnitPeriod(Long id) {
        unitPeriodMapper.delete(id);
    }
}
