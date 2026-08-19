package com.inventario.pasantia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.pasantia.entity.InventarioProgresoId;

@Repository
public interface PasantiaInventarioProgresoRepository
        extends JpaRepository<com.inventario.pasantia.entity.InventarioProgreso, InventarioProgresoId> {
    List<com.inventario.pasantia.entity.InventarioProgreso> findByIdInventarioId(Long inventarioId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE InventarioProgreso ip SET ip.id.productoIdentificador = :nuevoIdentificador WHERE ip.empId = :empId AND ip.id.productoIdentificador = :identificadorActual")
    void updateProductoIdentificador(Long empId, String identificadorActual, String nuevoIdentificador);
}
