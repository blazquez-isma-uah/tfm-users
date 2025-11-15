package com.tfm.bandas.users.service;

import com.tfm.bandas.users.dto.InstrumentDTO;
import com.tfm.bandas.users.dto.InstrumentRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstrumentService {
    Page<InstrumentDTO> getAllInstruments(Pageable pageable);
    InstrumentDTO getInstrumentById(Long instrumentId);
    InstrumentDTO createInstrument(InstrumentRequestDTO instument);
    InstrumentDTO updateInstrument(Long instrumentId, InstrumentRequestDTO instrument, int ifMatchVersion);
    void deleteInstrument(Long instrumentId, int ifMatchVersion);
    Page<InstrumentDTO> searchInstruments(String instrumentName, String voice, Pageable pageable);
}
