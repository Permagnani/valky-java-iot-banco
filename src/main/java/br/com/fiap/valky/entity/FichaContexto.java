package br.com.fiap.valky.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_ficha_contexto")
public class FichaContexto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String informacoesImportantes;

    @Column(length = 1000)
    private String orientacoesEmergencia;

    // Campos fixos da ficha (todos opcionais)
    @Column(name = "nome_agressor", length = 120)
    private String nomeAgressor;

    @Column(name = "relacao_agressor", length = 60)
    private String relacaoAgressor;

    @Column(name = "possui_arma")
    private Boolean possuiArma;

    @Column(length = 200)
    private String veiculo;

    @Column(name = "enderecos_conhecidos", length = 500)
    private String enderecosConhecidos;

    @Column(name = "numero_bo", length = 40)
    private String numeroBo;

    @Column(name = "medida_protetiva")
    private Boolean medidaProtetiva;

    // Resumo gerado pela IA (Spring AI) de forma assíncrona
    @Column(name = "resumo_ia", length = 2000)
    private String resumoIa;

    @Enumerated(EnumType.STRING)
    @Column(name = "resumo_status", length = 20)
    private ResumoStatus resumoStatus;

    @Column(name = "resumo_gerado_em")
    private LocalDateTime resumoGeradoEm;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    public FichaContexto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInformacoesImportantes() {
        return informacoesImportantes;
    }

    public void setInformacoesImportantes(String informacoesImportantes) {
        this.informacoesImportantes = informacoesImportantes;
    }

    public String getOrientacoesEmergencia() {
        return orientacoesEmergencia;
    }

    public void setOrientacoesEmergencia(String orientacoesEmergencia) {
        this.orientacoesEmergencia = orientacoesEmergencia;
    }

    public String getNomeAgressor() {
        return nomeAgressor;
    }

    public void setNomeAgressor(String nomeAgressor) {
        this.nomeAgressor = nomeAgressor;
    }

    public String getRelacaoAgressor() {
        return relacaoAgressor;
    }

    public void setRelacaoAgressor(String relacaoAgressor) {
        this.relacaoAgressor = relacaoAgressor;
    }

    public Boolean getPossuiArma() {
        return possuiArma;
    }

    public void setPossuiArma(Boolean possuiArma) {
        this.possuiArma = possuiArma;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public String getEnderecosConhecidos() {
        return enderecosConhecidos;
    }

    public void setEnderecosConhecidos(String enderecosConhecidos) {
        this.enderecosConhecidos = enderecosConhecidos;
    }

    public String getNumeroBo() {
        return numeroBo;
    }

    public void setNumeroBo(String numeroBo) {
        this.numeroBo = numeroBo;
    }

    public Boolean getMedidaProtetiva() {
        return medidaProtetiva;
    }

    public void setMedidaProtetiva(Boolean medidaProtetiva) {
        this.medidaProtetiva = medidaProtetiva;
    }

    public String getResumoIa() {
        return resumoIa;
    }

    public void setResumoIa(String resumoIa) {
        this.resumoIa = resumoIa;
    }

    public ResumoStatus getResumoStatus() {
        return resumoStatus;
    }

    public void setResumoStatus(ResumoStatus resumoStatus) {
        this.resumoStatus = resumoStatus;
    }

    public LocalDateTime getResumoGeradoEm() {
        return resumoGeradoEm;
    }

    public void setResumoGeradoEm(LocalDateTime resumoGeradoEm) {
        this.resumoGeradoEm = resumoGeradoEm;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
