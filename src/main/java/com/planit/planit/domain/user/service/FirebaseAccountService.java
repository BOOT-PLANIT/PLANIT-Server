package com.planit.planit.domain.user.service;

import com.google.firebase.auth.FirebaseToken;
import com.planit.planit.domain.enrollment.mapper.MyBootcampMapper;
import com.planit.planit.domain.user.mapper.UserMapper;
import com.planit.planit.domain.user.model.UserAccount;
import com.planit.planit.domain.user.model.UserLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FirebaseAccountService {

	private final UserMapper mapper;
	private final MyBootcampMapper bootcampMapper;

	private static final Set<String> ADMIN_UIDS   = Set.of(/* "admin-uid-1" */);
	private static final Set<String> ADMIN_EMAILS = Set.of(/* "admin@example.com" */);

	/** Firebase 토큰으로 DB 동기화, UserDetails 반환 */
	public UserDetails ensureAndLoad(FirebaseToken token) {
		final String uid = token.getUid();
		final String email = token.getEmail();
		final String displayName = token.getName();
		final String photoUrl = token.getPicture();
		final boolean emailVerified = token.isEmailVerified();

		String provider = "unknown";
		Object firebaseClaim = token.getClaims().get("firebase");
		if (firebaseClaim instanceof Map<?, ?> map) {
			Object sip = map.get("sign_in_provider");
			provider = (sip != null) ? sip.toString() : provider;
		}

		String userLevelStr = Optional.ofNullable(token.getClaims().get("user_level"))
			.map(Object::toString)
			.orElse("USER");
		UserLevel level = UserLevel.fromClaim(userLevelStr);

		var found = mapper.findByUid(uid);
		if (found.isEmpty()) {
			var userAccount = UserAccount.builder()
				.uid(uid)
				.email(email)
				.displayName(displayName)
				.photoUrl(photoUrl)
				.provider(provider)
				.userLevel(level)
				.emailVerified(emailVerified)
				.build();
			mapper.insertUser(userAccount);
		} else {
			mapper.updateLastLogin(uid);
		}

		// 권한 설정
		List<GrantedAuthority> authorities = new ArrayList<>();
		if ((email != null && ADMIN_EMAILS.contains(email)) || ADMIN_UIDS.contains(uid)) {
			authorities.add(new SimpleGrantedAuthority(UserLevel.ADMIN.asRole()));
		} else {
			authorities.add(new SimpleGrantedAuthority(level.asRole())); // ROLE_USER 기본값
		}

		return User.withUsername(uid)
			.password("N/A")
			.authorities(authorities)
			.build();
	}

	/** userId 조회 */
	public Long findUserIdByUid(String uid) {
		return mapper.findByUid(uid)
			.map(UserAccount::getId)
			.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
	}

	/** 최근 부트캠프 ID 조회 */
	public Long findRecentBootcampId(Long userId) {
		var latest = bootcampMapper.findLatestEnrollment(userId); // userId만 받도록 Mapper 수정 필요
		return latest != null ? latest.getBootcampId() : null;
	}
}
