package com.furnicraft.user.service.impl;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import com.furnicraft.user.client.AuthServiceClient;
import com.furnicraft.user.dto.UserCreateRequest;
import com.furnicraft.user.dto.UserResponse;
import com.furnicraft.user.entity.User;
import com.furnicraft.user.mapper.UserMapper;
import com.furnicraft.user.repository.UserRepository;
import com.furnicraft.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthServiceClient authServiceClient;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
//        boolean existInAuth = authServiceClient.checkUserExists(request.getId());
//        if (!existInAuth) {
//            throw new BaseException("User verification failed! ID not found in Auth Service.", ErrorCode.RESOURCE_NOT_FOUND);
//        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BaseException("User already exists in Auth Service.", ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .id(request.getId())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException("User not found with ID: " + id, ErrorCode.RESOURCE_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException("User not found with Email: " + email, ErrorCode.RESOURCE_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse uploadAvatar(UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException("User not found", ErrorCode.RESOURCE_NOT_FOUND));

        if (file.isEmpty()) {
            throw new BaseException("File cannot be empty", ErrorCode.VALIDATION_FAILED);
        }

        // Burada real bir MediaService çağırıb şəkili serverə yüklənəcək.
        // Hələlik nümunə üçün bir link təyin edirik:
        String fakeUrl = "https://unsplash.com/photos/a-cartoon-character-with-a-blue-hat-on-his-head-0piYmLeSgTQ" + userId + ".jpg";

        user.setAvatarUrl(fakeUrl);
        return userMapper.toResponse(userRepository.save(user));
    }
}
