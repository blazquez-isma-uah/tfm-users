package com.tfm.bandas.users.controller;

import com.tfm.bandas.users.dto.InstrumentDTO;
import com.tfm.bandas.users.dto.InstrumentRequestDTO;
import com.tfm.bandas.users.dto.UserDTO;
import com.tfm.bandas.users.service.InstrumentService;
import com.tfm.bandas.users.service.UserService;
import com.tfm.bandas.users.utils.EtagUtils;
import com.tfm.bandas.users.utils.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<PaginatedResponse<InstrumentDTO>> getAllInstruments(@PageableDefault(size = 10) Pageable pageable) {
        logger.info("Calling getAll with pageable: {}", pageable);
        PaginatedResponse<InstrumentDTO> response = PaginatedResponse.from(instrumentService.getAllInstruments(pageable));
        logger.info("getAll returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{instrumentId}")
    public ResponseEntity<InstrumentDTO> getInstrumentById(@PathVariable Long instrumentId) {
        logger.info("Calling getById with instrumentId: {}", instrumentId);
        InstrumentDTO response = instrumentService.getInstrumentById(instrumentId);
        logger.info("getById returning: {}", response);
        return EtagUtils.withEtag(ResponseEntity.ok(), response.version(), response);
    }

    @PostMapping
    public ResponseEntity<InstrumentDTO> createInstrument(@RequestBody @Valid InstrumentRequestDTO dto) {
        logger.info("Calling create with dto: {}", dto);
        InstrumentDTO response = instrumentService.createInstrument(dto);
        logger.info("create returning: {}", response);
        return EtagUtils.withEtag(ResponseEntity.status(HttpStatus.CREATED), response.version(), response);
    }

    @PutMapping("/{instrumentId}")
    public ResponseEntity<InstrumentDTO> updateInstrument(@PathVariable Long instrumentId,
                                                         @RequestBody @Valid InstrumentRequestDTO dto,
                                                         @RequestHeader(name = HttpHeaders.IF_MATCH, required = true) String ifMatch) {
        logger.info("Calling update with instrumentId: {}, dto: {}, ifMatch: {}", instrumentId, dto, ifMatch);
        int version = EtagUtils.parseIfMatchToVersion(ifMatch);
        InstrumentDTO response = instrumentService.updateInstrument(instrumentId, dto, version);
        logger.info("update returning: {}", response);
        return EtagUtils.withEtag(ResponseEntity.ok(), response.version(), response);
    }

    @DeleteMapping("/{instrumentId}")
    public ResponseEntity<Void> deleteInstrument(@PathVariable Long instrumentId,
                                                @RequestHeader(name = HttpHeaders.IF_MATCH, required = true) String ifMatch) {
        logger.info("Calling delete with instrumentId: {}, ifMatch: {}", instrumentId, ifMatch);
        int version = EtagUtils.parseIfMatchToVersion(ifMatch);
        instrumentService.deleteInstrument(instrumentId, version);
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
    public ResponseEntity<UserDTO> updateUserInstruments(@PathVariable Long userId, @RequestBody Set<Long> instrumentIds,
                                                         @RequestHeader(name = HttpHeaders.IF_MATCH, required = true) String ifMatch) {
        logger.info("Calling assignInstruments with userId: {} and instrumentIds: {}, ifMatch: {}", userId, instrumentIds, ifMatch);
        int version = EtagUtils.parseIfMatchToVersion(ifMatch);
        UserDTO response = userService.updateUserInstruments(userId, instrumentIds, version);
        logger.info("assignInstruments returning: {}", response);
        return EtagUtils.withEtag(ResponseEntity.ok(), response.version(), response);
    }

    @PostMapping("/user/{userId}/{instrumentId}")
    public ResponseEntity<UserDTO> assignInstrumentToUser(@PathVariable Long userId, @PathVariable Long instrumentId,
                                                          @RequestHeader(name = HttpHeaders.IF_MATCH, required = true) String ifMatch) {
        logger.info("Calling assignInstrumentToUser with userId: {} and instrumentId: {}, ifMatch: {}", userId, instrumentId, ifMatch);
        int version = EtagUtils.parseIfMatchToVersion(ifMatch);
        UserDTO response = userService.assignInstrumentToUser(userId, instrumentId, version);
        logger.info("assignInstrumentToUser returning: {}", response);
        return EtagUtils.withEtag(ResponseEntity.ok(), response.version(), response);
    }

    @DeleteMapping("/user/{userId}/{instrumentId}")
    public ResponseEntity<UserDTO> removeInstrumentFromUser(@PathVariable Long userId, @PathVariable Long instrumentId,
                                                            @RequestHeader(name = HttpHeaders.IF_MATCH, required = true) String ifMatch) {
        logger.info("Calling removeInstrumentFromUser with userId: {} and instrumentId: {}, ifMatch: {}", userId, instrumentId, ifMatch);
        int version = EtagUtils.parseIfMatchToVersion(ifMatch);
        UserDTO response = userService.removeInstrumentFromUser(userId, instrumentId, version);
        logger.info("removeInstrumentFromUser returning: {}", response);
        return EtagUtils.withEtag(ResponseEntity.ok(), response.version(), response);
    }
}
