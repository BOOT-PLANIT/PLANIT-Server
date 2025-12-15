package com.planit.planit.domain.enrollment.service;

import com.planit.planit.domain.enrollment.dto.EnrollmentResponseDTO;
import com.planit.planit.domain.enrollment.dto.MyBootcampDTO;
import com.planit.planit.domain.enrollment.mapper.MyBootcampMapper;
import com.planit.planit.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyBootcampService {

	private final MyBootcampMapper bootcampMapper;
	private final UserMapper userMapper;

	/** UID → UserId 변환 헬퍼 */
	private Long resolveUserId(String uid) {
		return userMapper.findByUid(uid)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."))
			.getId();
	}

	/** 부트캠프 등록 */
	@Transactional
	public EnrollmentResponseDTO enrollBootcamp(String uid, Long bootcampId) {
		Long userId = resolveUserId(uid);

		if (!bootcampMapper.existsById(bootcampId)) {
			throw new IllegalArgumentException("존재하지 않는 부트캠프입니다.");
		}

		try {
			bootcampMapper.insertEnrollment(userId, bootcampId);
		} catch (DuplicateKeyException e) {
			// DB Unique Constraint 위반 시 처리
			throw new IllegalArgumentException("이미 등록된 부트캠프입니다.", e);
		}

		return bootcampMapper.findLatestEnrollment(userId);
	}

	/** 내 부트캠프 목록 조회 */
	public List<MyBootcampDTO> getMyBootcampsByUid(String uid) {
		Long userId = resolveUserId(uid);
		return bootcampMapper.selectMyBootcamps(userId);
	}

	/** 내 부트캠프 삭제 */
	@Transactional
	public void deleteMyBootcampByUid(Long enrollmentId, String uid) {
		Long userId = resolveUserId(uid);
		int result = bootcampMapper.deleteEnrollment(enrollmentId, userId);
		if (result == 0) {
			throw new IllegalArgumentException("등록 정보를 찾을 수 없습니다.");
		}
	}
}
