package com.coagronet.marca.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.marca.Marca;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {

	Page<Marca> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	Optional<Marca> findByIdAndEmpresaId(Long id, Long empresaId);

}
