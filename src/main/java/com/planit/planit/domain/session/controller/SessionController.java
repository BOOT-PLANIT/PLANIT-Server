package com.planit.planit.domain.session.controller;

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
import com.planit.planit.domain.session.dto.SessionDTO;
import com.planit.planit.domain.session.service.SessionService;
import com.planit.planit.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/sessions")
@Tag(name = "세션", description = "수업 날짜 관리 API")
public class SessionController {
  private final SessionService sessionService;

  public SessionController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Operation(summary = "세션 전체 목록 조회", description = "모든 세션(수업 날짜)을 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "조회 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @GetMapping
  public ApiResponse<List<SessionDTO>> getAll() {
    List<SessionDTO> sessions = sessionService.getAllSessions();
    return ApiResponse.response(HttpStatus.OK, "세션 목록 조회 성공", sessions);
  }

  @Operation(summary = "부트캠프별 세션 조회", description = "특정 부트캠프의 모든 세션을 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "조회 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @GetMapping("/bootcamp/{bootcampId}")
  public ApiResponse<List<SessionDTO>> getByBootcampId(@PathVariable Long bootcampId) {
    List<SessionDTO> sessions = sessionService.getSessionsByBootcampId(bootcampId);
    return ApiResponse.response(HttpStatus.OK, "부트캠프별 세션 조회 성공", sessions);
  }

  @Operation(summary = "세션 단건 조회", description = "ID로 특정 세션을 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "조회 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "세션을 찾을 수 없음"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @GetMapping("/{id}")
  public ApiResponse<SessionDTO> getOne(@PathVariable Long id) {
    SessionDTO session = sessionService.getSession(id);
    return ApiResponse.response(HttpStatus.OK, "세션 조회 성공", session);
  }

  @Operation(summary = "세션 등록", description = "새로운 세션을 등록합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
              description = "등록 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
              description = "잘못된 요청"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @PostMapping
  public ApiResponse<SessionDTO> add(@RequestBody SessionDTO session) {
    sessionService.addSession(session);
    return ApiResponse.response(HttpStatus.CREATED, "세션 등록 성공", session);
  }

  @Operation(summary = "세션 수정", description = "기존 세션 정보를 수정합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "수정 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "세션을 찾을 수 없음"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @PutMapping("/{id}")
  public ApiResponse<SessionDTO> update(@PathVariable Long id, @RequestBody SessionDTO session) {
    session.setId(id);
    sessionService.updateSession(session);
    return ApiResponse.response(HttpStatus.OK, "세션 수정 성공", session);
  }

  @Operation(summary = "세션 삭제", description = "세션을 삭제합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "삭제 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "세션을 찾을 수 없음"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    sessionService.deleteSession(id);
    return ApiResponse.response(HttpStatus.OK, "세션 삭제 성공");
  }
}


