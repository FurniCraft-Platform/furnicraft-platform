package com.furnicraft.user.controller;

import com.furnicraft.user.client.dto.MediaResponse;
import com.furnicraft.user.dto.AddressRequest;
import com.furnicraft.user.dto.AddressResponse;
import com.furnicraft.user.dto.UserCreateRequest;
import com.furnicraft.user.dto.UserResponse;
import com.furnicraft.user.security.CurrentUserService;
import com.furnicraft.user.service.AddressService;
import com.furnicraft.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User", description = "User profile, avatar, media and address management endpoints")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;
    private final CurrentUserService currentUserService;

    @Operation(
            summary = "Create user",
            description = "Creates a new user. Accessible by ADMIN or internal service-to-service requests."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or @internalAuth.isInternalRequest()")
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @Operation(
            summary = "Get current user profile",
            description = "Returns the authenticated user's profile using security context resolved from gateway forwarded X-Auth-* headers."
    )
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public UserResponse getMe() {
        return userService.getUserById(currentUserService.getCurrentUserId());
    }

    @Operation(
            summary = "Get user by id",
            description = "Returns a user by id. Accessible by ADMIN, the owner of the profile, or internal requests."
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @userAuth.isCurrentUser(#id) or @internalAuth.isInternalRequest()")
    public UserResponse getUserById(
            @Parameter(description = "User id", required = true)
            @PathVariable UUID id
    ) {
        return userService.getUserById(id);
    }

    @Operation(
            summary = "Get user by email",
            description = "Returns user details by email address. Accessible only by ADMIN."
    )
    @GetMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserByEmail(
            @Parameter(description = "User email address", required = true, example = "ali@example.com")
            @RequestParam("email") String email
    ) {
        return userService.getUserByEmail(email);
    }

    @Operation(
            summary = "Get all users",
            description = "Returns paginated user list. Accessible only by ADMIN."
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Operation(
            summary = "Upload current user avatar",
            description = "Uploads avatar image for the authenticated user."
    )
    @PostMapping("/me/avatar")
    @PreAuthorize("isAuthenticated()")
    public UserResponse uploadMyAvatar(
            @Parameter(description = "Avatar image file", required = true)
            @RequestParam("file") MultipartFile file
    ) {
        return userService.uploadAvatar(currentUserService.getCurrentUserId(), file);
    }

    @Operation(
            summary = "Upload user avatar by user id",
            description = "Uploads avatar image for a specific user. Accessible only by ADMIN."
    )
    @PostMapping("/{userId}/avatar")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse uploadAvatar(
            @Parameter(description = "Target user id", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Avatar image file", required = true)
            @RequestParam("file") MultipartFile file
    ) {
        return userService.uploadAvatar(userId, file);
    }

    @Operation(
            summary = "Get current user media",
            description = "Returns media files belonging to the authenticated user."
    )
    @GetMapping("/me/media")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MediaResponse>> getMyMedia() {
        return ResponseEntity.ok(userService.getUserMedia(currentUserService.getCurrentUserId()));
    }

    @Operation(
            summary = "Get user media by user id",
            description = "Returns media files for a specific user. Accessible by ADMIN or internal requests."
    )
    @GetMapping("/{userId}/media")
    @PreAuthorize("hasRole('ADMIN') or @internalAuth.isInternalRequest()")
    public ResponseEntity<List<MediaResponse>> getUserMedia(
            @Parameter(description = "Target user id", required = true)
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(userService.getUserMedia(userId));
    }

    @Operation(
            summary = "Add address for current user",
            description = "Creates a new address for the authenticated user."
    )
    @PostMapping("/me/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public AddressResponse addMyAddress(@Valid @RequestBody AddressRequest request) {
        return addressService.addAddress(currentUserService.getCurrentUserId(), request);
    }

    @Operation(
            summary = "Add address for user by user id",
            description = "Creates a new address for a specific user. Accessible only by ADMIN."
    )
    @PostMapping("/{userId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public AddressResponse addAddress(
            @Parameter(description = "Target user id", required = true)
            @PathVariable UUID userId,
            @Valid @RequestBody AddressRequest request
    ) {
        return addressService.addAddress(userId, request);
    }

    @Operation(
            summary = "Get current user addresses",
            description = "Returns all addresses of the authenticated user."
    )
    @GetMapping("/me/addresses")
    @PreAuthorize("isAuthenticated()")
    public List<AddressResponse> getMyAddresses() {
        return addressService.getUserAddresses(currentUserService.getCurrentUserId());
    }

    @Operation(
            summary = "Get user addresses by user id",
            description = "Returns all addresses of a specific user. Accessible by ADMIN or internal requests."
    )
    @GetMapping("/{userId}/addresses")
    @PreAuthorize("hasRole('ADMIN') or @internalAuth.isInternalRequest()")
    public List<AddressResponse> getUserAddresses(
            @Parameter(description = "Target user id", required = true)
            @PathVariable UUID userId
    ) {
        return addressService.getUserAddresses(userId);
    }

    @Operation(
            summary = "Get current user address by id",
            description = "Returns a single address of the authenticated user by address id."
    )
    @GetMapping("/me/addresses/{addressId}")
    @PreAuthorize("isAuthenticated()")
    public AddressResponse getMyAddressById(
            @Parameter(description = "Address id", required = true)
            @PathVariable UUID addressId
    ) {
        return addressService.getAddressByIdAndUserId(currentUserService.getCurrentUserId(), addressId);
    }

    @Operation(
            summary = "Get user address by user id and address id",
            description = "Returns a single address of a specific user. Accessible by ADMIN or internal requests."
    )
    @GetMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @internalAuth.isInternalRequest()")
    public AddressResponse getUserAddressById(
            @Parameter(description = "Target user id", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Address id", required = true)
            @PathVariable UUID addressId
    ) {
        return addressService.getAddressByIdAndUserId(userId, addressId);
    }

    @Operation(
            summary = "Make current user address default",
            description = "Marks one of the authenticated user's addresses as default."
    )
    @PatchMapping("/me/addresses/{addressId}/default")
    @PreAuthorize("isAuthenticated()")
    public void makeMyAddressDefault(
            @Parameter(description = "Address id", required = true)
            @PathVariable UUID addressId
    ) {
        addressService.makeDefault(currentUserService.getCurrentUserId(), addressId);
    }

    @Operation(
            summary = "Make user address default by user id",
            description = "Marks a specific user's address as default. Accessible only by ADMIN."
    )
    @PatchMapping("/{userId}/addresses/{addressId}/default")
    @PreAuthorize("hasRole('ADMIN')")
    public void makeDefault(
            @Parameter(description = "Target user id", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Address id", required = true)
            @PathVariable UUID addressId
    ) {
        addressService.makeDefault(userId, addressId);
    }

    @Operation(
            summary = "Delete current user address",
            description = "Deletes one of the authenticated user's addresses."
    )
    @DeleteMapping("/me/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void deleteMyAddress(
            @Parameter(description = "Address id", required = true)
            @PathVariable UUID addressId
    ) {
        addressService.deleteAddress(currentUserService.getCurrentUserId(), addressId);
    }

    @Operation(
            summary = "Delete user address by user id",
            description = "Deletes a specific user's address. Accessible only by ADMIN."
    )
    @DeleteMapping("/{userId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAddress(
            @Parameter(description = "Target user id", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Address id", required = true)
            @PathVariable UUID addressId
    ) {
        addressService.deleteAddress(userId, addressId);
    }
}