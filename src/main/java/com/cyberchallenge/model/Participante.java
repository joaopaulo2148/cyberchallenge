package com.cyberchallenge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade de IDENTIDADE do participante (conta), separada da partida em
 * si (cada jogada e um registro "Partida" proprio, com FK para este
 * Participante). Isso evita duplicar nickname/idade/autoavaliacao a cada
 * partida e permite aplicar a restricao de unicidade do nickname num
 * unico lugar.
 *
 * O sistema de contas e propositalmente simples: nickname + senha, sem
 * e-mail, sem verificacao de conta, sem recuperacao de senha e sem um
 * mecanismo de sessao no servidor (ver README, secao "Autenticacao", para
 * a explicacao completa do funcionamento e das limitacoes assumidas
 * conscientemente por essa simplicidade).
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

    // Hash da senha (BCrypt). NUNCA armazenar a senha em texto puro, e
    // NUNCA incluir este campo em nenhum DTO de resposta da API.
    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

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
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public List<Partida> getPartidas() { return partidas; }
    public void setPartidas(List<Partida> partidas) { this.partidas = partidas; }
}
