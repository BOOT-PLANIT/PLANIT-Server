package com.planit.planit.domain.user.mapper;

import com.planit.planit.domain.user.model.UserAccount;
import com.planit.planit.domain.user.model.UserLevel;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
	Optional<UserAccount> findByUid(@Param("uid") String uid);
	int insertUser(UserAccount user);
	int updateLastLogin(@Param("uid") String uid);
	int updateUserLevel(@Param("uid") String uid, @Param("userLevel") UserLevel userLevel);
	int softDeleteUser(@Param("uid") String uid);
	List<UserAccount> findUsersWithAlarmOn();

	@Update("UPDATE users SET alarm_on = #{alarmOn} WHERE uid = #{uid}")
	void updateAlarmSettingByUid(@Param("uid") String uid, @Param("alarmOn") boolean alarmOn);

	@Update("UPDATE users SET fcm_token = #{fcmToken} WHERE uid = #{uid}")
	void updateTokenByUid(@Param("uid") String uid, @Param("fcmToken") String fcmToken);
}
