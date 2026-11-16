package cl.duoc.pacientes_service.service;

import cl.duoc.pacientes_service.DatosPrueba;
import cl.duoc.pacientes_service.exception.EmailExistenteException;
import cl.duoc.pacientes_service.exception.RutExistenteException;
import cl.duoc.pacientes_service.model.Paciente;
import cl.duoc.pacientes_service.repository.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    @Test
    @DisplayName("findAll devuelve todos los pacientes")
    void findAllDevuelveLista() {
        List<Paciente> pacientes = List.of(DatosPrueba.pacienteConId(1L), DatosPrueba.pacienteConId(2L));
        when(pacienteRepository.findAll()).thenReturn(pacientes);

        assertEquals(2, pacienteService.findAll().size());
    }

    @Test
    @DisplayName("findById devuelve el paciente cuando existe")
    void findByIdExistente() {
        Paciente paciente = DatosPrueba.pacienteConId(1L);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        assertEquals(paciente, pacienteService.findById(1L));
    }

    @Test
    @DisplayName("findById devuelve null cuando no existe")
    void findByIdInexistente() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(pacienteService.findById(99L));
    }

    @Test
    @DisplayName("save guarda el paciente si rut y email estan libres")
    void saveGuardaPaciente() {
        Paciente paciente = DatosPrueba.paciente();
        when(pacienteRepository.existsByRut(paciente.getRut())).thenReturn(false);
        when(pacienteRepository.existsByEmailIgnoreCase(paciente.getEmail())).thenReturn(false);
        when(pacienteRepository.save(paciente)).thenReturn(paciente);

        assertEquals(paciente, pacienteService.save(paciente));
        verify(pacienteRepository).save(paciente);
    }

    @Test
    @DisplayName("save lanza RutExistenteException si el rut ya existe")
    void saveRutDuplicado() {
        Paciente paciente = DatosPrueba.paciente();
        when(pacienteRepository.existsByRut(paciente.getRut())).thenReturn(true);

        assertThrows(RutExistenteException.class, () -> pacienteService.save(paciente));
        verify(pacienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("save lanza EmailExistenteException si el email ya existe")
    void saveEmailDuplicado() {
        Paciente paciente = DatosPrueba.paciente();
        when(pacienteRepository.existsByRut(paciente.getRut())).thenReturn(false);
        when(pacienteRepository.existsByEmailIgnoreCase(paciente.getEmail())).thenReturn(true);

        assertThrows(EmailExistenteException.class, () -> pacienteService.save(paciente));
        verify(pacienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("update devuelve null si el paciente no existe")
    void updateInexistente() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(pacienteService.update(99L, DatosPrueba.paciente()));
        verify(pacienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("update copia los campos nuevos y guarda")
    void updateActualizaCampos() {
        Paciente existente = DatosPrueba.pacienteConId(1L);
        Paciente cambios = DatosPrueba.paciente();
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pacienteRepository.existsByRut(cambios.getRut())).thenReturn(false);
        when(pacienteRepository.existsByEmailIgnoreCase(cambios.getEmail())).thenReturn(false);
        when(pacienteRepository.save(existente)).thenReturn(existente);

        Paciente resultado = pacienteService.update(1L, cambios);

        assertEquals(cambios.getRut(), resultado.getRut());
        assertEquals(cambios.getNombre(), resultado.getNombre());
        assertEquals(cambios.getEmail(), resultado.getEmail());
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("update lanza RutExistenteException si el nuevo rut es de otro paciente")
    void updateRutDuplicado() {
        Paciente existente = DatosPrueba.pacienteConId(1L);
        Paciente cambios = DatosPrueba.paciente();
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pacienteRepository.existsByRut(cambios.getRut())).thenReturn(true);

        assertThrows(RutExistenteException.class, () -> pacienteService.update(1L, cambios));
        verify(pacienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete elimina por id")
    void deleteEliminaPorId() {
        pacienteService.delete(1L);

        verify(pacienteRepository).deleteById(1L);
    }
}
