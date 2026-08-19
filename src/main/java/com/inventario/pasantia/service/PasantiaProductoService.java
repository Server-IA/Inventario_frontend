package com.inventario.pasantia.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.infrastructure.configuration.EmpresaTenantIdentifierResolver;
import com.inventario.pasantia.dto.MensajeResponseDTO;
import com.inventario.pasantia.dto.ProductoRequestDTO;
import com.inventario.pasantia.entity.ProductoId;
import com.inventario.pasantia.entity.Subseccion;
import com.inventario.pasantia.repository.PasantiaProductoRepository;
import com.inventario.pasantia.repository.PasantiaSubseccionRepository;
import com.inventario.pasantia.repository.PasantiaInventarioProgresoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasantiaProductoService {

    private final PasantiaProductoRepository productoRepository;
    private final PasantiaSubseccionRepository subseccionRepository;
    private final EmpresaTenantIdentifierResolver tenantResolver;
    private final PasantiaInventarioProgresoRepository inventarioProgresoRepository;

    @Transactional
    public MensajeResponseDTO crearProducto(ProductoRequestDTO request) {
        if (request.getIdentificador() == null) {
            throw new IllegalArgumentException("El identificador es requerido");
        }

        Long empId = tenantResolver.resolveCurrentTenantIdentifier();
        ProductoId id = new ProductoId(empId, request.getIdentificador());

        if (productoRepository.existsById(id)) {
            throw new IllegalArgumentException("El producto ya existe con este identificador");
        }

        Subseccion subseccion = subseccionRepository.findById(request.getSubseccionId())
                .orElseThrow(() -> new IllegalArgumentException("Subsección no encontrada"));

        com.inventario.pasantia.entity.Producto producto = com.inventario.pasantia.entity.Producto.builder()
                .id(id)
                .nombre(request.getNombre())
                .subseccion(subseccion)
                .cantidadEsperada(request.getCantidadEsperada() != null ? request.getCantidadEsperada() : 1)
                .build();

        productoRepository.save(producto);

        return new MensajeResponseDTO("Producto creado exitosamente");
    }

    @Transactional
    public MensajeResponseDTO actualizarProducto(ProductoRequestDTO request) {
        if (request.getIdentificador() == null) {
            throw new IllegalArgumentException("El identificador es requerido");
        }

        Long empId = tenantResolver.resolveCurrentTenantIdentifier();
        ProductoId id = new ProductoId(empId, request.getIdentificador());

        com.inventario.pasantia.entity.Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        boolean changeId = request.getNuevoIdentificador() != null && !request.getNuevoIdentificador().equals(request.getIdentificador());
        if (changeId) {
            ProductoId nuevoId = new ProductoId(empId, request.getNuevoIdentificador());
            if (productoRepository.existsById(nuevoId)) {
                throw new IllegalArgumentException("El producto ya existe con el nuevo identificador");
            }
        }

        if (request.getSubseccionId() != null && !producto.getSubseccion().getId().equals(request.getSubseccionId())) {
            Subseccion subseccion = subseccionRepository.findById(request.getSubseccionId())
                    .orElseThrow(() -> new IllegalArgumentException("Subsección no encontrada"));
            producto.setSubseccion(subseccion);
        }

        if (request.getNombre() != null) {
            producto.setNombre(request.getNombre());
        }

        if (request.getCantidadEsperada() != null) {
            producto.setCantidadEsperada(request.getCantidadEsperada());
        }

        if (changeId) {
            ProductoId nuevoId = new ProductoId(empId, request.getNuevoIdentificador());
            com.inventario.pasantia.entity.Producto nuevoProducto = com.inventario.pasantia.entity.Producto.builder()
                    .id(nuevoId)
                    .nombre(producto.getNombre())
                    .subseccion(producto.getSubseccion())
                    .cantidadEsperada(producto.getCantidadEsperada())
                    .build();
            productoRepository.save(nuevoProducto);
            inventarioProgresoRepository.updateProductoIdentificador(empId, request.getIdentificador(), request.getNuevoIdentificador());
            productoRepository.delete(producto);
        } else {
            productoRepository.save(producto);
        }

        return new MensajeResponseDTO("Producto actualizado exitosamente");
    }
}
