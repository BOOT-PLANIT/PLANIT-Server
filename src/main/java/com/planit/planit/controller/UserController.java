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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/test")
/////////////////TODO 5. Controller  설명 설정
@Tag(name = "테스트용", description = "테스트용 유저 확인")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

	/////////////////TODO 6. Controller 함수 설명 설정
	@Operation(	summary = "유저 전체 목록 확인", 
			description = "테스트용 유저 전체 목록 확인",
			responses ={
				@ApiResponse(responseCode = "200", description = "목록 성공"),
				@ApiResponse(responseCode = "404", description = "전체목록확인에러!!"),
				@ApiResponse(responseCode = "500", description = "서버에러!!")
			}
	)
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
