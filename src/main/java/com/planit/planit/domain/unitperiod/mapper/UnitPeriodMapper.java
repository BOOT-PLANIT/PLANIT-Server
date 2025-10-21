package com.planit.planit.domain.unitperiod.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.planit.planit.domain.unitperiod.dto.UnitPeriodDTO;

@Mapper
public interface UnitPeriodMapper {
  List<UnitPeriodDTO> findAll();

  List<UnitPeriodDTO> findByBootcampId(Long bootcampId);

  UnitPeriodDTO findById(Long id);

  void insert(UnitPeriodDTO unitPeriod);

  void update(UnitPeriodDTO unitPeriod);

  void delete(Long id);

  void deleteByBootcampId(Long bootcampId);
}
