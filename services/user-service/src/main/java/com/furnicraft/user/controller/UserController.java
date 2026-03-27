package com.furnicraft.user.controller;

import com.furnicraft.user.client.dto.MediaResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN') or @internalAuth.isInternalRequest()")
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @userAuth.isCurrentUser(#id) or @internalAuth.isInternalRequest()")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserByEmail(@RequestParam("email") String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PostMapping("/{userId}/avatar")
    @PreAuthorize("hasRole('ADMIN') or @userAuth.isCurrentUser(#userId)")
    public UserResponse uploadAvatar(@PathVariable UUID userId, @RequestParam("file") MultipartFile file) {
        return userService.uploadAvatar(userId, file);
    }

    @GetMapping("/{userId}/media")
    @PreAuthorize("hasRole('ADMIN') or @userAuth.isCurrentUser(#userId) or @internalAuth.isInternalRequest()")
    public ResponseEntity<List<MediaResponse>> getUserMedia(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserMedia(userId));
    }

    @PostMapping("/{userId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or @userAuth.isCurrentUser(#userId)")
    public AddressResponse addAddress(@PathVariable UUID userId, @Valid @RequestBody AddressRequest request) {
        return addressService.addAddress(userId, request);
    }

    @GetMapping("/{userId}/addresses")
    @PreAuthorize("hasRole('ADMIN') or @userAuth.isCurrentUser(#userId) or @internalAuth.isInternalRequest()")
    public List<AddressResponse> getUserAddresses(@PathVariable UUID userId) {
        return addressService.getUserAddresses(userId);
    }

    @GetMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @userAuth.isCurrentUser(#userId) or @internalAuth.isInternalRequest()")
    public AddressResponse getUserAddressById(
            @PathVariable UUID userId,
            @PathVariable UUID addressId
    ) {
        return addressService.getAddressByIdAndUserId(userId, addressId);
    }

    @PatchMapping("/{userId}/addresses/{addressId}/default")
    @PreAuthorize("hasRole('ADMIN') or @userAuth.isCurrentUser(#userId)")
    public void makeDefault(@PathVariable UUID userId, @PathVariable UUID addressId) {
        addressService.makeDefault(userId, addressId);
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @authz.isCurrentUser(#userId)")
    public void deleteAddress(@PathVariable UUID userId, @PathVariable UUID addressId) {
        addressService.deleteAddress(userId, addressId);
    }
}