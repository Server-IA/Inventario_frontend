package com.coagronet.pasantia.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.infrastructure.configuration.EmpresaTenantIdentifierResolver;
import com.coagronet.pasantia.dto.MensajeResponseDTO;
import com.coagronet.pasantia.dto.ProductoRequestDTO;
import com.coagronet.pasantia.entity.ProductoId;
import com.coagronet.pasantia.entity.Subseccion;
import com.coagronet.pasantia.repository.PasantiaProductoRepository;
import com.coagronet.pasantia.repository.PasantiaSubseccionRepository;
import com.coagronet.pasantia.repository.PasantiaInventarioProgresoRepository;

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

        com.coagronet.pasantia.entity.Producto producto = com.coagronet.pasantia.entity.Producto.builder()
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

        com.coagronet.pasantia.entity.Producto producto = productoRepository.findById(id)
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
            com.coagronet.pasantia.entity.Producto nuevoProducto = com.coagronet.pasantia.entity.Producto.builder()
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
