package cl.duoc.cordillera.bffgateway.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private String statusBff;
    private BigDecimal ventasTotales;
    private List<?> kpis;
    private List<String> alertas;

    public DashboardResponse() {}

    public DashboardResponse(String statusBff, BigDecimal ventasTotales, 
                              List<?> kpis, List<String> alertas) {
        this.statusBff = statusBff;
        this.ventasTotales = ventasTotales;
        this.kpis = kpis;
        this.alertas = alertas;
    }

    public String getStatusBff() { return statusBff; }
    public void setStatusBff(String statusBff) { this.statusBff = statusBff; }
    public BigDecimal getVentasTotales() { return ventasTotales; }
    public void setVentasTotales(BigDecimal ventasTotales) { this.ventasTotales = ventasTotales; }
    public List<?> getKpis() { return kpis; }
    public void setKpis(List<?> kpis) { this.kpis = kpis; }
    public List<String> getAlertas() { return alertas; }
    public void setAlertas(List<String> alertas) { this.alertas = alertas; }
}