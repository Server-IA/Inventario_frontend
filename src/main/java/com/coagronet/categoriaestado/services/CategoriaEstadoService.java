package com.coagronet.categoriaestado.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.coagronet.categoriaestado.CategoriaEstado;
import com.coagronet.categoriaestado.repositories.CategoriaEstadoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaEstadoService {

	private final CategoriaEstadoRepository repository;

	public List<CategoriaEstado> findAll() {
		return repository.findAllByOrderByIdAsc();
	}

	public CategoriaEstado findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	public CategoriaEstado create(CategoriaEstado categoriaEstado) {
		categoriaEstado.setId(null);
		return repository.save(categoriaEstado);
	}

	public void update(Long id, CategoriaEstado categoriaEstado) {
		repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		categoriaEstado.setId(id);
		repository.save(categoriaEstado);
	}

	public void delete(Long id) {
		repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		repository.deleteById(id);
	}

}
