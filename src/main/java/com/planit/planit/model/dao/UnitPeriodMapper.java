package com.planit.planit.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import com.planit.planit.model.dto.UnitPeriodDTO;

@Mapper
public interface UnitPeriodMapper {
    List<UnitPeriodDTO> findAll();
    List<UnitPeriodDTO> findByBootcampId(@Param("bootcampId") Long bootcampId);
    UnitPeriodDTO findById(@Param("id") Long id);
    void insert(UnitPeriodDTO unitPeriod);
    int update(UnitPeriodDTO unitPeriod);
    int delete(@Param("id") Long id);
    int deleteByBootcampId(@Param("bootcampId") Long bootcampId);
}

