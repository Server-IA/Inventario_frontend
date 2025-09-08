package com.coagronet.pedido.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.pedido.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	Optional<Pedido> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Pedido> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

}
