package com.planit.planit.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

////////////////TODO 4. DTO 설정
@Data
public class UserDTO {
	@Schema(description = "테스트 유저 아이디" , example = "1")
    private Long id;
	@Schema(description = "테스트 유저 이름" , example = "김현우")
    private String name;
	@Schema(description = "테스트 유저 이메일" , example = "hyunwoo@example.com")
    private String email;

//    public UserDTO() {}
//
//    public UserDTO(Long id, String name, String email) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//    }
//
//    // getter, setter
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
}
