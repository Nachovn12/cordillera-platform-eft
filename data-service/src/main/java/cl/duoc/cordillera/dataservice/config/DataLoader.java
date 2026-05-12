package cl.duoc.cordillera.dataservice.config;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.repository.DatoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final DatoRepository datoRepository;

    public DataLoader(DatoRepository datoRepository) {
        this.datoRepository = datoRepository;
    }

    @Override
    public void run(String... args) {
        if (datoRepository.count() == 0) {
            datoRepository.save(new Dato(null, "POS", "VENTA", "150000", null, 1L));
            datoRepository.save(new Dato(null, "POS", "VENTA", "230000", null, 2L));
            datoRepository.save(new Dato(null, "E-COMMERCE", "PEDIDO", "89000", null, 1L));
            datoRepository.save(new Dato(null, "E-COMMERCE", "PEDIDO", "120000", null, 3L));
            datoRepository.save(new Dato(null, "INVENTARIO", "STOCK", "500", null, 1L));
            datoRepository.save(new Dato(null, "INVENTARIO", "STOCK", "320", null, 2L));
            datoRepository.save(new Dato(null, "FINANZAS", "GASTO", "450000", null, 1L));
            datoRepository.save(new Dato(null, "FINANZAS", "INGRESO", "980000", null, 3L));
            datoRepository.save(new Dato(null, "CRM", "CLIENTE", "ACTIVO", null, 2L));
            datoRepository.save(new Dato(null, "CRM", "CLIENTE", "INACTIVO", null, 1L));
        }
    }
}