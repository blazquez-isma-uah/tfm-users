package com.tfm.bandas.usuarios.controller;

import com.tfm.bandas.usuarios.dto.UserCreateDTO;
import com.tfm.bandas.usuarios.dto.UserResponseDTO;
import com.tfm.bandas.usuarios.service.UserService;
import com.tfm.bandas.usuarios.utils.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping
    public PaginatedResponse<UserResponseDTO> getAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        logger.info("Calling getAllUsers with pageable: {}", pageable);
        PaginatedResponse<UserResponseDTO> response = PaginatedResponse.from(userService.getAllUsers(pageable));
        logger.info("getAllUsers returning: {}", response);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        logger.info("Calling getUserById with id: {}", id);
        UserResponseDTO response = userService.getUserById(id);
        logger.info("getUserById returning: {}", response);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/email/{email}")
    public UserResponseDTO getUserByEmail(@PathVariable String email) {
        logger.info("Calling getUserByEmail with email: {}", email);
        UserResponseDTO response = userService.getUserByEmail(email);
        logger.info("getUserByEmail returning: {}", response);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/username/{username}")
    public UserResponseDTO getUserByUsername(@PathVariable String username) {
        logger.info("Calling getUserByUsername with username: {}", username);
        UserResponseDTO response = userService.getUserByUsername(username);
        logger.info("getUserByUsername returning: {}", response);
        return response;
    }

    // get user by iamId solo para admin
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/iam/{iamId}")
    public UserResponseDTO getUserByIamId(@PathVariable String iamId) {
        logger.info("Calling getUserByIamId with iamId: {}", iamId);
        UserResponseDTO response = userService.getUserByIamId(iamId);
        logger.info("getUserByIamId returning: {}", response);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UserResponseDTO createUser(@RequestBody @Valid UserCreateDTO dto) {
        logger.info("Calling createUser with userCreateDTO: {}", dto);
        UserResponseDTO response = userService.createUser(dto);
        logger.info("createUser returning: {}", response);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody @Valid UserCreateDTO dto) {
        logger.info("Calling updateUser with id: {} and dto: {}", id, dto);
        UserResponseDTO response = userService.updateUser(id, dto);
        logger.info("updateUser returning: {}", response);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        logger.info("Calling deleteUser with id: {}", id);
        userService.deleteUser(id);
        logger.info("deleteUser completed for id: {}", id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/disable")
    public void disableUser(@PathVariable Long id) {
        logger.info("Calling disableUser with id: {}", id);
        userService.disableUser(id);
        logger.info("disableUser completed for id: {}", id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/enable")
    public void enableUser(@PathVariable Long id) {
        logger.info("Calling enableUser with id: {}", id);
        userService.enableUser(id);
        logger.info("enableUser completed for id: {}", id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/assign-instruments")
    public UserResponseDTO assignInstruments(@PathVariable Long id, @RequestBody Set<Long> instrumentIds) {
        logger.info("Calling assignInstruments with id: {} and instrumentIds: {}", id, instrumentIds);
        UserResponseDTO response = userService.updateUserInstruments(id, instrumentIds);
        logger.info("assignInstruments returning: {}", response);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/search")
    public PaginatedResponse<UserResponseDTO> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long instrumentId,
            @PageableDefault(size = 10) Pageable pageable) {

        logger.info("Calling searchUsers with username: {}, firstName: {}, lastName: {}, email: {}, active: {}, instrumentId: {}, pageable: {}",
                username, firstName, lastName, email, active, instrumentId, pageable);
        PaginatedResponse<UserResponseDTO> response = PaginatedResponse.from(
                userService.searchUsers(username, firstName, lastName, email, active, instrumentId, pageable)
        );
        logger.info("searchUsers returning: {}", response);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/me")
    public UserResponseDTO getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        logger.info("Calling getMyProfile");
        // El claim "sub" de JWT es el que corresponde al iamId
        String iamId = jwt.getSubject();
        UserResponseDTO response = userService.getUserByIamId(iamId);
        logger.info("getMyProfile returning: {}", response);
        return response;
    }
}
