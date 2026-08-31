package br.com.devops.devops.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;

    @Column(nullable = false, length = 100)
    private String nomeCliente;

    @Column(nullable = false)
    private LocalDate dataPedido;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDoPedido> itenList= new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "idALuno")
    private Aluno aluno;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDoPedido> itens;
}
