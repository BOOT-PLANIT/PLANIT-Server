package com.planit.planit.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.planit.planit.model.dto.UserDTO;

@Mapper
public interface UserMapper {
    List<UserDTO> findAll();
    UserDTO findById(Long id);
    void insert(UserDTO user);
    void update(UserDTO user);
    void delete(Long id);
}
