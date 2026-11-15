package com.odontologia.gestion_citas.modules.pagos.infrastructure.persistence;

import com.odontologia.gestion_citas.modules.pagos.domain.EstadoCuenta;
import com.odontologia.gestion_citas.persistence.entities.Tratamiento;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Cuenta por cobrar: un cargo de un paciente (ortodoncia, cirugía, etc.). Tabla cuentas_por_cobrar. */
@Getter
@Setter
@Entity
@Table(name = "cuentas_por_cobrar")
public class CuentaPorCobrarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tratamiento_id")
    private Tratamiento tratamiento;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "entrada", nullable = false, precision = 10, scale = 2)
    private BigDecimal entrada;

    @Column(name = "cuota_sugerida", precision = 10, scale = 2)
    private BigDecimal cuotaSugerida;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCuenta estado = EstadoCuenta.ACTIVA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;

    @Column(name = "motivo_anulacion", columnDefinition = "TEXT")
    private String motivoAnulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anulado_por")
    private Usuario anuladoPor;

    @Column(name = "anulado_en")
    private LocalDateTime anuladoEn;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "cuenta", fetch = FetchType.LAZY)
    @OrderBy("fecha ASC, creadoEn ASC")
    private List<AbonoEntity> abonos = new ArrayList<>();
}
