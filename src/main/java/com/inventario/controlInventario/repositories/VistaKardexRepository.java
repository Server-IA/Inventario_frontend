package com.inventario.controlInventario.repositories;

import com.inventario.controlInventario.VistaKardex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VistaKardexRepository extends JpaRepository<VistaKardex, Long> {

	List<VistaKardex> findByEmpresaId(Long empresaId);

}
