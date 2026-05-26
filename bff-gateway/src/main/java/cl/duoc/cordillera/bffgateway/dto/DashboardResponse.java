package cl.duoc.cordillera.bffgateway.dto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class DashboardResponse {

    private String statusBff;
    private BigDecimal ventasTotales;
    private List<?> kpis;
    private List<?> alertas;
    private List<?> datosSucursal;
    private List<?> tendenciaVentas;
    private List<?> reportesRecientes;
    private List<?> servicios;

    public DashboardResponse() {}

    public DashboardResponse(String statusBff, BigDecimal ventasTotales,
            List<?> kpis, List<?> alertas) {
        this.statusBff = statusBff;
        this.ventasTotales = ventasTotales;
        this.kpis = kpis != null ? kpis : Collections.emptyList();
        this.alertas = alertas != null ? alertas : Collections.emptyList();
        this.datosSucursal = Collections.emptyList();
        this.tendenciaVentas = Collections.emptyList();
        this.reportesRecientes = Collections.emptyList();
        this.servicios = Collections.emptyList();
    }

    public DashboardResponse(String statusBff, BigDecimal ventasTotales,
            List<?> kpis, List<?> alertas, List<?> datosSucursal) {
        this.statusBff = statusBff;
        this.ventasTotales = ventasTotales;
        this.kpis = kpis != null ? kpis : Collections.emptyList();
        this.alertas = alertas != null ? alertas : Collections.emptyList();
        this.datosSucursal = datosSucursal != null ? datosSucursal : Collections.emptyList();
        this.tendenciaVentas = Collections.emptyList();
        this.reportesRecientes = Collections.emptyList();
        this.servicios = Collections.emptyList();
    }

    public DashboardResponse(String statusBff, BigDecimal ventasTotales,
            List<?> kpis, List<?> alertas, List<?> datosSucursal,
            List<?> tendenciaVentas, List<?> reportesRecientes, List<?> servicios) {
        this.statusBff = statusBff;
        this.ventasTotales = ventasTotales;
        this.kpis = kpis != null ? kpis : Collections.emptyList();
        this.alertas = alertas != null ? alertas : Collections.emptyList();
        this.datosSucursal = datosSucursal != null ? datosSucursal : Collections.emptyList();
        this.tendenciaVentas = tendenciaVentas != null ? tendenciaVentas : Collections.emptyList();
        this.reportesRecientes = reportesRecientes != null ? reportesRecientes : Collections.emptyList();
        this.servicios = servicios != null ? servicios : Collections.emptyList();
    }

    public String getStatusBff() { return statusBff; }
    public void setStatusBff(String statusBff) { this.statusBff = statusBff; }
    public BigDecimal getVentasTotales() { return ventasTotales; }
    public void setVentasTotales(BigDecimal ventasTotales) { this.ventasTotales = ventasTotales; }
    public List<?> getKpis() { return kpis; }
    public void setKpis(List<?> kpis) { this.kpis = kpis; }
    public List<?> getAlertas() { return alertas; }
    public void setAlertas(List<?> alertas) { this.alertas = alertas; }
    public List<?> getDatosSucursal() { return datosSucursal; }
    public void setDatosSucursal(List<?> datosSucursal) { this.datosSucursal = datosSucursal; }
    public List<?> getTendenciaVentas() { return tendenciaVentas; }
    public void setTendenciaVentas(List<?> tendenciaVentas) { this.tendenciaVentas = tendenciaVentas; }
    public List<?> getReportesRecientes() { return reportesRecientes; }
    public void setReportesRecientes(List<?> reportesRecientes) { this.reportesRecientes = reportesRecientes; }
    public List<?> getServicios() { return servicios; }
    public void setServicios(List<?> servicios) { this.servicios = servicios; }
}
