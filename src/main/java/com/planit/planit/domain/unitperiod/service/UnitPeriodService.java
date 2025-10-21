package com.planit.planit.domain.unitperiod.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.planit.planit.domain.unitperiod.dto.UnitPeriodDTO;
import com.planit.planit.domain.unitperiod.exception.UnitPeriodNotFoundException;
import com.planit.planit.domain.unitperiod.mapper.UnitPeriodMapper;

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
    UnitPeriodDTO unitPeriod = unitPeriodMapper.findById(id);
    if (unitPeriod == null) {
      throw new UnitPeriodNotFoundException("ID가 " + id + "인 단위기간을 찾을 수 없습니다.");
    }
    return unitPeriod;
  }

  public void addUnitPeriod(UnitPeriodDTO unitPeriod) {
    unitPeriodMapper.insert(unitPeriod);
  }

  public void updateUnitPeriod(UnitPeriodDTO unitPeriod) {
    UnitPeriodDTO existingUnitPeriod = unitPeriodMapper.findById(unitPeriod.getId());
    if (existingUnitPeriod == null) {
      throw new UnitPeriodNotFoundException("ID가 " + unitPeriod.getId() + "인 단위기간을 찾을 수 없습니다.");
    }
    unitPeriodMapper.update(unitPeriod);
  }

  public void deleteUnitPeriod(Long id) {
    UnitPeriodDTO existingUnitPeriod = unitPeriodMapper.findById(id);
    if (existingUnitPeriod == null) {
      throw new UnitPeriodNotFoundException("ID가 " + id + "인 단위기간을 찾을 수 없습니다.");
    }
    unitPeriodMapper.delete(id);
  }
}
