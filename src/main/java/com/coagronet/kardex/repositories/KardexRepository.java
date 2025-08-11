package com.coagronet.kardex.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.kardex.Kardex;

public interface KardexRepository extends JpaRepository<Kardex, Long> {

	Optional<Kardex> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Kardex> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

}
