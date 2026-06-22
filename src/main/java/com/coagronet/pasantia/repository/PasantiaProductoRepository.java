package com.coagronet.pasantia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.pasantia.entity.ProductoId;

@Repository
public interface PasantiaProductoRepository extends JpaRepository<com.coagronet.pasantia.entity.Producto, ProductoId> {

    List<com.coagronet.pasantia.entity.Producto> findBySubseccionId(Long subseccionId);
}
