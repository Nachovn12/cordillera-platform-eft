package cl.duoc.cordillera.dataservice.service;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.repository.DatoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DatoService {

    private final DatoRepository datoRepository;

    public DatoService(DatoRepository datoRepository) {
        this.datoRepository = datoRepository;
    }

    public List<Dato> listarTodos() {
        return datoRepository.findAll();
    }

    public Dato buscarPorId(Long id) {
        return datoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Dato no encontrado con id: " + id));
    }

    public Dato crear(Dato dato) {
        return datoRepository.save(dato);
    }

    public Dato actualizar(Long id, Dato dato) {
        Dato existente = buscarPorId(id);
        existente.setSistemaOrigen(dato.getSistemaOrigen());
        existente.setTipoDato(dato.getTipoDato());
        existente.setValor(dato.getValor());
        existente.setSucursalId(dato.getSucursalId());
        return datoRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        datoRepository.deleteById(id);
    }

    public List<Dato> buscarPorSistemaOrigen(String sistemaOrigen) {
        return datoRepository.findBySistemaOrigen(sistemaOrigen);
    }

    public List<Dato> buscarPorSucursalId(Long sucursalId) {
        return datoRepository.findBySucursalId(sucursalId);
    }
}