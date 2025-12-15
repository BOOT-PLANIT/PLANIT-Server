package com.planit.planit.domain.enrollment.service;

import com.planit.planit.domain.enrollment.dto.EnrollmentResponseDTO;
import com.planit.planit.domain.enrollment.dto.MyBootcampDto;
import com.planit.planit.domain.enrollment.mapper.MyBootcampMapper;
import com.planit.planit.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyBootcampService {

	private final MyBootcampMapper bootcampMapper;
	private final UserMapper userMapper;

	/** 부트캠프 등록 */
	public EnrollmentResponseDTO enrollBootcamp(String uid, Long bootcampId) {
		Long userId = userMapper.findByUid(uid)
			.orElseThrow(() -> new IllegalArgumentException("사용자 없음"))
			.getId();

		if (!bootcampMapper.existsById(bootcampId)) {
			throw new IllegalArgumentException("존재하지 않는 부트캠프입니다.");
		}

		if (bootcampMapper.countEnrollment(userId, bootcampId) > 0) {
			throw new IllegalArgumentException("이미 등록된 부트캠프입니다.");
		}

		bootcampMapper.insertEnrollment(userId, bootcampId);

		return bootcampMapper.findLatestEnrollment(userId);
	}

	/** 내 부트캠프 목록 조회 */
	public List<MyBootcampDto> getMyBootcampsByUid(String uid) {
		Long userId = userMapper.findByUid(uid)
			.orElseThrow(() -> new IllegalArgumentException("사용자 없음"))
			.getId();
		return bootcampMapper.selectMyBootcamps(userId);
	}

	/** 내 부트캠프 삭제 */
	public void deleteMyBootcampByUid(Long enrollmentId, String uid) {
		Long userId = userMapper.findByUid(uid)
			.orElseThrow(() -> new IllegalArgumentException("사용자 없음"))
			.getId();
		int result = bootcampMapper.deleteEnrollment(enrollmentId, userId);
		if (result == 0) {
			throw new IllegalArgumentException("등록 정보를 찾을 수 없습니다.");
		}
	}
}
