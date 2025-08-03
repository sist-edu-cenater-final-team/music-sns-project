package com.github.musicsnsproject.repository.mybatis.mapper;

import com.github.musicsnsproject.domain.user.MyUserVO;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

@Mapper
public interface MyUserMybatisMapper {
     MyUserVO findById(@Param("userId") long userId);

}
