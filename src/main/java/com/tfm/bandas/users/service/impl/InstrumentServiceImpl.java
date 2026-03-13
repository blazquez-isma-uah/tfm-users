package com.tfm.bandas.users.service.impl;

import com.tfm.bandas.users.dto.InstrumentDTO;
import com.tfm.bandas.users.dto.InstrumentRequestDTO;
import com.tfm.bandas.users.dto.mapper.InstrumentMapper;
import com.tfm.bandas.users.exception.NotFoundException;
import com.tfm.bandas.users.model.entity.InstrumentEntity;
import com.tfm.bandas.users.model.repository.InstrumentRepository;
import com.tfm.bandas.users.model.repository.UserRepository;
import com.tfm.bandas.users.model.specification.InstrumentSpecifications;
import com.tfm.bandas.users.service.InstrumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tfm.bandas.users.utils.EtagUtils.compareVersion;

@Service
@RequiredArgsConstructor
public class InstrumentServiceImpl implements InstrumentService {

    private final InstrumentRepository instrumentRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<InstrumentDTO> getAllInstruments(Pageable pageable) {
        return instrumentRepo.findAll(pageable)
                .map(InstrumentMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public InstrumentDTO getInstrumentById(Long instrumentId) {
        return instrumentRepo.findById(instrumentId)
                .map(InstrumentMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Instrument not found"));
    }

    @Override
    @Transactional
    public InstrumentDTO createInstrument(InstrumentRequestDTO instument) {
        InstrumentEntity instrument = InstrumentMapper.toEntityFromRequest(instument);
        return InstrumentMapper.toDTO(instrumentRepo.save(instrument));
    }

    @Override
    @Transactional
    public InstrumentDTO updateInstrument(Long instrumentId, InstrumentRequestDTO instrumentDTO, int ifMatchVersion) {
        InstrumentEntity instrument = instrumentRepo.findById(instrumentId)
                .orElseThrow(() -> new NotFoundException("Instrument not found: " + instrumentId));
        compareVersion(ifMatchVersion, instrument.getVersion());
        instrument.setInstrumentName(instrumentDTO.instrumentName());
        instrument.setVoice(instrumentDTO.voice());
        return InstrumentMapper.toDTO(instrumentRepo.saveAndFlush(instrument));
    }

    @Override
    @Transactional
    public void deleteInstrument(Long instrumentId, int ifMatchVersion) {
        InstrumentEntity instrument = instrumentRepo.findById(instrumentId)
                .orElseThrow(() -> new NotFoundException("Instrument not found: " + instrumentId));
        compareVersion(ifMatchVersion, instrument.getVersion());
        // Eliminar asignaciones de usuarios antes de borrar
        userRepo.deleteInstrumentAssociationsByInstrumentId(instrumentId);
        instrumentRepo.delete(instrument);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstrumentDTO> searchInstruments(String instrumentName, String voice, Pageable pageable) {
        Specification<InstrumentEntity> spec = Specification.allOf(
                InstrumentSpecifications.instrumentNameContains(instrumentName),
                InstrumentSpecifications.voiceContains(voice));
        return instrumentRepo.findAll(spec, pageable).map(InstrumentMapper::toDTO);
    }

}
