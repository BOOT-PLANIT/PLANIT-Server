package com.planit.planit.domain.user.mapper;

import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.planit.planit.domain.user.model.UserAccount;
import com.planit.planit.domain.user.model.UserLevel;

@Mapper
public interface UserMapper {
  Optional<UserAccount> findByUid(@Param("uid") String uid);

  Optional<UserAccount> findByUidIncludingDeleted(@Param("uid") String uid);

  int insertUser(UserAccount user);

  int updateLastLogin(@Param("uid") String uid);

  int updateUserLevel(@Param("uid") String uid, @Param("userLevel") UserLevel userLevel);

  int softDeleteUser(@Param("uid") String uid);

  int updateFcmToken(@Param("uid") String uid, @Param("fcmToken") String fcmToken);

  String findFcmTokenByUid(@Param("uid") String uid);
}
