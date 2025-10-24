package com.tfm.bandas.usuarios.controller;

import com.tfm.bandas.usuarios.dto.UserCreateDTO;
import com.tfm.bandas.usuarios.dto.UserResponseDTO;
import com.tfm.bandas.usuarios.service.UserService;
import com.tfm.bandas.usuarios.utils.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping
    public PaginatedResponse<UserResponseDTO> getAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        return PaginatedResponse.from(userService.getAllUsers(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/email/{email}")
    public UserResponseDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/username/{username}")
    public UserResponseDTO getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    // get user by iamId solo para admin
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/iam/{iamId}")
    public UserResponseDTO getUserByIamId(@PathVariable String iamId) {
        return userService.getUserByIamId(iamId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UserResponseDTO createUser(@RequestBody @Valid UserCreateDTO dto) {
        return userService.createUser(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody @Valid UserCreateDTO dto) {
        return userService.updateUser(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/disable")
    public void disableUser(@PathVariable Long id) {
        userService.disableUser(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/enable")
    public void enableUser(@PathVariable Long id) {
        userService.enableUser(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/assign-instruments")
    public UserResponseDTO assignInstruments(@PathVariable Long id, @RequestBody Set<Long> instrumentIds) {
        return userService.updateUserInstruments(id, instrumentIds);
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

        return PaginatedResponse.from(
                userService.searchUsers(username, firstName, lastName, email, active, instrumentId, pageable)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/me")
    public UserResponseDTO getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        // El claim "sub" de JWT es el que corresponde al iamId
        String iamId = jwt.getSubject();
        return userService.getUserByIamId(iamId);
    }
}
