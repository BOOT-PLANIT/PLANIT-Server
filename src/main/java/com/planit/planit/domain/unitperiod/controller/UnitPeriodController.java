package com.planit.planit.domain.unitperiod.controller;

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
import com.planit.planit.domain.unitperiod.dto.UnitPeriodDTO;
import com.planit.planit.domain.unitperiod.service.UnitPeriodService;
import com.planit.planit.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/unit-periods")
@Tag(name = "단위기간", description = "단위기간 관리 API")
public class UnitPeriodController {
  private final UnitPeriodService unitPeriodService;

  public UnitPeriodController(UnitPeriodService unitPeriodService) {
    this.unitPeriodService = unitPeriodService;
  }

  @Operation(summary = "단위기간 전체 목록 조회", description = "모든 단위기간을 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "조회 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @GetMapping
  public ApiResponse<List<UnitPeriodDTO>> getAll() {
    List<UnitPeriodDTO> unitPeriods = unitPeriodService.getAllUnitPeriods();
    return ApiResponse.response(HttpStatus.OK, "단위기간 목록 조회 성공", unitPeriods);
  }

  @Operation(summary = "부트캠프별 단위기간 조회", description = "특정 부트캠프의 모든 단위기간을 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "조회 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @GetMapping("/bootcamp/{bootcampId}")
  public ApiResponse<List<UnitPeriodDTO>> getByBootcampId(@PathVariable Long bootcampId) {
    List<UnitPeriodDTO> unitPeriods = unitPeriodService.getUnitPeriodsByBootcampId(bootcampId);
    return ApiResponse.response(HttpStatus.OK, "부트캠프별 단위기간 조회 성공", unitPeriods);
  }

  @Operation(summary = "단위기간 단건 조회", description = "ID로 특정 단위기간을 조회합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "조회 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "단위기간을 찾을 수 없음"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @GetMapping("/{id}")
  public ApiResponse<UnitPeriodDTO> getOne(@PathVariable Long id) {
    UnitPeriodDTO unitPeriod = unitPeriodService.getUnitPeriod(id);
    return ApiResponse.response(HttpStatus.OK, "단위기간 조회 성공", unitPeriod);
  }

  @Operation(summary = "단위기간 등록", description = "새로운 단위기간을 등록합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
              description = "등록 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
              description = "잘못된 요청"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @PostMapping
  public ApiResponse<UnitPeriodDTO> add(@RequestBody UnitPeriodDTO unitPeriod) {
    unitPeriodService.addUnitPeriod(unitPeriod);
    return ApiResponse.response(HttpStatus.CREATED, "단위기간 등록 성공", unitPeriod);
  }

  @Operation(summary = "단위기간 수정", description = "기존 단위기간 정보를 수정합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "수정 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "단위기간을 찾을 수 없음"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @PutMapping("/{id}")
  public ApiResponse<UnitPeriodDTO> update(@PathVariable Long id,
      @RequestBody UnitPeriodDTO unitPeriod) {
    unitPeriod.setId(id);
    unitPeriodService.updateUnitPeriod(unitPeriod);
    return ApiResponse.response(HttpStatus.OK, "단위기간 수정 성공", unitPeriod);
  }

  @Operation(summary = "단위기간 삭제", description = "단위기간을 삭제합니다.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
              description = "삭제 성공"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
              description = "단위기간을 찾을 수 없음"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
              description = "서버 에러")})
  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    unitPeriodService.deleteUnitPeriod(id);
    return ApiResponse.response(HttpStatus.OK, "단위기간 삭제 성공");
  }
}
