package com.planit.planit.domain.bootcamp.controller;

import java.util.List;

import com.planit.planit.domain.bootcamp.dto.BootcampRequestDTO;
import com.planit.planit.domain.bootcamp.dto.BootcampResponseDTO;
import com.planit.planit.domain.bootcamp.service.BootcampService;
import com.planit.planit.global.common.response.ApiResponse;
import com.planit.planit.global.common.response.ErrorDetail;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;

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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BootcampListResponseSchema.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "서버 에러",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponseSchema.class)
                )
            )
        }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<BootcampResponseDTO>>> getAll() {
        List<BootcampResponseDTO> bootcamps = bootcampService.getAllBootcamps();
        return ResponseEntity.ok(
            ApiResponse.success("부트캠프 목록 조회 성공", bootcamps)
        );
    }

    @Operation(
        summary = "부트캠프 단건 조회",
        description = "ID로 특정 부트캠프를 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BootcampOneResponseSchema.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "부트캠프를 찾을 수 없음",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponseSchema.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "서버 에러",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponseSchema.class)
                )
            )
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BootcampResponseDTO>> getOne(@PathVariable Long id) {
        BootcampResponseDTO bootcamp = bootcampService.getBootcamp(id);
        return ResponseEntity.ok(
            ApiResponse.success("부트캠프 조회 성공", bootcamp)
        );
    }

    @Operation(
        summary = "부트캠프 등록",
        description = "새로운 부트캠프를 등록합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BootcampOneResponseSchema.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponseSchema.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "서버 에러",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponseSchema.class)
                )
            )
        }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<BootcampResponseDTO>> add(@Valid @RequestBody BootcampRequestDTO request) {
        BootcampResponseDTO bootcamp = bootcampService.addBootcamp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success("부트캠프 등록 성공", bootcamp)
        );
    }

    @Operation(
        summary = "부트캠프 수정",
        description = "기존 부트캠프 정보를 수정합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BootcampOneResponseSchema.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "부트캠프를 찾을 수 없음",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponseSchema.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "서버 에러",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponseSchema.class)
                )
            )
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BootcampResponseDTO>> update(
        @PathVariable Long id,
        @Valid @RequestBody BootcampRequestDTO request
    ) {
        BootcampResponseDTO bootcamp = bootcampService.updateBootcamp(id, request);
        return ResponseEntity.ok(
            ApiResponse.success("부트캠프 수정 성공", bootcamp)
        );
    }

    @Operation(
        summary = "부트캠프 삭제",
        description = "부트캠프를 삭제합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "삭제 성공",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = VoidResponseSchema.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "부트캠프를 찾을 수 없음",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponseSchema.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "서버 에러",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponseSchema.class)
                )
            )
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bootcampService.deleteBootcamp(id);
        return ResponseEntity.ok(
            new ApiResponse<>(HttpStatus.OK.value(), "부트캠프 삭제 성공", null)
        );
    }

    @Schema(name = "BootcampListResponse", description = "부트캠프 목록 응답")
    static class BootcampListResponseSchema {
        @Schema(example = "200") public int code;
        @Schema(example = "부트캠프 목록 조회 성공") public String message;
        public List<BootcampResponseDTO> data;
    }

    @Schema(name = "BootcampOneResponse", description = "부트캠프 단건 응답")
    static class BootcampOneResponseSchema {
        @Schema(example = "200") public int code;
        @Schema(example = "부트캠프 조회 성공") public String message;
        public BootcampResponseDTO data;
    }

    @Schema(name = "VoidResponse", description = "데이터 없는 성공 응답")
    static class VoidResponseSchema {
        @Schema(example = "200") public int code;
        @Schema(example = "부트캠프 삭제 성공") public String message;
        public Void data;
    }

    @Schema(name = "ApiErrorResponse", description = "실패 응답(에러)")
    static class ApiErrorResponseSchema {
        @Schema(example = "400") public int code;
        @Schema(example = "잘못된 [인자]입니다.") public String message;
        public List<ErrorDetail> errors;
    }
}
