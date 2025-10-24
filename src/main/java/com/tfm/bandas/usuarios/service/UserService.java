package com.tfm.bandas.usuarios.service;

import com.tfm.bandas.usuarios.dto.UserCreateDTO;
import com.tfm.bandas.usuarios.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface UserService {
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByEmail(String email);
    UserResponseDTO getUserByUsername(String username);
    UserResponseDTO getUserByIamId(String iamId);
    UserResponseDTO createUser(UserCreateDTO dto);
    UserResponseDTO updateUser(Long id, UserCreateDTO dto);
    void deleteUser(Long id);
    void disableUser(Long id);
    void enableUser(Long id);
    UserResponseDTO updateUserInstruments(Long userId, Set<Long> instrumentIds);
    Page<UserResponseDTO> searchUsers(String username, String firstName, String lastName, String email, Boolean active, Long instrumentId, Pageable pageable);
}
