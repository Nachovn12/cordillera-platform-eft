package cl.duoc.cordillera.dataservice.repository;

import cl.duoc.cordillera.dataservice.model.Dato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatoRepository extends JpaRepository<Dato, Long> {

    List<Dato> findBySistemaOrigen(String sistemaOrigen);

    List<Dato> findBySucursalId(Long sucursalId);
}