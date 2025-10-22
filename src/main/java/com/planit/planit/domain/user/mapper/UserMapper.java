package com.planit.planit.domain.user.mapper;

import com.planit.planit.domain.user.model.UserAccount;
import com.planit.planit.domain.user.model.UserLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
	UserAccount findByUid(@Param("uid") String uid);
	void insertUser(UserAccount user);
	int updateLastLogin(@Param("uid") String uid);
	int updateUserLevel(@Param("uid") String uid,
		@Param("userLevel") UserLevel userLevel);
}
