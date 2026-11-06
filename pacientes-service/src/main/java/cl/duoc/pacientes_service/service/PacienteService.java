package cl.duoc.pacientes_service.service;

import cl.duoc.pacientes_service.exception.EmailExistenteException;
import cl.duoc.pacientes_service.exception.RutExistenteException;
import cl.duoc.pacientes_service.model.Paciente;
import cl.duoc.pacientes_service.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    public Paciente findById(Long id) {
        return pacienteRepository.findById(id).orElse(null);
    }

    public Paciente save(Paciente paciente) {
        // VALIDACIONES
        if (pacienteRepository.existsByRut(paciente.getRut())) {
            throw new RutExistenteException("El RUT '" + paciente.getRut() + "' ya se encuentra registrado.");
        }
        if (pacienteRepository.existsByEmailIgnoreCase(paciente.getEmail())) {
            throw new EmailExistenteException("El email '" + paciente.getEmail() + "' ya se encuentra registrado.");
        }
        return pacienteRepository.save(paciente);
    }

    public Paciente update(Long id, Paciente paciente) {
        Paciente pacienteActualizar = pacienteRepository.findById(id).orElse(null);
        if (pacienteActualizar == null) return null;

        // VALIDACIONES (solo si el valor cambio)
        if (!pacienteActualizar.getRut().equals(paciente.getRut())
                && pacienteRepository.existsByRut(paciente.getRut())) {
            throw new RutExistenteException("No se puede actualizar. El RUT '" + paciente.getRut() + "' ya existe.");
        }
        if (!pacienteActualizar.getEmail().equalsIgnoreCase(paciente.getEmail())
                && pacienteRepository.existsByEmailIgnoreCase(paciente.getEmail())) {
            throw new EmailExistenteException("No se puede actualizar. El email '" + paciente.getEmail() + "' ya existe.");
        }

        // Mapear campos aqui
        pacienteActualizar.setRut(paciente.getRut());
        pacienteActualizar.setNombre(paciente.getNombre());
        pacienteActualizar.setApellido(paciente.getApellido());
        pacienteActualizar.setEmail(paciente.getEmail());
        pacienteActualizar.setTelefono(paciente.getTelefono());
        pacienteActualizar.setFechaNacimiento(paciente.getFechaNacimiento());
        pacienteActualizar.setSexoBiologico(paciente.getSexoBiologico());
        pacienteActualizar.setIdentidadGenero(paciente.getIdentidadGenero());
        pacienteActualizar.setPeso(paciente.getPeso());
        pacienteActualizar.setEstatura(paciente.getEstatura());
        pacienteActualizar.setCintura(paciente.getCintura());
        pacienteActualizar.setAlergias(paciente.getAlergias());
        pacienteActualizar.setEnfermedades(paciente.getEnfermedades());
        pacienteActualizar.setPatronAlimenticio(paciente.getPatronAlimenticio());
        return pacienteRepository.save(pacienteActualizar);
    }

    public void delete(Long id) {
        pacienteRepository.deleteById(id);
    }
}