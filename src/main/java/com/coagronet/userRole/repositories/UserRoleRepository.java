package com.coagronet.userRole.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.user.User;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.models.UserRoleId;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findByUser(User user);

    Optional<UserRole> findByUserAndEmpresaIdAndRoleId(User user, Long empresaId, Long roleId);
}
