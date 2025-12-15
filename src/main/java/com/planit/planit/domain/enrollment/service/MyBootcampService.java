package com.planit.planit.domain.enrollment.service;

import com.planit.planit.domain.enrollment.dto.EnrollmentResponseDTO;
import com.planit.planit.domain.enrollment.dto.MyBootcampDTO;
import com.planit.planit.domain.enrollment.mapper.MyBootcampMapper;
import com.planit.planit.domain.user.service.FirebaseAccountService;
import com.planit.planit.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyBootcampService {

	private final MyBootcampMapper bootcampMapper;
	private final FirebaseAccountService firebaseAccountService;

	private Long resolveUserId(String uid) {
		return firebaseAccountService.findUserIdByUid(uid);
	}

	/** 부트캠프 등록 */
	@Transactional
	public EnrollmentResponseDTO enrollBootcamp(String uid, Long bootcampId) {
		Long userId = resolveUserId(uid);

		if (!bootcampMapper.existsById(bootcampId)) {
			throw new ResponseStatusException(
				ErrorCode.BOOTCAMP_NOT_FOUND.getStatus(),
				"존재하지 않는 부트캠프입니다."
			);
		}

		if (bootcampMapper.countEnrollment(userId, bootcampId) > 0) {
			throw new ResponseStatusException(
				ErrorCode.CONFLICT.getStatus(),
				"이미 등록된 부트캠프입니다."
			);
		}

		bootcampMapper.insertEnrollment(userId, bootcampId);
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
			throw new ResponseStatusException(
				ErrorCode.ENROLLMENT_NOT_FOUND.getStatus(),
				"등록 정보를 찾을 수 없습니다."
			);
		}
	}
}
