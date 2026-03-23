package com.furnicraft.user.controller;

import com.furnicraft.user.dto.AddressRequest;
import com.furnicraft.user.dto.AddressResponse;
import com.furnicraft.user.dto.UserCreateRequest;
import com.furnicraft.user.dto.UserResponse;
import com.furnicraft.user.service.AddressService;
import com.furnicraft.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByEmail(@RequestParam("email") String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PostMapping("/{userId}/avatar")
    public UserResponse uploadAvatar(@PathVariable UUID userId, @RequestParam("file") MultipartFile file) {
        return userService.uploadAvatar(userId, file);
    }

    @PostMapping("/{userId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse addAddress(@PathVariable UUID userId, @Valid @RequestBody AddressRequest request) {
        return addressService.addAddress(userId, request);
    }

    @GetMapping("/{userId}/addresses")
    public List<AddressResponse> addAddresses(@PathVariable UUID userId) {
        return addressService.getUserAddresses(userId);
    }

    @PatchMapping("/{userId}/addresses/{addressId}/default")
    public void makeDefault(@PathVariable UUID userId, @PathVariable UUID addressId) {
        addressService.makeDefault(userId, addressId);
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable UUID userId, @PathVariable UUID addressId) {
        addressService.deleteAddress(userId, addressId);
    }
}
