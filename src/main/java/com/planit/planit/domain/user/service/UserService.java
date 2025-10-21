package com.planit.planit.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.planit.planit.domain.user.dto.UserDTO;
import com.planit.planit.domain.user.mapper.UserMapper;



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
