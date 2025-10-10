package com.coagronet.evaluacionitem.repositories;

import com.coagronet.evaluacionitem.EvaluacionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluacionItemRepository extends JpaRepository<EvaluacionItem, Long> {

    List<EvaluacionItem> findByEvaluacionIdAndEmpresaId(Long evaluacionId, Long empresaId);
    Optional<EvaluacionItem>findByIdAndEmpresaId(Long id, Long empresaId);
}
