package com.tfm.bandas.users.service;

import com.tfm.bandas.users.dto.UserCreateDTO;
import com.tfm.bandas.users.dto.UserResponseDTO;
import com.tfm.bandas.users.dto.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface UserService {
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    UserResponseDTO getUserById(Long userId);
    UserResponseDTO getUserByEmail(String email);
    UserResponseDTO getUserByUsername(String username);
    UserResponseDTO getUserByIamId(String iamId);
    UserResponseDTO createUser(UserCreateDTO dto);
    UserResponseDTO updateUser(Long userId, UserUpdateDTO dto);
    void deleteUser(Long userId);
    void disableUser(Long userId);
    void enableUser(Long userId);
    UserResponseDTO assignInstrumentToUser(Long userId, Long instrumentId);
    UserResponseDTO removeInstrumentFromUser(Long userId, Long instrumentId);
    UserResponseDTO updateUserInstruments(Long userId, Set<Long> instrumentIds);
    Page<UserResponseDTO> searchUsers(String username, String firstName, String lastName, String secondLastName,
                                      String email, Boolean active, Long instrumentId, Pageable pageable);
}
