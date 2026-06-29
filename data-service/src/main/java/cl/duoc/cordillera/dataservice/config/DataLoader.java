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
            // SUCURSAL 1: Santiago
            datoRepository.save(new Dato(null, "POS", "VENTA", "150000", null, 1L));
            datoRepository.save(new Dato(null, "POS", "VENTA", "89990", null, 1L));
            datoRepository.save(new Dato(null, "INVENTARIO", "STOCK", "500", null, 1L));
            datoRepository.save(new Dato(null, "FINANZAS", "INGRESO", "3500000", null, 1L));
            datoRepository.save(new Dato(null, "CRM", "CLIENTE", "ACTIVO", null, 1L));
            datoRepository.save(new Dato(null, "E-COMMERCE", "PEDIDO", "125000", null, 1L));
            datoRepository.save(new Dato(null, "FINANZAS", "GASTO", "450000", null, 1L));

            // SUCURSAL 2: Viña del Mar
            datoRepository.save(new Dato(null, "POS", "VENTA", "230000", null, 2L));
            datoRepository.save(new Dato(null, "POS", "VENTA", "45000", null, 2L));
            datoRepository.save(new Dato(null, "INVENTARIO", "STOCK", "320", null, 2L));
            datoRepository.save(new Dato(null, "INVENTARIO", "MERMA", "15", null, 2L));
            datoRepository.save(new Dato(null, "FINANZAS", "INGRESO", "2100000", null, 2L));
            datoRepository.save(new Dato(null, "CRM", "CLIENTE", "ACTIVO", null, 2L));
            datoRepository.save(new Dato(null, "CRM", "CLIENTE", "INACTIVO", null, 2L));

            // SUCURSAL 3: Concepción
            datoRepository.save(new Dato(null, "E-COMMERCE", "PEDIDO", "89000", null, 3L));
            datoRepository.save(new Dato(null, "E-COMMERCE", "PEDIDO", "340000", null, 3L));
            datoRepository.save(new Dato(null, "POS", "VENTA", "120000", null, 3L));
            datoRepository.save(new Dato(null, "INVENTARIO", "STOCK", "410", null, 3L));
            datoRepository.save(new Dato(null, "FINANZAS", "INGRESO", "2800000", null, 3L));
            datoRepository.save(new Dato(null, "CRM", "CLIENTE", "ACTIVO", null, 3L));
        }
    }
}