package com.inventario.pasantia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.pasantia.entity.ProductoId;

@Repository
public interface PasantiaProductoRepository extends JpaRepository<com.inventario.pasantia.entity.Producto, ProductoId> {

    List<com.inventario.pasantia.entity.Producto> findBySubseccionId(Long subseccionId);
}
