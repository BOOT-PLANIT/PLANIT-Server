package com.planit.planit.domain.user.service;

import com.google.firebase.auth.FirebaseToken;
import com.planit.planit.domain.auth.dto.LoginResponseDTO;
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

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FirebaseAccountService {

	private final UserMapper mapper;
	private final MyBootcampMapper enrollmentMapper;

	private static final Set<String> ADMIN_UIDS   = Set.of(/* "admin-uid-1" */);
	private static final Set<String> ADMIN_EMAILS = Set.of(/* "admin@example.com" */);


	/* ==========================================================
	 * DB 동기화만 담당 — UserAccount 반환
	 * ========================================================== */
	private UserAccount syncUser(FirebaseToken token) {

		final String uid = token.getUid();
		final String email = token.getEmail();
		final String displayName = token.getName();
		final String photoUrl = token.getPicture();
		final boolean emailVerified = token.isEmailVerified();

		// 파이어베이스 provider 읽기
		String provider = Optional.ofNullable(token.getClaims().get("firebase"))
			.filter(Map.class::isInstance)
			.map(Map.class::cast)
			.map(map -> map.get("sign_in_provider"))
			.map(Object::toString)
			.orElse("unknown");

		// 유저 레벨 Claim 읽기
		String userLevelStr = Optional.ofNullable(token.getClaims().get("user_level"))
			.map(Object::toString)
			.orElse("USER");
		UserLevel userLevel = UserLevel.fromClaim(userLevelStr);

		Optional<UserAccount> found = mapper.findByUid(uid);

		if (found.isEmpty()) {
			UserAccount newUser = UserAccount.builder()
				.uid(uid)
				.email(email)
				.displayName(displayName)
				.photoUrl(photoUrl)
				.provider(provider)
				.userLevel(userLevel)
				.emailVerified(emailVerified)
				.createdAt(LocalDateTime.now())
				.lastLoginAt(LocalDateTime.now())
				.build();

			mapper.insertUser(newUser);
			return newUser;

		} else {
			mapper.updateLastLogin(uid);
			return found.get();
		}
	}


	/* ==========================================================
	 * 인증용 UserDetails 생성
	 * ========================================================== */
	private UserDetails createUserDetails(UserAccount user) {

		List<GrantedAuthority> authorities = new ArrayList<>();

		if ((user.getEmail() != null && ADMIN_EMAILS.contains(user.getEmail()))
			|| ADMIN_UIDS.contains(user.getUid())) {

			authorities.add(new SimpleGrantedAuthority(UserLevel.ADMIN.asRole()));

		} else {
			authorities.add(new SimpleGrantedAuthority(user.getUserLevel().asRole()));
		}

		return User.withUsername(user.getUid())
			.password("N/A")
			.authorities(authorities)
			.build();
	}


	/* ==========================================================
	 * Spring Security 인증에서 사용하는 메서드
	 * ========================================================== */
	public UserDetails ensureAndLoad(FirebaseToken token) {
		UserAccount user = syncUser(token);
		return createUserDetails(user);
	}


	/* ==========================================================
	 * 프론트에 리턴하는 로그인 API 메서드
	 * ========================================================== */
	public LoginResponseDTO loginAndLoad(FirebaseToken token) {

		// DB 동기화 + UserAccount 확보
		UserAccount user = syncUser(token);

		// 최근 참여 부트캠프 ID 조회 (없으면 null)
		Long recentBootcampId = enrollmentMapper.findRecentBootcampId(user.getId());

		// 인증용 UserDetails 생성
		UserDetails userDetails = createUserDetails(user);

		// 최종 DTO 반환
		return LoginResponseDTO.builder()
			.userId(user.getId())
			.recentBootcampId(recentBootcampId)
			.userDetails(userDetails)
			.build();
	}
}
