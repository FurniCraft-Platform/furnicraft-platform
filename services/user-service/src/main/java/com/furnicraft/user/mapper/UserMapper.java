package com.furnicraft.user.mapper;

import com.furnicraft.user.dto.UserResponse;
import com.furnicraft.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
