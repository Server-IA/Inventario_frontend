package com.coagronet.ordenCompraItem.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.empresa.Empresa;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import com.coagronet.ordenCompraItem.OrdenCompraItem;
import com.coagronet.ordenCompraItem.dtos.OrdenCompraItemDTO;
import com.coagronet.ordenCompraItem.mappers.OrdenCompraItemMapper;
import com.coagronet.ordenCompraItem.repositories.OrdenCompraItemRepository;
import com.coagronet.productoPresentacion.repositories.ProductoPresentacionRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/orden_compra_item")
@CrossOrigin(origins = "*")
public class OrdenCompraItemController {/*
										 * 
										 * private final OrdenCompraItemRepository ordenCompraItemRepository; private
										 * final OrdenCompraItemMapper ordenCompraItemMapper; private final
										 * UserRoleRepository userRoleRepository; private final UserRepository
										 * userRepository; private final OrdenCompraRepository ordenCompraRepository;
										 * private final ProductoPresentacionRepository productoPresentacionRepository;
										 * 
										 * private OrdenCompraItemController( OrdenCompraItemRepository
										 * ordenCompraItemRepository, OrdenCompraItemMapper ordenCompraItemMapper,
										 * UserRoleRepository userRoleRepository, UserRepository userRepository,
										 * OrdenCompraRepository ordenCompraRepository, ProductoPresentacionRepository
										 * productoPresentacionRepository) { this.ordenCompraItemRepository =
										 * ordenCompraItemRepository; this.ordenCompraItemMapper =
										 * ordenCompraItemMapper; this.userRoleRepository = userRoleRepository;
										 * this.userRepository = userRepository; this.ordenCompraRepository =
										 * ordenCompraRepository; this.productoPresentacionRepository =
										 * productoPresentacionRepository; }
										 * 
										 * private Empresa getEmpresaFromUser(User user) { return
										 * userRoleRepository.findByUser(user).stream() .map(UserRole::getEmpresa)
										 * .findFirst() .orElseThrow(() -> new
										 * RuntimeException("Empresa no encontrada para el usuario")); }
										 * 
										 * private User getAuthenticatedUser() { String username =
										 * SecurityContextHolder.getContext().getAuthentication().getName(); return
										 * userRepository.findByUsername(username) .orElseThrow(() -> new
										 * UsernameNotFoundException("Usuario no encontrado")); }
										 * 
										 * @GetMapping("/{requestedId}") private ResponseEntity<OrdenCompraItemDTO>
										 * findById(@PathVariable Integer requestedId) { User authenticatedUser =
										 * getAuthenticatedUser(); Empresa empresa =
										 * getEmpresaFromUser(authenticatedUser); return ordenCompraItemRepository
										 * .findByIdAndOrdenCompraPedidoAlmacenSedeEmpresaId(requestedId,
										 * empresa.getId()) .map(ordenCompraItemMapper::toDto) .map(ResponseEntity::ok)
										 * .orElse(ResponseEntity.notFound().build()); }
										 * 
										 * @PostMapping private ResponseEntity<Void> createOrdenCompraItem(@RequestBody
										 * OrdenCompraItemDTO newOrdenCompraItemRequest, UriComponentsBuilder ucb) {
										 * OrdenCompraItemDTO newOrdenCompraItem = new OrdenCompraItemDTO( null,
										 * newOrdenCompraItemRequest.getOrdenCompra(),
										 * newOrdenCompraItemRequest.getProductoPresentacion(),
										 * newOrdenCompraItemRequest.getCantidad(),
										 * newOrdenCompraItemRequest.getPrecio(),
										 * newOrdenCompraItemRequest.getEstado()); User authenticatedUser =
										 * getAuthenticatedUser(); Empresa empresa =
										 * getEmpresaFromUser(authenticatedUser); if
										 * (ordenCompraRepository.existsByIdAndPedidoAlmacenSedeEmpresaIdAndEstadoIdNot(
										 * newOrdenCompraItemRequest.getOrdenCompra(), empresa.getId(), 2) &&
										 * productoPresentacionRepository.existsByIdAndProductoEmpresaIdAndEstadoIdNot(
										 * newOrdenCompraItemRequest.getProductoPresentacion(), empresa.getId(), 2)) {
										 * OrdenCompraItem savedOrdenCompraItem =
										 * ordenCompraItemMapper.toEntity(newOrdenCompraItem);
										 * ordenCompraItemRepository.save(savedOrdenCompraItem); URI
										 * locationOfNewOrdenCompraItem = ucb .path("/api/v1/orden_compra_item/{id}")
										 * .buildAndExpand(savedOrdenCompraItem.getId()) .toUri(); return
										 * ResponseEntity.created(locationOfNewOrdenCompraItem).build(); } return
										 * ResponseEntity.badRequest().build(); }
										 * 
										 * @GetMapping("/ordenCompraId/{requestedOrdenCompraId}") private
										 * ResponseEntity<List<OrdenCompraItemDTO>> findAllByOrdenCompraId(@PathVariable
										 * Long requestedOrdenCompraId,
										 * 
										 * @PageableDefault Pageable pageable) { User authenticatedUser =
										 * getAuthenticatedUser(); Empresa empresa =
										 * getEmpresaFromUser(authenticatedUser); List<OrdenCompraItemDTO>
										 * ordenCompraItemDTOs = ordenCompraItemRepository
										 * .findByOrdenCompraIdAndOrdenCompraPedidoAlmacenSedeEmpresaIdAndEstadoIdNotOrderByIdAsc(
										 * requestedOrdenCompraId, empresa.getId(), 2) .stream()
										 * .map(ordenCompraItemMapper::toDto) .collect(Collectors.toList());
										 * 
										 * return !ordenCompraItemDTOs.isEmpty() ?
										 * ResponseEntity.ok(ordenCompraItemDTOs) : ResponseEntity.noContent().build();
										 * }
										 * 
										 * @PutMapping("/{requestedId}") private ResponseEntity<Void>
										 * putOrdenCompraItem(
										 * 
										 * @PathVariable Integer requestedId, @RequestBody OrdenCompraItemDTO
										 * ordenCompraItemDTOUpdate) { User authenticatedUser = getAuthenticatedUser();
										 * Empresa empresa = getEmpresaFromUser(authenticatedUser); if
										 * (ordenCompraItemRepository.
										 * existsByIdAndOrdenCompraPedidoAlmacenSedeEmpresaId( requestedId,
										 * empresa.getId()) &&
										 * ordenCompraRepository.existsByIdAndPedidoAlmacenSedeEmpresaIdAndEstadoIdNot(
										 * ordenCompraItemDTOUpdate.getOrdenCompra(), empresa.getId(), 2) &&
										 * productoPresentacionRepository.existsByIdAndProductoEmpresaIdAndEstadoIdNot(
										 * ordenCompraItemDTOUpdate.getProductoPresentacion(), empresa.getId(), 2)) {
										 * OrdenCompraItemDTO updateOrdenCompraItemDTO = new OrdenCompraItemDTO(
										 * requestedId, ordenCompraItemDTOUpdate.getOrdenCompra(),
										 * ordenCompraItemDTOUpdate.getProductoPresentacion(),
										 * ordenCompraItemDTOUpdate.getCantidad(), ordenCompraItemDTOUpdate.getPrecio(),
										 * ordenCompraItemDTOUpdate.getEstado()); OrdenCompraItem updatedOrdenCompraItem
										 * = ordenCompraItemMapper.toEntity(updateOrdenCompraItemDTO);
										 * ordenCompraItemRepository.save(updatedOrdenCompraItem); return
										 * ResponseEntity.noContent().build(); } return
										 * ResponseEntity.notFound().build(); }
										 * 
										 * @DeleteMapping("/{id}") private ResponseEntity<Void>
										 * deleteOrdenCompraItem(@PathVariable Integer id) { try { User
										 * authenticatedUser = getAuthenticatedUser(); Empresa empresa =
										 * getEmpresaFromUser(authenticatedUser); if
										 * (ordenCompraItemRepository.existsByIdAndOrdenCompraPedidoAlmacenSedeEmpresaId
										 * (id, empresa.getId())) { ordenCompraItemRepository.deleteById(id); return
										 * ResponseEntity.noContent().build(); } return
										 * ResponseEntity.notFound().build(); } catch (Exception e) { return
										 * ResponseEntity.internalServerError().build(); } }
										 */

}
