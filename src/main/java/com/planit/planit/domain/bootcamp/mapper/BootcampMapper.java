package com.planit.planit.domain.bootcamp.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.planit.planit.domain.bootcamp.dto.BootcampDTO;

@Mapper
public interface BootcampMapper {
  List<BootcampDTO> findAll();

  BootcampDTO findById(Long id);

  void insert(BootcampDTO bootcamp);

  void update(BootcampDTO bootcamp);

  void delete(Long id);
}
