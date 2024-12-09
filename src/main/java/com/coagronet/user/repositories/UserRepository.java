package com.coagronet.user.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(
            String username);

    Page<User> findByUsuarioEstadoIdGreaterThanEqual(
            int usuarioEstadoId,
            Pageable pageable);

    Optional<User> findById(
            Long id);

}
