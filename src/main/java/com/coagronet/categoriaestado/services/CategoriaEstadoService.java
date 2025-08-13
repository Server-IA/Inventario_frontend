package com.coagronet.categoriaestado.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.coagronet.categoriaestado.CategoriaEstado;
import com.coagronet.categoriaestado.repositories.CategoriaEstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaEstadoService {

	private final CategoriaEstadoRepository repository;

	public List<CategoriaEstado> findAll() {
		return repository.findAllByOrderByIdAsc();
	}

	public CategoriaEstado findByIdOrThrow(Long id) {
		return repository.findById(id).orElseThrow(() -> new NotFoundException("categoria-estado.not-found", id));
	}

	public CategoriaEstado create(CategoriaEstado categoriaEstado) {
		categoriaEstado.setId(null);
		return repository.save(categoriaEstado);
	}

	public void update(Long id, CategoriaEstado categoriaEstado) {
		findByIdOrThrow(id);
		categoriaEstado.setId(id);
		repository.save(categoriaEstado);
	}

	public void delete(Long id) {
		findByIdOrThrow(id);
		repository.deleteById(id);
	}

}
