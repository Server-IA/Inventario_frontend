package com.coagronet.utils;

import org.springframework.stereotype.Service;

import com.coagronet.empresa.Empresa;
import com.coagronet.user.User;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEmpresaService {

    private final UserRoleRepository userRoleRepository;

    public Empresa getEmpresaFromUser(User user) {
        return userRoleRepository.findByUser(user).stream()
                .map(UserRole::getEmpresa)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada para el usuario"));
    }
}
