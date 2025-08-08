package com.github.musicsnsproject.repository.mybatis.mapper;

import com.github.musicsnsproject.domain.user.MyUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MyUserMybatisMapper {
     MyUserVO findById(@Param("userId") long userId);

}
