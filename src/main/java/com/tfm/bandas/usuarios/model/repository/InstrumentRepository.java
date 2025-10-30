package com.tfm.bandas.usuarios.model.repository;

import com.tfm.bandas.usuarios.model.entity.InstrumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InstrumentRepository extends JpaRepository<InstrumentEntity, Long>,
        JpaSpecificationExecutor<InstrumentEntity> {

    Optional<InstrumentEntity> findByInstrumentNameAndVoice(String instrumentName, String voice);
    Page<InstrumentEntity> findByInstrumentNameContainingIgnoreCase(String instrumentName, Pageable pageable);
}
