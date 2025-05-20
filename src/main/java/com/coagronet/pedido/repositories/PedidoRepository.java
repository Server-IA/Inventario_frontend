package com.coagronet.pedido.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.pedido.Pedido;

public interface PedidoRepository extends
		JpaRepository<Pedido, Integer> {/*
										 * 
										 * Optional<Pedido> findByIdAndAlmacenSedeEmpresaId( Integer id, Long
										 * empresaId);
										 * 
										 * Page<Pedido> findByAlmacenIdAndAlmacenSedeEmpresaIdAndEstadoIdNot( Integer
										 * almacenId, Long empresaId, Integer estado, Pageable pageable);
										 * 
										 * boolean existsByIdAndAlmacenSedeEmpresaId( Integer id, Long empresaId);
										 * 
										 * boolean existsByIdAndAlmacenSedeEmpresaIdAndEstadoIdNot( Integer id, Long
										 * empresaId, Integer estadoId);
										 */

}
