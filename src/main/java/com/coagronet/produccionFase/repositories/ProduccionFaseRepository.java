package com.coagronet.produccionFase.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.produccionFase.ProduccionFase;


@Repository
public interface ProduccionFaseRepository extends JpaRepository<ProduccionFase, Long> {
    Optional<ProduccionFase> findByIdAndEmpresaId(Long id, Long empresaId);

    List<ProduccionFase> findByEmpresaIdOrderByIdAsc(Long empresaId);

    List<ProduccionFase> findByTipoProduccionId (Long tipoProduccionId);

    List<ProduccionFase> findByVariedadId (Long variedadId);

    List<ProduccionFase> findByFaseId (Long faseId);

    List<ProduccionFase> findByEstadoId (Long estadoId);
    
}
