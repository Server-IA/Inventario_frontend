package com.inventario.evaluacionitem.repositories;

import com.inventario.evaluacionitem.EvaluacionItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluacionItemRepository extends JpaRepository<EvaluacionItem, Long> {
    Page<EvaluacionItem>findByEmpresaId(Long empresaId, Pageable pageable);
    List<EvaluacionItem> findByEvaluacionIdAndEmpresaId(Long evaluacionId, Long empresaId);
    Optional<EvaluacionItem>findByIdAndEmpresaId(Long id, Long empresaId);
}
