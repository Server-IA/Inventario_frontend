package com.coagronet.marca.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.marca.Marca;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {

        List<Marca> findByEmpresaIdOrderByIdAsc(
                        Long empresaId);

        List<Marca> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(
                        Long empresaId,
                        Long estadoId);

        Optional<Marca> findByIdAndEmpresaId(
                        Long id,
                        Long empresaId);

        boolean existsByIdAndEmpresaIdAndEstadoIdNot(
                        Long id,
                        Long empresaId,
                        Long estadoId);

}
