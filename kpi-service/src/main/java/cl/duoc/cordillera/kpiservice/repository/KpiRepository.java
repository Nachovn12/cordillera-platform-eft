package cl.duoc.cordillera.kpiservice.repository;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KpiRepository extends JpaRepository<Kpi, Long> {
    List<Kpi> findByCategoria(String categoria);
}