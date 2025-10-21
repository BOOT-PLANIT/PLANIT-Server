package com.planit.planit.domain.bootcamp.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.planit.planit.domain.bootcamp.dto.BootcampDTO;
import com.planit.planit.domain.bootcamp.service.BootcampService;
import com.planit.planit.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bootcamps")
@Tag(name = "부트캠프", description = "부트캠프 관리 API")
public class BootcampController {
  private final BootcampService bootcampService;

  public BootcampController(BootcampService bootcampService) {
    this.bootcampService = bootcampService;
  }

  @Operation(summary = "부트캠프 전체 목록 조회", description = "등록된 모든 부트캠프 목록을 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "조회 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @GetMapping
  public ApiResponse<List<BootcampDTO>> getAll() {
    List<BootcampDTO> bootcamps = bootcampService.getAllBootcamps();
    return ApiResponse.response(HttpStatus.OK, "부트캠프 목록 조회 성공", bootcamps);
  }

  @Operation(summary = "부트캠프 단건 조회", description = "ID로 특정 부트캠프를 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "조회 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "부트캠프를 찾을 수 없음"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @GetMapping("/{id}")
  public ApiResponse<BootcampDTO> getOne(@PathVariable Long id) {
    BootcampDTO bootcamp = bootcampService.getBootcamp(id);
    return ApiResponse.response(HttpStatus.OK, "부트캠프 조회 성공", bootcamp);
  }

  @Operation(summary = "부트캠프 등록", description = "새로운 부트캠프를 등록합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
              description = "등록 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
              description = "잘못된 요청"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @PostMapping
  public ApiResponse<BootcampDTO> add(@Valid @RequestBody BootcampDTO bootcamp) {
    bootcampService.addBootcamp(bootcamp);
    return ApiResponse.response(HttpStatus.CREATED, "부트캠프 등록 성공", bootcamp);
  }

  @Operation(summary = "부트캠프 수정", description = "기존 부트캠프 정보를 수정합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "수정 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "부트캠프를 찾을 수 없음"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @PutMapping("/{id}")
  public ApiResponse<BootcampDTO> update(@PathVariable Long id,
      @Valid @RequestBody BootcampDTO bootcamp) {
    bootcamp.setId(id);
    bootcampService.updateBootcamp(bootcamp);
    return ApiResponse.response(HttpStatus.OK, "부트캠프 수정 성공", bootcamp);
  }

  @Operation(summary = "부트캠프 삭제", description = "부트캠프를 삭제합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "삭제 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "부트캠프를 찾을 수 없음"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    bootcampService.deleteBootcamp(id);
    return ApiResponse.response(HttpStatus.OK, "부트캠프 삭제 성공");
  }
}

