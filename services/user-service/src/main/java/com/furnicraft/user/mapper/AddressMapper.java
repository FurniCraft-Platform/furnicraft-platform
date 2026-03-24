package com.furnicraft.user.mapper;

import com.furnicraft.user.dto.AddressRequest;
import com.furnicraft.user.dto.AddressResponse;
import com.furnicraft.user.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse toResponse(Address address);

    Address toEntity(AddressRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromRequest(AddressRequest request, @MappingTarget Address address);
}
