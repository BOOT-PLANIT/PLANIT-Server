package com.planit.planit.domain.enrollment.mapper;

import com.planit.planit.domain.enrollment.dto.EnrollmentResponseDTO;
import com.planit.planit.domain.enrollment.dto.MyBootcampDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyBootcampMapper {

	int countEnrollment(@Param("userId") Long userId, @Param("bootcampId") Long bootcampId);

	int insertEnrollment(@Param("userId") Long userId, @Param("bootcampId") Long bootcampId);

	List<MyBootcampDto> selectMyBootcamps(@Param("userId") Long userId);

	int deleteEnrollment(@Param("enrollmentId") Long enrollmentId, @Param("userId") Long userId);

	boolean existsById(@Param("bootcampId") Long bootcampId);

	EnrollmentResponseDTO findLatestEnrollment (@Param("userId") Long userId);
}
