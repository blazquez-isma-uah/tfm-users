package com.tfm.bandas.users.dto.mapper;

import com.tfm.bandas.users.dto.InstrumentDTO;
import com.tfm.bandas.users.dto.InstrumentRequestDTO;
import com.tfm.bandas.users.model.entity.InstrumentEntity;

public class InstrumentMapper {

    private InstrumentMapper() {
        // Constructor privado para evitar instanciación
    }

    // Convierte InstrumentEntity a InstrumentDTO
    public static InstrumentDTO toDTO(InstrumentEntity instrument) {
        if (instrument == null) {
            return null;
        }
        return new InstrumentDTO(
                instrument.getId(),
                instrument.getVersion(),
                instrument.getInstrumentName(),
                instrument.getVoice()
        );
    }

    // Convierte InstrumentDTO a InstrumentEntity
    public static InstrumentEntity toEntity(InstrumentDTO instrumentDTO) {
        if (instrumentDTO == null) {
            return null;
        }
        return InstrumentEntity.builder()
                .id(instrumentDTO.id())
                .instrumentName(instrumentDTO.instrumentName())
                .voice(instrumentDTO.voice())
                .build();
    }

    // Convierte InstrumentRequestDTO a InstrumentEntity
    public static InstrumentEntity toEntityFromRequest(InstrumentRequestDTO instrumentRequestDTO) {
        if (instrumentRequestDTO == null) {
            return null;
        }
        return InstrumentEntity.builder()
                .instrumentName(instrumentRequestDTO.instrumentName())
                .voice(instrumentRequestDTO.voice())
                .build();
    }
}
