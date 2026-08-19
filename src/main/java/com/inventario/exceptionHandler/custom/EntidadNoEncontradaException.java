package com.inventario.exceptionHandler.custom;

import java.net.URI;
import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * Excepci?n espec?fica de dominio lanzada cuando una entidad requerida no puede ser
 * hallada en la capa de persistencia.
 * <p>
 * Esta clase extiende {@link ErrorResponseException} para proporcionar una estructura de
 * error compatible con el est?ndar <strong>RFC 7807 (Problem Details)</strong>. Se mapea
 * al c?digo de estado HTTP <code>422 UNPROCESSABLE_ENTITY</code>, indicando que, aunque
 * la solicitud es sint?cticamente correcta, contiene instrucciones sem?nticas err?neas
 * (por ejemplo, una referencia a una llave for?nea inexistente).
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see ErrorResponseException
 * @see ProblemDetail
 * @since 2026
 */
public class EntidadNoEncontradaException extends ErrorResponseException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construye una nueva instancia de la excepci?n con los metadatos de la entidad
	 * faltante.
	 * <p>
	 * Inicializa el cuerpo del error con un t?tulo estandarizado ("Referencia Inv?lida")
	 * y un URI de tipo espec?fico para la plataforma Inventario. Utiliza el c?digo de
	 * mensaje <code>entidad.no_encontrada</code> para permitir la internacionalizaci?n
	 * (i18n) de la respuesta, inyectando el nombre de la entidad y el ID como argumentos.
	 * </p>
	 * @param nombreEntidad nombre t?cnico o funcional de la entidad de negocio (ej.
	 * "SubSistema", "Usuario"). Se utiliza para contextualizar el mensaje de error.
	 * @param id identificador ?nico (llave primaria) que se intent? buscar sin ?xito.
	 */
	public EntidadNoEncontradaException(String nombreEntidad, Long id) {
		super(HttpStatus.UNPROCESSABLE_ENTITY, ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY), null,
				"entidad.no_encontrada", new Object[] { nombreEntidad, id });

		getBody().setTitle("Referencia Inv?lida");
		getBody().setType(URI.create("https://inmero.co/inventario/errors/not-found"));
	}

	public EntidadNoEncontradaException(String nombreEntidad, Collection<Long> idsFaltantes) {
		super(HttpStatus.UNPROCESSABLE_ENTITY, ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY), null,
				"entidades.no_encontradas", new Object[] { nombreEntidad, idsFaltantes.size() });

		getBody().setTitle("Referencias Inv?lidas M?ltiples");
		getBody().setType(URI.create("https://inmero.co/inventario/errors/not-found-multiple"));
		getBody().setProperty("ids_invalidos", idsFaltantes);
	}

}
