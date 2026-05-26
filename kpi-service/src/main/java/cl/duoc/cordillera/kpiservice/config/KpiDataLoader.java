package cl.duoc.cordillera.kpiservice.config;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.repository.KpiRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class KpiDataLoader implements CommandLineRunner {

    private final KpiRepository kpiRepository;

    public KpiDataLoader(KpiRepository kpiRepository) {
        this.kpiRepository = kpiRepository;
    }

    @Override
    public void run(String... args) {
        if (kpiRepository.count() > 0) {
            return;
        }

        kpiRepository.save(kpi("Ventas totales", "1250000", "CLP", "ventas", "Activo"));
        kpiRepository.save(kpi("Rotacion de inventario", "82", "%", "inventario", "Advertencia"));
        kpiRepository.save(kpi("Rentabilidad operacional", "18.4", "%", "rentabilidad", "Activo"));
    }

    private Kpi kpi(String nombre, String valor, String unidad, String categoria, String estado) {
        Kpi kpi = new Kpi();
        kpi.setNombre(nombre);
        kpi.setValor(new BigDecimal(valor));
        kpi.setUnidad(unidad);
        kpi.setCategoria(categoria);
        kpi.setEstado(estado);
        return kpi;
    }
}
