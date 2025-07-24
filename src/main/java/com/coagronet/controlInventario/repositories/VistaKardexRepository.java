package com.coagronet.controlInventario.repositories;

import com.coagronet.controlInventario.VistaKardex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VistaKardexRepository extends JpaRepository<VistaKardex, Long> {

	List<VistaKardex> findByEmpresaId(Long empresaId);

}
