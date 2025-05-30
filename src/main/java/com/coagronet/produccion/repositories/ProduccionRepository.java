package com.coagronet.produccion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.produccion.Produccion;

public interface ProduccionRepository extends JpaRepository<Produccion, Long> {


        Optional<Produccion> findByIdAndEmpresaId(Long id, Long empresaId);

        List<Produccion> findByEmpresaIdOrderByIdAsc(Long empresaId);
}
