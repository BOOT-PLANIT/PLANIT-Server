package com.planit.planit.domain.enrollment.service;

import com.planit.planit.domain.enrollment.dto.MyBootcampDto;
import com.planit.planit.domain.enrollment.mapper.MyBootcampMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyBootcampService {

	private final MyBootcampMapper bootcampMapper;

	/** 부트캠프 등록 */
	public void enrollBootcamp(Long userId, Long bootcampId) {
		int exists = bootcampMapper.countEnrollment(userId, bootcampId);
		if (exists > 0) {
			throw new IllegalArgumentException("이미 등록된 부트캠프입니다.");
		}
		bootcampMapper.insertEnrollment(userId, bootcampId);
	}

	/** 내 부트캠프 목록 보기 */
	public List<MyBootcampDto> getMyBootcamps(Long userId) {
		return bootcampMapper.selectMyBootcamps(userId);
	}

	/** 내 부트캠프 삭제 */
	public void deleteMyBootcamp(Long userId, Long bootcampId) {
		int result = bootcampMapper.deleteEnrollment(userId, bootcampId);
		if (result == 0) {
			throw new IllegalArgumentException("등록된 부트캠프가 없습니다.");
		}
	}
}

