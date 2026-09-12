package com.cyberchallenge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MELHORIA (ajuste de banco de dados): entidade de IDENTIDADE do
 * participante, separada da partida em si (cada jogada agora e um registro
 * "Partida" proprio, com FK para este Participante). Isso evita duplicar
 * nickname/idade/autoavaliacao a cada partida e permite aplicar a
 * restricao de unicidade do nickname num unico lugar.
 *
 * O jogo continua sem nenhum sistema de contas/login: nickname, idade e
 * autoavaliacao sao informados uma vez, antes da primeira partida daquela
 * "sessao" de uso, sem senha nem qualquer credencial.
 */
@Entity
@Table(name = "participantes")
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Restricao de unicidade tambem no banco (alem das validacoes de
    // frontend/backend) -- ver ParticipanteService para a mensagem de erro
    // amigavel exibida quando o nickname ja esta em uso.
    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    private Integer idade;

    // Autoavaliacao de conhecimento em ciberseguranca, de 1 a 10
    private Integer autoavaliacao;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partida> partidas = new ArrayList<>();

    public Participante() {}

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
    public Integer getAutoavaliacao() { return autoavaliacao; }
    public void setAutoavaliacao(Integer autoavaliacao) { this.autoavaliacao = autoavaliacao; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public List<Partida> getPartidas() { return partidas; }
    public void setPartidas(List<Partida> partidas) { this.partidas = partidas; }
}
