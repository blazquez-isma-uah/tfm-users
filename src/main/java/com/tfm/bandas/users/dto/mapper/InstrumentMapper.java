package com.tfm.bandas.users.dto.mapper;

import com.tfm.bandas.users.dto.InstrumentDTO;
import com.tfm.bandas.users.model.entity.InstrumentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {
    InstrumentDTO toDTO(InstrumentEntity instrument);
    InstrumentEntity toEntity(InstrumentDTO instrumentDTO);
}
