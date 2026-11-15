package com.odontologia.gestion_citas.modules.pagos.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CuentaPorCobrarRepository extends JpaRepository<CuentaPorCobrarEntity, UUID> {

    List<CuentaPorCobrarEntity> findByPaciente_IdOrderByCreadoEnDesc(UUID pacienteId);

    /** Bloquea la cuenta mientras se registra o aprueba un abono, para que dos abonos simultáneos no superen el saldo. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CuentaPorCobrarEntity c where c.id = :id")
    Optional<CuentaPorCobrarEntity> findByIdParaActualizar(@Param("id") UUID id);
}
