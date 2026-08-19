/*=============================================================================
 Nombre del archivo : ReportePedidoJasperTemplateTest.java
 Descripcion        : Pruebas de compilacion y contrato de la plantilla Jasper de pedido.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-18 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

import net.sf.jasperreports.engine.JasperCompileManager;

class ReportePedidoJasperTemplateTest {

	private static final List<String> REPORT_MESSAGE_KEYS = List.of(
			"report.pedido.title",
			"report.pedido.generated-at",
			"report.pedido.label.empresa",
			"report.pedido.label.estado",
			"report.pedido.label.sede",
			"report.pedido.label.bloque",
			"report.pedido.label.espacio",
			"report.pedido.label.almacen",
			"report.pedido.label.municipio",
			"report.pedido.label.responsable",
			"report.pedido.label.contacto",
			"report.pedido.label.correo",
			"report.pedido.label.pedido-id",
			"report.pedido.label.fecha-pedido",
			"report.pedido.label.indice",
			"report.pedido.label.presentacion-id",
			"report.pedido.label.producto",
			"report.pedido.label.cantidad",
			"report.pedido.label.unidad",
			"report.pedido.label.total",
			"report.pedido.label.pagina");

	@Test
	void shouldCompileAndMeetTheRequiredReportContract() throws Exception {
		try (InputStream stream = getClass().getClassLoader().getResourceAsStream("reports/pedido.jrxml")) {
			assertThat(stream).isNotNull();
			byte[] bytes = stream.readAllBytes();
			String template = new String(bytes, StandardCharsets.UTF_8);

			assertThat(template)
				.contains("startNewPage=\"true\"")
				.contains("reprintHeaderOnEachPage=\"true\"")
				.contains("#114232", "#E7F6F7")
				.contains("conditionalStyle")
				.contains("$P{label_presentacion_id}", "$P{label_cantidad}", "$P{label_unidad}")
				.contains("$F{totalPedido}", "$F{advertencia}")
				.doesNotContain("$P!{condicion}")
				.doesNotContain("<query")
				.doesNotContain("<text><![CDATA[PEDIDO]]>")
				.doesNotContain("#3747A1", "#D6E4F0");
			assertThat(JasperCompileManager.compileReport(new ByteArrayInputStream(bytes))).isNotNull();
		}
	}

	@Test
	void shouldDefineAllVisibleTextsInSpanishAndEnglish() {
		ResourceBundle spanish = ResourceBundle.getBundle("messages", Locale.forLanguageTag("es"));
		ResourceBundle english = ResourceBundle.getBundle("messages", Locale.ENGLISH);

		assertThat(spanish.keySet()).containsAll(REPORT_MESSAGE_KEYS);
		assertThat(english.keySet()).containsAll(REPORT_MESSAGE_KEYS);
	}

}
