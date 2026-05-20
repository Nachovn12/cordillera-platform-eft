package cl.duoc.cordillera.kpiservice.service;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.repository.KpiRepository;
import cl.duoc.cordillera.kpiservice.service.calculator.KpiCalculator;
import cl.duoc.cordillera.kpiservice.service.calculator.KpiFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class KpiService {

    private final KpiRepository kpiRepository;
    private final KpiFactory kpiFactory;

    public KpiService(KpiRepository kpiRepository, KpiFactory kpiFactory) {
        this.kpiRepository = kpiRepository;
        this.kpiFactory = kpiFactory;
    }

    public List<Kpi> findAll() {
        return kpiRepository.findAll();
    }

    public Kpi findById(Long id) {
        return kpiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KPI no encontrado: " + id));
    }

    public Kpi create(Kpi kpi) {
        calcularValor(kpi);
        return kpiRepository.save(kpi);
    }

    public Kpi update(Long id, Kpi kpi) {
        Kpi existing = findById(id);
        existing.setNombre(kpi.getNombre());
        existing.setValor(kpi.getValor());
        existing.setUnidad(kpi.getUnidad());
        existing.setCategoria(kpi.getCategoria());
        existing.setEstado(kpi.getEstado());
        calcularValor(existing);
        return kpiRepository.save(existing);
    }

    public void delete(Long id) {
        kpiRepository.deleteById(id);
    }

    public List<Kpi> findByCategoria(String categoria) {
        return kpiRepository.findByCategoria(categoria);
    }

    private void calcularValor(Kpi kpi) {
        try {
            KpiCalculator calculator = kpiFactory.obtenerCalculador(kpi.getCategoria());
            BigDecimal resultado = calculator.calcular(kpi.getValor(), BigDecimal.valueOf(100));
            kpi.setValor(resultado);
            kpi.setUnidad(calculator.getUnidad());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoría KPI no soportada: " + kpi.getCategoria());
        }
    }
}