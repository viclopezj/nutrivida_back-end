package cl.duoc.pacientes_service.repository;

import cl.duoc.pacientes_service.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByRut(String rut);
    boolean existsByEmailIgnoreCase(String email);
}