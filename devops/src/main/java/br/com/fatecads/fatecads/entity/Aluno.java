package br.com.fatecads.fatecads.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idAluno;

    @Column(nullable = false, length = 40)
    private String nomeAluno;

    @Column(length = 40)
    private String emailAluno;

    @Column(nullable = false, length = 11)
    private String telefoneAluno;

    @Column(nullable = false, length = 50)
    private String enderecoAluno;

    @Column(nullable = false, length = 11)
    private String cpfAluno;

    @Column(nullable = false)
    private String raAluno;

    @ManyToOne
    @JoinColumn(name = "idCurso", nullable = false)
    private Curso curso;
}
