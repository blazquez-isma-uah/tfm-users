package com.tfm.bandas.users.controller;

import com.tfm.bandas.users.dto.InstrumentDTO;
import com.tfm.bandas.users.dto.UserResponseDTO;
import com.tfm.bandas.users.service.InstrumentService;
import com.tfm.bandas.users.service.UserService;
import com.tfm.bandas.users.utils.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private static final Logger logger = LoggerFactory.getLogger(InstrumentController.class);
    private final InstrumentService instrumentService;
    private final UserService userService;

    @GetMapping
    public PaginatedResponse<InstrumentDTO> getAllInstuments(@PageableDefault(size = 10) Pageable pageable) {
        logger.info("Calling getAll with pageable: {}", pageable);
        PaginatedResponse<InstrumentDTO> response = PaginatedResponse.from(instrumentService.getAllInstruments(pageable));
        logger.info("getAll returning: {}", response);
        return response;
    }

    @GetMapping("/{instrumentId}")
    public InstrumentDTO getInstumentById(@PathVariable Long instrumentId) {
        logger.info("Calling getById with instrumentId: {}", instrumentId);
        InstrumentDTO response = instrumentService.getInstrumentById(instrumentId);
        logger.info("getById returning: {}", response);
        return response;
    }

    @PostMapping
    public InstrumentDTO createInstument(@RequestBody @Valid InstrumentDTO dto) {
        logger.info("Calling create with dto: {}", dto);
        InstrumentDTO response = instrumentService.createInstrument(dto);
        logger.info("create returning: {}", response);
        return response;
    }

    @DeleteMapping("/{instrumentId}")
    public void deleteInstument(@PathVariable Long instrumentId) {
        logger.info("Calling delete with instrumentId: {}", instrumentId);
        instrumentService.deleteInstrument(instrumentId);
        logger.info("delete completed for instrumentId: {}", instrumentId);
    }

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

    @PutMapping("/user/{userId}")
    public UserResponseDTO updateUserInstruments(@PathVariable Long userId, @RequestBody Set<Long> instrumentIds) {
        logger.info("Calling assignInstruments with userId: {} and instrumentIds: {}", userId, instrumentIds);
        UserResponseDTO response = userService.updateUserInstruments(userId, instrumentIds);
        logger.info("assignInstruments returning: {}", response);
        return response;
    }

    @PostMapping("/user/{userId}/{instrumentId}")
    public UserResponseDTO assignInstrumentToUser(@PathVariable Long userId, @PathVariable Long instrumentId) {
        logger.info("Calling assignInstrumentToUser with userId: {} and instrumentId: {}", userId, instrumentId);
        UserResponseDTO response = userService.assignInstrumentToUser(userId, instrumentId);
        logger.info("assignInstrumentToUser returning: {}", response);
        return response;
    }

    @DeleteMapping("/user/{userId}/{instrumentId}")
    public UserResponseDTO removeInstrumentFromUser(@PathVariable Long userId, @PathVariable Long instrumentId) {
        logger.info("Calling removeInstrumentFromUser with userId: {} and instrumentId: {}", userId, instrumentId);
        UserResponseDTO response = userService.removeInstrumentFromUser(userId, instrumentId);
        logger.info("removeInstrumentFromUser returning: {}", response);
        return response;
    }
}
