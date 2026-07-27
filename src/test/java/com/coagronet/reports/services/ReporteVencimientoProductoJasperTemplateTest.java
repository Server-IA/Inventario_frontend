/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoJasperTemplateTest.java
 Descripcion        : Pruebas de compilacion y contrato de la plantilla Jasper de vencimientos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-18 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

import net.sf.jasperreports.engine.JasperCompileManager;

class ReporteVencimientoProductoJasperTemplateTest {

	private static final List<String> REPORT_MESSAGE_KEYS = List.of(
			"report.vencimiento.title",
			"report.vencimiento.generated-at",
			"report.vencimiento.label.empresa",
			"report.vencimiento.label.sede",
			"report.vencimiento.label.bloque",
			"report.vencimiento.label.espacio",
			"report.vencimiento.label.almacen",
			"report.vencimiento.label.municipio",
			"report.vencimiento.column.producto",
			"report.vencimiento.column.estado",
			"report.vencimiento.column.fecha-vencimiento",
			"report.vencimiento.page",
			"report.vencimiento.estado.vencido",
			"report.vencimiento.estado.proximo");

	@Test
	void shouldCompileTemplateAndReferenceLocalizedParameters() throws Exception {
		try (InputStream stream = getClass().getClassLoader()
			.getResourceAsStream("reports/producto_vencimiento.jrxml")) {
			assertThat(stream).isNotNull();
			byte[] templateBytes = stream.readAllBytes();
			String template = new String(templateBytes, StandardCharsets.UTF_8);

			assertThat(template)
				.contains("$P{report_title}", "$P{label_producto}", "$P{label_estado}")
				.contains("$P{label_fecha_vencimiento}", "$P{estado_vencido}", "$P{estado_proximo}")
				.contains("uuid=\"b41c7d22-8d72-4dfb-8a7a-5101a98bfb96\" pattern=\"dd/MM/yyyy\"")
				.contains("uuid=\"3774290f-38f2-4997-a355-df4efdb70795\" pattern=\"dd/MM/yyyy\"")
				.doesNotContain("<text><![CDATA[VENCIMIENTO PRODUCTO]]>")
				.doesNotContain("<text><![CDATA[Producto]]>")
				.doesNotContain("<text><![CDATA[Estado]]>")
				.doesNotContain("<text><![CDATA[Fecha de vencimiento]]>");
			assertThat(JasperCompileManager.compileReport(new ByteArrayInputStream(templateBytes))).isNotNull();
		}
	}

	@Test
	void shouldDefineReportMessagesForSpanishAndEnglish() {
		ResourceBundle spanish = ResourceBundle.getBundle("messages", Locale.forLanguageTag("es"));
		ResourceBundle english = ResourceBundle.getBundle("messages", Locale.ENGLISH);

		assertThat(spanish.keySet()).containsAll(REPORT_MESSAGE_KEYS);
		assertThat(english.keySet()).containsAll(REPORT_MESSAGE_KEYS);
	}

}
