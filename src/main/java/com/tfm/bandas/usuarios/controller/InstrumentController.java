package com.tfm.bandas.usuarios.controller;

import com.tfm.bandas.usuarios.dto.InstrumentDTO;
import com.tfm.bandas.usuarios.service.InstrumentService;
import com.tfm.bandas.usuarios.utils.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private static final Logger logger = LoggerFactory.getLogger(InstrumentController.class);
    private final InstrumentService instrumentService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping
    public PaginatedResponse<InstrumentDTO> getAll(@PageableDefault(size = 10) Pageable pageable) {
        logger.info("Calling getAll with pageable: {}", pageable);
        PaginatedResponse<InstrumentDTO> response = PaginatedResponse.from(instrumentService.getAllInstruments(pageable));
        logger.info("getAll returning: {}", response);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/{id}")
    public InstrumentDTO getById(@PathVariable Long id) {
        logger.info("Calling getById with id: {}", id);
        InstrumentDTO response = instrumentService.getInstrumentById(id);
        logger.info("getById returning: {}", response);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public InstrumentDTO create(@RequestBody @Valid InstrumentDTO dto) {
        logger.info("Calling create with dto: {}", dto);
        InstrumentDTO response = instrumentService.createInstrument(dto);
        logger.info("create returning: {}", response);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        logger.info("Calling delete with id: {}", id);
        instrumentService.deleteInstrument(id);
        logger.info("delete completed for id: {}", id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/search")
    public PaginatedResponse<InstrumentDTO> searchInstruments(
            @RequestParam(required = false) String instrumentName,
            @RequestParam(required = false) String voice,
            @PageableDefault(size = 10) Pageable pageable) {
        logger.info("Calling searchInstruments with instrumentName: {}, voice: {}, pageable: {}", instrumentName, voice, pageable);
        PaginatedResponse<InstrumentDTO> response = PaginatedResponse.from(instrumentService.searchInstruments(instrumentName, voice, pageable));
        logger.info("searchInstruments returning: {}", response);
        return response;
    }
}
