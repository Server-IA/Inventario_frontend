package com.coagronet.user.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.user.User;
import com.coagronet.user.dtos.UserDTO;
import com.coagronet.user.dtos.UserMinimalDTO;
import com.coagronet.user.mappers.UserMapper;
import com.coagronet.user.repositories.UserRepository;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	private final UserRepository userRepository;

	private final UserMapper userMapper;

	private final PasswordEncoder passwordEncoder;

	public UserController(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/{requestedId}")
	private ResponseEntity<UserDTO> findById(@PathVariable Long requestedId) {
		return userRepository.findById(requestedId)
				.map(userMapper::toDto)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	private ResponseEntity<Page<UserDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<UserDTO> page = userRepository.findByUsuarioEstadoIdGreaterThanEqual(0, pageable).map(userMapper::toDto);
		return page.hasContent() ? ResponseEntity.ok(page) : ResponseEntity.noContent().build();
	}

	@GetMapping("/minimal")
	private ResponseEntity<Page<UserMinimalDTO>> findAllMinimal(@PageableDefault Pageable pageable) {
		Page<UserMinimalDTO> page = userRepository.findByUsuarioEstadoIdGreaterThanEqual(0, pageable)
				.map(userMapper::toMinimalDTO);
		return page.hasContent() ? ResponseEntity.ok(page) : ResponseEntity.noContent().build();
	}

	@PutMapping("/{requestedId}")
	private ResponseEntity<Void> putUser(@PathVariable Long requestedId, @RequestBody UserDTO userDTOUpdate) {
		User user = userRepository.findById(requestedId).orElse(null);
		if (null != user) {
			String encodedPassword = passwordEncoder.encode(userDTOUpdate.getPassword());
			UserDTO updatedUserDTO = new UserDTO(requestedId, encodedPassword, userDTOUpdate.getUsername(),
					userDTOUpdate.getPersonaId(), userDTOUpdate.getUsuarioEstadoId(),
					userDTOUpdate.getPreferredLanguage() != null
							? userDTOUpdate.getPreferredLanguage()
							: user.getPreferredLanguage());

			User updatedUser = userMapper.toEntity(updatedUserDTO);
			userRepository.save(updatedUser);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	private ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		try {
			if (userRepository.existsById(id)) {
				userRepository.deleteById(id);
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

}
