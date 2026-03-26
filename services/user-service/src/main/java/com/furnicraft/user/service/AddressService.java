package com.furnicraft.user.service;

import com.furnicraft.user.dto.AddressRequest;
import com.furnicraft.user.dto.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponse addAddress(UUID userId, AddressRequest request);

    List<AddressResponse> getUserAddresses(UUID userId);

    AddressResponse getAddressByIdAndUserId(UUID userId, UUID addressId);

    AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest request);

    void deleteAddress(UUID userId, UUID addressId);

    void makeDefault(UUID userId, UUID addressId);
}