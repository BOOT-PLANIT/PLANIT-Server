package com.planit.planit.domain.bootcamp.mapper;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.planit.planit.domain.bootcamp.dto.BootcampDTO;

@Mapper
public interface BootcampMapper {
  List<BootcampDTO> findAll();

  List<BootcampDTO> findAllWithPagination(@Param("offset") int offset,
                                        @Param("limit") int limit);

  Long countAll();

  Long countActive();

  BootcampDTO findById(Long id);

  BootcampDTO findByIdForUpdate(Long id);

  void insert(BootcampDTO bootcamp);

	void update(BootcampDTO bootcamp);

	void updateDates(Long id, LocalDate startedAt, LocalDate endedAt);

	void delete(Long id);
}
