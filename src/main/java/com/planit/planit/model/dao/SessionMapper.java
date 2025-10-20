package com.planit.planit.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.planit.planit.model.dto.SessionDTO;

@Mapper
public interface SessionMapper {
    List<SessionDTO> findAll();
    List<SessionDTO> findByBootcampId(Long bootcampId);
    SessionDTO findById(Long id);
    void insert(SessionDTO session);
    void insertBatch(List<SessionDTO> sessions);
    void update(SessionDTO session);
    void delete(Long id);
    void deleteByBootcampId(Long bootcampId);
}

