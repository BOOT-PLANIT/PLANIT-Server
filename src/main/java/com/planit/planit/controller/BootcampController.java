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

import com.planit.planit.model.dto.BootcampDTO;
import com.planit.planit.model.service.BootcampService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/bootcamps")
@Tag(name = "부트캠프", description = "부트캠프 관리 API")
public class BootcampController {
    private final BootcampService bootcampService;
    
    public BootcampController(BootcampService bootcampService) {
        this.bootcampService = bootcampService;
    }

    @Operation(
        summary = "부트캠프 전체 목록 조회", 
        description = "등록된 모든 부트캠프 목록을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
        }
    )
    @GetMapping
    public List<BootcampDTO> getAll() {
        return bootcampService.getAllBootcamps();
    }

    @Operation(
        summary = "부트캠프 단건 조회", 
        description = "ID로 특정 부트캠프를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "부트캠프를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
        }
    )
    @GetMapping("/{id}")
    public BootcampDTO getOne(@PathVariable Long id) {
        return bootcampService.getBootcamp(id);
    }

    @Operation(
        summary = "부트캠프 등록", 
        description = "새로운 부트캠프를 등록합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
        }
    )
    @PostMapping
    public String add(@RequestBody BootcampDTO bootcamp) {
        bootcampService.addBootcamp(bootcamp);
        return "Bootcamp inserted: " + bootcamp.getId();
    }

    @Operation(
        summary = "부트캠프 수정", 
        description = "기존 부트캠프 정보를 수정합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "부트캠프를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
        }
    )
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody BootcampDTO bootcamp) {
        bootcamp.setId(id);
        bootcampService.updateBootcamp(bootcamp);
        return "Bootcamp updated.";
    }

    @Operation(
        summary = "부트캠프 삭제", 
        description = "부트캠프를 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "부트캠프를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
        }
    )
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        bootcampService.deleteBootcamp(id);
        return "Bootcamp deleted.";
    }
}

