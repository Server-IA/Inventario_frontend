package com.coagronet.pedido.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.pedido.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	Optional<Pedido> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Pedido> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	@Query("SELECT p FROM Pedido p WHERE p.id = :id AND p.estado.id = :estadoId")
	Optional<Pedido> findByIdAndEstadoId(@Param("id") Long id, @Param("estadoId") Long estadoId);

}
