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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PaginatedResponse<InstrumentDTO>> getAllInstuments(@PageableDefault(size = 10) Pageable pageable) {
        logger.info("Calling getAll with pageable: {}", pageable);
        PaginatedResponse<InstrumentDTO> response = PaginatedResponse.from(instrumentService.getAllInstruments(pageable));
        logger.info("getAll returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{instrumentId}")
    public ResponseEntity<InstrumentDTO> getInstumentById(@PathVariable Long instrumentId) {
        logger.info("Calling getById with instrumentId: {}", instrumentId);
        InstrumentDTO response = instrumentService.getInstrumentById(instrumentId);
        logger.info("getById returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<InstrumentDTO> createInstument(@RequestBody @Valid InstrumentDTO dto) {
        logger.info("Calling create with dto: {}", dto);
        InstrumentDTO response = instrumentService.createInstrument(dto);
        logger.info("create returning: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{instrumentId}")
    public ResponseEntity<Void> deleteInstument(@PathVariable Long instrumentId) {
        logger.info("Calling delete with instrumentId: {}", instrumentId);
        instrumentService.deleteInstrument(instrumentId);
        logger.info("delete completed for instrumentId: {}", instrumentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<InstrumentDTO>> searchInstruments(
            @RequestParam(required = false) String instrumentName,
            @RequestParam(required = false) String voice,
            @PageableDefault(size = 10) Pageable pageable) {
        logger.info("Calling searchInstruments with instrumentName: {}, voice: {}, pageable: {}", instrumentName, voice, pageable);
        PaginatedResponse<InstrumentDTO> response = PaginatedResponse.from(instrumentService.searchInstruments(instrumentName, voice, pageable));
        logger.info("searchInstruments returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<UserResponseDTO> updateUserInstruments(@PathVariable Long userId, @RequestBody Set<Long> instrumentIds) {
        logger.info("Calling assignInstruments with userId: {} and instrumentIds: {}", userId, instrumentIds);
        UserResponseDTO response = userService.updateUserInstruments(userId, instrumentIds);
        logger.info("assignInstruments returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/{userId}/{instrumentId}")
    public ResponseEntity<UserResponseDTO> assignInstrumentToUser(@PathVariable Long userId, @PathVariable Long instrumentId) {
        logger.info("Calling assignInstrumentToUser with userId: {} and instrumentId: {}", userId, instrumentId);
        UserResponseDTO response = userService.assignInstrumentToUser(userId, instrumentId);
        logger.info("assignInstrumentToUser returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}/{instrumentId}")
    public ResponseEntity<UserResponseDTO> removeInstrumentFromUser(@PathVariable Long userId, @PathVariable Long instrumentId) {
        logger.info("Calling removeInstrumentFromUser with userId: {} and instrumentId: {}", userId, instrumentId);
        UserResponseDTO response = userService.removeInstrumentFromUser(userId, instrumentId);
        logger.info("removeInstrumentFromUser returning: {}", response);
        return ResponseEntity.ok(response);
    }
}
