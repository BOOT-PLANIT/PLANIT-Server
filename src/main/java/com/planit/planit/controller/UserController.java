package com.planit.planit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planit.planit.model.dto.UserDTO;
import com.planit.planit.model.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getOne(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    public String add(@RequestBody UserDTO user) {
        userService.addUser(user);
        return "User inserted: " + user.getId();
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody UserDTO user) {
        user.setId(id);
        userService.updateUser(user);
        return "User updated.";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User deleted.";
    }
}
