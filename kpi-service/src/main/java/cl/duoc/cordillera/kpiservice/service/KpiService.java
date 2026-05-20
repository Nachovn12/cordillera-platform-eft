package cl.duoc.cordillera.kpiservice.service;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.repository.KpiRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KpiService {

    private final KpiRepository kpiRepository;

    public KpiService(KpiRepository kpiRepository) {
        this.kpiRepository = kpiRepository;
    }

    public List<Kpi> findAll() {
        return kpiRepository.findAll();
    }

    public Kpi findById(Long id) {
        return kpiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KPI no encontrado: " + id));
    }

    public Kpi create(Kpi kpi) {
        return kpiRepository.save(kpi);
    }

    public Kpi update(Long id, Kpi kpi) {
        Kpi existing = findById(id);
        existing.setNombre(kpi.getNombre());
        existing.setValor(kpi.getValor());
        existing.setUnidad(kpi.getUnidad());
        existing.setCategoria(kpi.getCategoria());
        existing.setEstado(kpi.getEstado());
        return kpiRepository.save(existing);
    }

    public void delete(Long id) {
        kpiRepository.deleteById(id);
    }

    public List<Kpi> findByCategoria(String categoria) {
        return kpiRepository.findByCategoria(categoria);
    }
}