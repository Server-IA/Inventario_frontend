package com.coagronet.pedidocotizacion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.pedidocotizacion.PedidoCotizacion;

public interface PedidoCotizacionRepository extends JpaRepository<PedidoCotizacion, Long> {

	Optional<PedidoCotizacion> findByIdAndPedidoEmpresaId(Long id, Long empresaId);

	List<PedidoCotizacion> findByPedidoIdAndPedidoEmpresaId(Long pedidoId, Long empresaId);

	Page<PedidoCotizacion> findByPedidoEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

}
