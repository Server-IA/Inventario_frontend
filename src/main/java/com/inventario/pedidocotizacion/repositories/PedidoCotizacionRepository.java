package com.inventario.pedidocotizacion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.inventario.pedidocotizacion.PedidoCotizacion;

/**
 * Repositorio de acceso a datos para
 * {@link com.inventario.pedidocotizacion.PedidoCotizacion}.
 *
 * <p>
 * Incluye consultas derivadas que filtran por el ID de la empresa al que
 * pertenece el pedido asociado, reforzando el aislamiento multiempresa.
 * </p>
 *
 * <p>
 * Convenciones de nomenclatura:
 * </p>
 * <ul>
 * <li>{@code findByIdAndPedidoEmpresaId}: garantiza que solo se consulte el
 * registro
 * perteneciente a la empresa actual.</li>
 * <li>{@code findByPedidoIdAndPedidoEmpresaId}: devuelve las cotizaciones de un
 * pedido
 * concreto para la empresa.</li>
 * <li>{@code findByPedidoEmpresaIdOrderByIdAsc}: pagina las cotizaciones de la
 * empresa
 * ordenadas por ID ascendente.</li>
 * </ul>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
public interface PedidoCotizacionRepository extends JpaRepository<PedidoCotizacion, Long> {

	/**
	 * Busca una cotización por su ID y el ID de empresa del pedido asociado.
	 *
	 * @param id        identificador de la cotización
	 * @param empresaId identificador de la empresa
	 * @return {@code Optional} con el registro si existe
	 */
	Optional<PedidoCotizacion> findByIdAndPedidoEmpresaId(Long id, Long empresaId);

	/**
	 * Lista todas las cotizaciones de un pedido específico y de una empresa dada.
	 *
	 * @param pedidoId  identificador del pedido
	 * @param empresaId identificador de la empresa
	 * @return lista de cotizaciones
	 */
	List<PedidoCotizacion> findByPedidoIdAndPedidoEmpresaId(Long pedidoId, Long empresaId);

	/**
	 * Obtiene una página de cotizaciones pertenecientes a una empresa, ordenadas
	 * por ID ascendente.
	 *
	 * @param empresaId identificador de la empresa
	 * @param pageable  configuración de paginación
	 * @return página de resultados
	 */
	Page<PedidoCotizacion> findByPedidoEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

}
