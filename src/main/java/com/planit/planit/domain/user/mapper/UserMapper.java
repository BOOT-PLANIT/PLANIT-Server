package com.planit.planit.domain.user.mapper;

import com.planit.planit.domain.user.model.UserAccount;
import com.planit.planit.domain.user.model.UserLevel;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
	Optional<UserAccount> findByUid(@Param("uid") String uid);
	int insertUser(UserAccount user);
	int updateLastLogin(@Param("uid") String uid);
	int updateUserLevel(@Param("uid") String uid, @Param("userLevel") UserLevel userLevel);
	int softDeleteUser(@Param("uid") String uid);
}
