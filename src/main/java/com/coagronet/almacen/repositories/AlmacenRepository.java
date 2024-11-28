package com.coagronet.almacen.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.almacen.Almacen;

public interface AlmacenRepository extends JpaRepository<Almacen, Integer> {

        List<Almacen> findBySedeIdAndEstadoIdNotAndSedeEmpresaIdOrderByIdAsc(
                        Long sedeId,
                        Integer estadoId,
                        Long empresaId);

        Page<Almacen> findBySedeIdAndEstadoIdNotAndSedeEmpresaId(
                        Long sedeId,
                        Integer estadoId,
                        Long empresaId,
                        Pageable pageable);

        boolean existsByIdAndSedeEmpresaIdAndEstadoIdNot(
                        Integer id,
                        Long empresaId,
                        Integer estadoId);

        Optional<Almacen> findByIdAndSedeEmpresaIdAndEstadoIdNot(Integer id, Long empresaId, Integer estadoId);

}
