package com.furnicraft.user.service;

import com.furnicraft.user.client.dto.MediaResponse;
import com.furnicraft.user.dto.UserCreateRequest;
import com.furnicraft.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(UUID id);

    UserResponse getUserByEmail(String email);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse uploadAvatar(UUID userId, MultipartFile file);

    List<MediaResponse> getUserMedia(UUID userId);
}
