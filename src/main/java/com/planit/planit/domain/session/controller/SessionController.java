package com.planit.planit.domain.session.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.planit.planit.domain.session.dto.SessionCreateRequestDTO;
import com.planit.planit.domain.session.dto.SessionDeleteRequestDTO;
import com.planit.planit.domain.session.dto.SessionDTO;
import com.planit.planit.domain.session.service.SessionService;
import com.planit.planit.global.common.response.ApiResponse;
import com.planit.planit.global.common.response.ErrorDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/sessions")
@Tag(name = "세션", description = "세션 관리 API")
public class SessionController {

  private final SessionService sessionService;

  public SessionController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Operation(summary = "세션 전체 목록 조회", description = "등록된 모든 세션 목록을 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "조회 성공",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = SessionListResponseSchema.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiErrorResponseSchema.class)))})
  @GetMapping
  public ResponseEntity<ApiResponse<List<SessionDTO>>> getAll() {
    List<SessionDTO> sessions = sessionService.getAllSessions();
    return ResponseEntity.ok(ApiResponse.success("세션 목록 조회 성공", sessions));
  }

  @Operation(summary = "부트캠프별 세션 목록 조회", description = "특정 부트캠프의 모든 세션 목록을 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "조회 성공",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = SessionListResponseSchema.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "부트캠프를 찾을 수 없음",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiErrorResponseSchema.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiErrorResponseSchema.class)))})
  @GetMapping("/bootcamp/{bootcampId}")
  public ResponseEntity<ApiResponse<List<SessionDTO>>> getByBootcampId(
      @PathVariable Long bootcampId) {
    List<SessionDTO> sessions = sessionService.getSessionsByBootcampId(bootcampId);
    return ResponseEntity.ok(ApiResponse.success("부트캠프별 세션 목록 조회 성공", sessions));
  }

  @Operation(summary = "세션 단건 조회", description = "ID로 특정 세션을 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "조회 성공",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = SessionOneResponseSchema.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "세션을 찾을 수 없음",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiErrorResponseSchema.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiErrorResponseSchema.class)))})
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<SessionDTO>> getOne(@PathVariable Long id) {
    SessionDTO session = sessionService.getSession(id);
    return ResponseEntity.ok(ApiResponse.success("세션 조회 성공", session));
  }

	@Operation(summary = "세션 등록", 
		description = "여러 세션을 한번에 등록합니다. "
			+ "**필수 필드**: bootcampId, sessions[].classDate. "
			+ "unitNo, periodStartDate, periodEndDate는 자동으로 계산됩니다. "
			+ "단위기간이 존재하지 않으면 자동으로 생성됩니다.",
		responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
              description = "등록 성공",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = SessionListResponseSchema.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
              description = "잘못된 요청",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiErrorResponseSchema.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiErrorResponseSchema.class)))})
  @PostMapping
  public ResponseEntity<ApiResponse<List<SessionDTO>>> add(@Valid @RequestBody SessionCreateRequestDTO request) {
    List<SessionDTO> createdSessions = sessionService.addSession(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("세션 등록 성공", createdSessions));
  }


  @Operation(summary = "세션 삭제", 
      description = "여러 세션을 한번에 삭제합니다. 단, 부트캠프의 시작일에 해당하는 세션은 삭제할 수 없습니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "삭제 성공",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = VoidResponseSchema.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
              description = "부트캠프 시작일 세션은 삭제 불가",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiErrorResponseSchema.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "세션을 찾을 수 없음",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiErrorResponseSchema.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiErrorResponseSchema.class)))})
  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> delete(@Valid @RequestBody SessionDeleteRequestDTO request) {
    sessionService.deleteSessions(request);
    return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "세션 삭제 성공", null));
  }

  @Schema(name = "SessionListResponse", description = "세션 목록 응답")
  static class SessionListResponseSchema {
    @Schema(example = "200")
    public int code;
    @Schema(example = "세션 목록 조회 성공")
    public String message;
    public List<SessionDTO> data;
  }

  @Schema(name = "SessionOneResponse", description = "세션 단건 응답")
  static class SessionOneResponseSchema {
    @Schema(example = "200")
    public int code;
    @Schema(example = "세션 조회 성공")
    public String message;
    public SessionDTO data;
  }

  @Schema(name = "VoidResponse", description = "데이터 없는 성공 응답")
  static class VoidResponseSchema {
    @Schema(example = "200")
    public int code;
    @Schema(example = "세션 삭제 성공")
    public String message;
    public Void data;
  }

  @Schema(name = "ApiErrorResponse", description = "실패 응답(에러)")
  static class ApiErrorResponseSchema {
    @Schema(example = "400")
    public int code;
    @Schema(example = "잘못된 [인자]입니다.")
    public String message;
    public List<ErrorDetail> errors;
  }
}

