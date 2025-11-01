package com.tfm.bandas.users.service;

import com.tfm.bandas.users.dto.InstrumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstrumentService {
    Page<InstrumentDTO> getAllInstruments(Pageable pageable);
    InstrumentDTO getInstrumentById(Long instrumentId);
    InstrumentDTO createInstrument(InstrumentDTO instument);
    void deleteInstrument(Long instrumentId);
    Page<InstrumentDTO> searchInstruments(String instrumentName, String voice, Pageable pageable);
}
