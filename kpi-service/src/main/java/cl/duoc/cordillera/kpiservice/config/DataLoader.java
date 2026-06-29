package cl.duoc.cordillera.kpiservice.config;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.repository.KpiRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final KpiRepository kpiRepository;

    public DataLoader(KpiRepository kpiRepository) {
        this.kpiRepository = kpiRepository;
    }

    @Override
    public void run(String... args) {
        if (kpiRepository.count() == 0) {
            Kpi kpi1 = new Kpi();
            kpi1.setNombre("Crecimiento Ventas Omnicanal");
            kpi1.setValor(new BigDecimal("15.5"));
            kpi1.setUnidad("%");
            kpi1.setCategoria("VENTAS");
            kpi1.setEstado("ACTIVO");

            Kpi kpi2 = new Kpi();
            kpi2.setNombre("Rotacion de Stock Hogar/Tech");
            kpi2.setValor(new BigDecimal("85.0"));
            kpi2.setUnidad("%");
            kpi2.setCategoria("INVENTARIO");
            kpi2.setEstado("ACTIVO");

            Kpi kpi3 = new Kpi();
            kpi3.setNombre("Tasa de Entrega a Tiempo");
            kpi3.setValor(new BigDecimal("92.3"));
            kpi3.setUnidad("%");
            kpi3.setCategoria("LOGISTICA");
            kpi3.setEstado("ACTIVO");

            Kpi kpi4 = new Kpi();
            kpi4.setNombre("Margen Bruto General");
            kpi4.setValor(new BigDecimal("35.8"));
            kpi4.setUnidad("%");
            kpi4.setCategoria("RENTABILIDAD");
            kpi4.setEstado("ACTIVO");

            Kpi kpi5 = new Kpi();
            kpi5.setNombre("Retencion de Clientes");
            kpi5.setValor(new BigDecimal("76.4"));
            kpi5.setUnidad("%");
            kpi5.setCategoria("CRM");
            kpi5.setEstado("ACTIVO");

            kpiRepository.save(kpi1);
            kpiRepository.save(kpi2);
            kpiRepository.save(kpi3);
            kpiRepository.save(kpi4);
            kpiRepository.save(kpi5);
        }
    }
}
