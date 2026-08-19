package com.inventario.kardex.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.inventario.kardex.KardexAdminView;

public interface KardexAdminViewRepository
        extends JpaRepository<KardexAdminView, Long>, JpaSpecificationExecutor<KardexAdminView> {

}
