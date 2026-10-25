package br.com.fesac.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nota")
@Getter
@Setter
@NoArgsConstructor
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double valor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_sala", nullable = false)
    private Sala sala;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_criterio", nullable = false)
    private Criterio criterio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_usuario", nullable = false)
    private Usuario usuario;
}
