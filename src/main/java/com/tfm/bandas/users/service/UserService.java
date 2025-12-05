package com.tfm.bandas.users.service;

import com.tfm.bandas.users.dto.UserCreateRequestDTO;
import com.tfm.bandas.users.dto.UserDTO;
import com.tfm.bandas.users.dto.UserUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface UserService {
    Page<UserDTO> getAllUsers(Pageable pageable);
    UserDTO getUserById(Long userId);
    UserDTO getUserByEmail(String email);
    UserDTO getUserByUsername(String username);
    UserDTO getUserByIamId(String iamId);
    UserDTO createUser(UserCreateRequestDTO dto);
    UserDTO updateUser(Long userId, UserUpdateRequestDTO dto, int ifMatchVersion);
    void deleteUser(Long userId, int ifMatchVersion);
    void disableUser(Long userId, int ifMatchVersion);
    void enableUser(Long userId, int ifMatchVersion);
    UserDTO assignInstrumentToUser(Long userId, Long instrumentId, int ifMatchVersion);
    UserDTO removeInstrumentFromUser(Long userId, Long instrumentId, int ifMatchVersion);
    UserDTO updateUserInstruments(Long userId, Set<Long> instrumentIds, int ifMatchVersion);
    Page<UserDTO> searchUsers(String username, String firstName, String lastName, String secondLastName,
                              String email, Boolean active, Long instrumentId, String role, Pageable pageable);
}
