package com.coagronet.produccion.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.coagronet.produccion.dtos.ProduccionDTO;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.coagronet.produccion.Produccion;
import com.coagronet.produccion.repositories.ProduccionRepository;
import com.coagronet.produccion.services.ProduccionService;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/produccion")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProduccionController {

    private final ProduccionService produccionService;
    private final UriBuilderUtil uriBuilderUtil;


    @GetMapping
    public ResponseEntity<List<ProduccionDTO>> findAll () {
        List<ProduccionDTO> produccionDTOList = produccionService.findAll();

        return produccionDTOList.isEmpty()?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(produccionDTOList);

    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<ProduccionDTO> findById (@PathVariable Long requestedId) {
        return produccionService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }


    @PostMapping
    public ResponseEntity<Void> crearProduccion(@RequestBody @Valid ProduccionDTO produccionDTO, UriComponentsBuilder ucb) {
        ProduccionDTO savedProduccionDTO = produccionService.create(produccionDTO);

        URI locationOfNewProduccion = uriBuilderUtil.buildProduccion(savedProduccionDTO.getId(), ucb);
        return ResponseEntity.created(locationOfNewProduccion).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> actualizarProduccion(@PathVariable Long requestedId,
            @RequestBody ProduccionDTO produccionDTO) {

        produccionService.update(requestedId, produccionDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> eliminarProduccion(@PathVariable Long requestedId) {
        produccionService.delete(requestedId);
        return ResponseEntity.noContent().build();
    }

}
