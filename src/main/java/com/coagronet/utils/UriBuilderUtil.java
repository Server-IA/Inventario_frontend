package com.coagronet.utils;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UriBuilderUtil {

	public URI buildTipoIdentificacionUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/tipo_identificacion/{id}").buildAndExpand(id).toUri();
	}

	public URI buildPaisUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/pais/{id}").buildAndExpand(id).toUri();
	}

	public URI buildDepartamentoUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/departamento/{id}").buildAndExpand(id).toUri();
	}

	public URI buildMunicipioUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/municipio/{id}").buildAndExpand(id).toUri();
	}

	public URI buildTipoEspacioUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/tipo_espacio/{id}").buildAndExpand(id).toUri();
	}

	public URI buildGrupoUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/grupo/{id}").buildAndExpand(id).toUri();
	}

	public URI buildSedeUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/sede/{id}").buildAndExpand(id).toUri();
	}

	public URI buildMarcaUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/marca/{id}").buildAndExpand(id).toUri();
	}

	public URI buildTipoEvaluacionUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/tipo_evaluacion/{id}").buildAndExpand(id).toUri();
	}

	public URI buildBloqueUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/bloque/{id}").buildAndExpand(id).toUri();
	}

	public URI buildEspacioUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/espacio/{id}").buildAndExpand(id).toUri();
	}

	public URI buildAlmacenUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/almacen/{id}").buildAndExpand(id).toUri();
	}

	public URI buildProductoCategoriaUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/producto_categoria/{id}").buildAndExpand(id).toUri();
	}

	public URI buildPedidoUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/pedido/{id}").buildAndExpand(id).toUri();
	}

	public URI buildTipoProduccionUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/tipo_produccion/{id}").buildAndExpand(id).toUri();
	}

	public URI buildProcesoUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/proceso/{id}").buildAndExpand(id).toUri();
	}

	public URI buildMovimientoUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/movimiento/{id}").buildAndExpand(id).toUri();
	}

	public URI buildIngredienteUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/ingrediente/{id}").buildAndExpand(id).toUri();
	}

	public URI buildProductoUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/producto/{id}").buildAndExpand(id).toUri();
	}

	public URI buildArticuloKardexUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/articulo-kardex/{id}").buildAndExpand(id).toUri();
	}

	public URI buildProduccion(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/produccion/{id}").buildAndExpand(id).toUri();
	}

	public URI buildArticuloPedidoUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/articulo-pedido/{id}").buildAndExpand(id).toUri();
	}

	public URI buildProveedorUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/proveedor/{id}").buildAndExpand(id).toUri();
	}

	public URI buildOrdenCompraUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/orden_compra/{id}").buildAndExpand(id).toUri();
	}

	public URI buildKardexUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/kardex/{id}").buildAndExpand(id).toUri();
	}

	public URI buildTipoMovimientoUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/tipo_movimiento/{id}").buildAndExpand(id).toUri();
	}

	public URI buildArticuloOrdenCompraUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/articulo-orden-compra/{id}").buildAndExpand(id).toUri();
	}

	public URI buildEspacioOcupacionUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/espacio_ocupacion/{id}").buildAndExpand(id).toUri();
	}

	public URI buildOcupacionUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/ocupacion/{id}").buildAndExpand(id).toUri();
	}

	public URI buildTipoInventarioUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/tipo_inventario/{id}").buildAndExpand(id).toUri();
	}

	public URI buildSeccionUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/seccion/{id}").buildAndExpand(id).toUri();
	}

	public URI buildSubseccionUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/subseccion/{id}").buildAndExpand(id).toUri();
	}

	public URI buildInventarioUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/inventario/{id}").buildAndExpand(id).toUri();
	}

	public URI buildInventarioItemUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/inventario_item/{id}").buildAndExpand(id).toUri();
	}

	public URI buildCriterioEvaluacionUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/criterio_evaluacion/{id}").buildAndExpand(id).toUri();
	}

	public URI buildEvaluacionUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/evaluacion/{id}").buildAndExpand(id).toUri();
	}

	public URI buildIngredientePresentacionProductoUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/ingrediente-presentacion-producto/{id}").buildAndExpand(id).toUri();
	}

	public URI buildProductoLocalizacionUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/producto_localizacion/{id}").buildAndExpand(id).toUri();
	}

	public URI buildVariedadUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/variedad/{id}").buildAndExpand(id).toUri();
	}

	public URI buildTipoFaseUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/tipo_fase/{id}").buildAndExpand(id).toUri();
	}
	
	public URI buildFaseUri(Long id, UriComponentsBuilder ucb) {
		return ucb.path("/api/v1/fase/{id}").buildAndExpand(id).toUri();
	}
	
}
	

