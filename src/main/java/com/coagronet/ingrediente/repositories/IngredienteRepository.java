package com.coagronet.ingrediente.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.ingrediente.Ingrediente;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    Optional<Ingrediente> findByIdAndEmpresaId(Long id, Long empresaId);

    List<Ingrediente> findByEmpresaIdOrderByIdAsc(Long empresaId);

}
