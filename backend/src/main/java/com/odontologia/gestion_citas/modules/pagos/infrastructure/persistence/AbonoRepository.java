package com.odontologia.gestion_citas.modules.pagos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AbonoRepository extends JpaRepository<AbonoEntity, UUID> {

    List<AbonoEntity> findByCuenta_IdOrderByFechaAscCreadoEnAsc(UUID cuentaId);
}
