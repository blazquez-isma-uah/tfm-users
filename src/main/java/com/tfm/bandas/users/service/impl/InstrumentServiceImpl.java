package com.tfm.bandas.users.service.impl;

import com.tfm.bandas.users.dto.InstrumentDTO;
import com.tfm.bandas.users.dto.mapper.InstrumentMapper;
import com.tfm.bandas.users.exception.NotFoundException;
import com.tfm.bandas.users.model.entity.InstrumentEntity;
import com.tfm.bandas.users.model.repository.InstrumentRepository;
import com.tfm.bandas.users.model.repository.UserRepository;
import com.tfm.bandas.users.model.specification.InstrumentSpecifications;
import com.tfm.bandas.users.service.InstrumentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstrumentServiceImpl implements InstrumentService {

    private final InstrumentRepository instrumentRepo;
    private final InstrumentMapper instrumentMapper;
    private final UserRepository userRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<InstrumentDTO> getAllInstruments(Pageable pageable) {
        return instrumentRepo.findAll(pageable)
                .map(instrumentMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public InstrumentDTO getInstrumentById(Long instrumentId) {
        return instrumentRepo.findById(instrumentId)
                .map(instrumentMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Instrument not found"));
    }

    @Override
    @Transactional
    public InstrumentDTO createInstrument(InstrumentDTO instument) {
        InstrumentEntity instrument = instrumentMapper.toEntity(instument);
        return instrumentMapper.toDTO(instrumentRepo.save(instrument));
    }

    @Override
    @Transactional
    public void deleteInstrument(Long instrumentId) {
        InstrumentEntity instrument = instrumentRepo.findById(instrumentId)
                .orElseThrow(() -> new EntityNotFoundException("Instrument not found: " + instrumentId));
        // Eliminar asignaciones de usuarios antes de borrar
        userRepo.findAll().forEach(user -> user.getInstruments().remove(instrument));
        instrumentRepo.delete(instrument);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstrumentDTO> searchInstruments(String instrumentName, String voice, Pageable pageable) {
        Specification<InstrumentEntity> spec = Specification.allOf(
                InstrumentSpecifications.instrumentNameContains(instrumentName),
                InstrumentSpecifications.voiceContains(voice));
        return instrumentRepo.findAll(spec, pageable).map(instrumentMapper::toDTO);
    }

}
