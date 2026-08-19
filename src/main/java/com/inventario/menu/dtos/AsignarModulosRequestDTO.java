package com.inventario.menu.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AsignarModulosRequestDTO {
    // Recibe los IDs de tipo String (ej: ["kardex", "inventario", "rol"])
    private List<String> modulosIds;
}
