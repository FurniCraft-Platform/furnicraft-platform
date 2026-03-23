package com.furnicraft.user.service.impl;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import com.furnicraft.user.dto.AddressRequest;
import com.furnicraft.user.dto.AddressResponse;
import com.furnicraft.user.entity.Address;
import com.furnicraft.user.entity.User;
import com.furnicraft.user.mapper.AddressMapper;
import com.furnicraft.user.repository.AddressRepository;
import com.furnicraft.user.repository.UserRepository;
import com.furnicraft.user.service.AddressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressResponse addAddress(UUID userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException("User not found", ErrorCode.RESOURCE_NOT_FOUND));

        if (request.isDefault()){
            resetDefaultAddress(userId);
        }

        Address address = addressMapper.toEntity(request);
        address.setUser(user);

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    public List<AddressResponse> getUserAddresses(UUID userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BaseException("Address not found for this user", ErrorCode.RESOURCE_NOT_FOUND));

        if (request.isDefault() && !address.isDefault()){
            resetDefaultAddress(userId);
        }

        addressMapper.updateEntityFromRequest(request, address);
        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BaseException("Address not found for this user", ErrorCode.RESOURCE_NOT_FOUND));
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public void makeDefault(UUID userId, UUID addressId) {
        resetDefaultAddress(userId);
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BaseException("Address not found for this user", ErrorCode.RESOURCE_NOT_FOUND));
        address.setDefault(true);
        addressRepository.save(address);
    }

    private void resetDefaultAddress(UUID userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        addresses.forEach(a -> a.setDefault(false));
        addressRepository.saveAll(addresses);
    }
}
