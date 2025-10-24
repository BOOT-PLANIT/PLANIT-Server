package com.planit.planit.domain.bootcamp.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import com.planit.planit.domain.bootcamp.dto.BootcampParseResponseDTO;
import com.planit.planit.domain.bootcamp.exception.BootcampParseFailedException;

@Component
public class BootcampParser {

  /**
   * 고용24에서 복사한 텍스트를 파싱하여 부트캠프 정보를 추출합니다.
   * 
   * @param text 고용24에서 복사한 텍스트
   * @return 파싱된 부트캠프 정보
   * @throws BootcampParseFailedException 파싱 실패 시
   */
  public BootcampParseResponseDTO parse(String text) {
    // 훈련과정명 추출 (탭으로 구분, 쉼표 제외)
    Pattern coursePattern = Pattern.compile("훈련과정명\\t([^\\t,\\n]+)\\t훈련기간");
    Matcher courseMatcher = coursePattern.matcher(text);
    String courseName = null;
    while (courseMatcher.find()) {
      String match = courseMatcher.group(1).trim();
      // 유효한 값만 저장 (공백이 아니고 최소 2글자 이상)
      if (!match.isEmpty() && match.length() > 1) {
        courseName = match;
        break;
      }
    }

    // 훈련기관명 추출 (탭으로 구분, 쉼표 제외)
    Pattern instPattern = Pattern.compile("훈련기관명\\t([^\\t,\\n]+)\\t훈련일수");
    Matcher instMatcher = instPattern.matcher(text);
    String institution = null;
    while (instMatcher.find()) {
      String match = instMatcher.group(1).trim();
      // 유효한 값만 저장 (공백이 아니고 최소 2글자 이상)
      if (!match.isEmpty() && match.length() > 1) {
        institution = match;
        break;
      }
    }

    // 출석 일자 추출 (시간표에서 날짜만 추출)
    Pattern attendanceDatePattern =
        Pattern.compile("(\\d{4}-\\d{2}-\\d{2})[\\s\\t]+\\d{2}:\\d{2}\\s*~\\s*\\d{2}:\\d{2}");
    Matcher dateMatcher = attendanceDatePattern.matcher(text);
    List<LocalDate> attendanceDates = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    while (dateMatcher.find()) {
      String dateStr = dateMatcher.group(1);
      try {
        LocalDate date = LocalDate.parse(dateStr, formatter);
        // 중복 방지
        if (!attendanceDates.contains(date)) {
          attendanceDates.add(date);
        }
      } catch (Exception e) {
        // 파싱 실패 시 무시
      }
    }

    // 날짜 정렬
    attendanceDates.sort(LocalDate::compareTo);

    // 파싱 결과 검증
    validateParseResult(courseName, institution, attendanceDates);

    // 결과 반환
    return BootcampParseResponseDTO.builder()
        .name(courseName)
        .organizer(institution)
        .classDates(attendanceDates)
        .build();
  }

  /**
   * 파싱 결과를 검증합니다.
   * 
   * @param courseName 훈련과정명
   * @param institution 훈련기관명
   * @param attendanceDates 교육일 목록
   * @throws BootcampParseFailedException 필수 정보가 누락된 경우
   */
  private void validateParseResult(String courseName, String institution, List<LocalDate> attendanceDates) {
    List<String> missingFields = new ArrayList<>();

    if (courseName == null || courseName.trim().isEmpty()) {
      missingFields.add("훈련과정명");
    }

    if (institution == null || institution.trim().isEmpty()) {
      missingFields.add("훈련기관명");
    }

    if (attendanceDates == null || attendanceDates.isEmpty()) {
      missingFields.add("시간표");
    }

    if (!missingFields.isEmpty()) {
      String message = String.format(
          "부트캠프 정보를 파싱할 수 없습니다. 누락된 정보: %s. 고용24 시간표 페이지에서 전체 텍스트를 복사했는지 확인해주세요.",
          String.join(", ", missingFields));
      throw new BootcampParseFailedException(message);
    }
  }
}
