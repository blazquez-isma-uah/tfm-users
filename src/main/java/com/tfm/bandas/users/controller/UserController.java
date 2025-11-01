package com.tfm.bandas.users.controller;

import com.tfm.bandas.users.dto.UserCreateDTO;
import com.tfm.bandas.users.dto.UserResponseDTO;
import com.tfm.bandas.users.dto.UserUpdateDTO;
import com.tfm.bandas.users.service.UserService;
import com.tfm.bandas.users.utils.PaginatedResponse;
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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping
    public PaginatedResponse<UserResponseDTO> getAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        logger.info("Calling getAllUsers with pageable: {}", pageable);
        PaginatedResponse<UserResponseDTO> response = PaginatedResponse.from(userService.getAllUsers(pageable));
        logger.info("getAllUsers returning: {}", response);
        return response;
    }

    @GetMapping("/{userId}")
    public UserResponseDTO getUserById(@PathVariable Long userId) {
        logger.info("Calling getUserById with userId: {}", userId);
        UserResponseDTO response = userService.getUserById(userId);
        logger.info("getUserById returning: {}", response);
        return response;
    }

    @GetMapping("/email/{email}")
    public UserResponseDTO getUserByEmail(@PathVariable String email) {
        logger.info("Calling getUserByEmail with email: {}", email);
        UserResponseDTO response = userService.getUserByEmail(email);
        logger.info("getUserByEmail returning: {}", response);
        return response;
    }

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

    @PostMapping
    public UserResponseDTO createUser(@RequestBody @Valid UserCreateDTO dto) {
        logger.info("Calling createUser with userCreateDTO: {}", dto);
        UserResponseDTO response = userService.createUser(dto);
        logger.info("createUser returning: {}", response);
        return response;
    }

    @PutMapping("/{userId}")
    public UserResponseDTO updateUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateDTO dto) {
        logger.info("Calling updateUser with userId: {} and dto: {}", userId, dto);
        UserResponseDTO response = userService.updateUser(userId, dto);
        logger.info("updateUser returning: {}", response);
        return response;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        logger.info("Calling deleteUser with userId: {}", userId);
        userService.deleteUser(userId);
        logger.info("deleteUser completed for userId: {}", userId);
    }

    @PutMapping("/{userId}/disable")
    public void disableUser(@PathVariable Long userId) {
        logger.info("Calling disableUser with userId: {}", userId);
        userService.disableUser(userId);
        logger.info("disableUser completed for userId: {}", userId);
    }

    @PutMapping("/{userId}/enable")
    public void enableUser(@PathVariable Long userId) {
        logger.info("Calling enableUser with userId: {}", userId);
        userService.enableUser(userId);
        logger.info("enableUser completed for userId: {}", userId);
    }

    @GetMapping("/search")
    public PaginatedResponse<UserResponseDTO> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String secondLastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long instrumentId,
            @PageableDefault(size = 10) Pageable pageable) {

        logger.info("Calling searchUsers with username: {}, firstName: {}, lastName: {}, email: {}, active: {}, instrumentId: {}, pageable: {}",
                username, firstName, lastName, email, active, instrumentId, pageable);
        PaginatedResponse<UserResponseDTO> response = PaginatedResponse.from(
                userService.searchUsers(username, firstName, lastName, secondLastName, email, active, instrumentId, pageable)
        );
        logger.info("searchUsers returning: {}", response);
        return response;
    }

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
