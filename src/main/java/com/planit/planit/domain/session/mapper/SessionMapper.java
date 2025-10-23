package com.planit.planit.domain.session.mapper;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.planit.planit.domain.session.dto.SessionDTO;

@Mapper
public interface SessionMapper {
    List<SessionDTO> findAll();

    List<SessionDTO> findByBootcampId(Long bootcampId);

    SessionDTO findById(Long id);

    LocalDate findMinClassDateByBootcampIdForUpdate(Long bootcampId);

    void insert(SessionDTO session);

    void insertBatch(List<SessionDTO> sessions);

    void delete(Long id);

    void deleteByBootcampId(Long bootcampId);
}