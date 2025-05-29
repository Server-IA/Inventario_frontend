package com.coagronet.articuloPedido.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.articuloPedido.ArticuloPedido;

@Repository
public interface ArticuloPedidoRepository extends JpaRepository<ArticuloPedido, Long> {

    Optional<ArticuloPedido> findByIdAndEmpresaId(Long id, Long empresaId);

    List<ArticuloPedido> findByEmpresaIdOrderByIdAsc(Long empresaId);

}