package com.planit.planit.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.planit.planit.model.dao.UserMapper;
import com.planit.planit.model.dto.UserDTO;

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public List<UserDTO> getAllUsers() {
        return userMapper.findAll();
    }

    public UserDTO getUser(Long id) {
        return userMapper.findById(id);
    }

    public void addUser(UserDTO user) {
        userMapper.insert(user);
    }

    public void updateUser(UserDTO user) {
        userMapper.update(user);
    }

    public void deleteUser(Long id) {
        userMapper.delete(id);
    }
}
