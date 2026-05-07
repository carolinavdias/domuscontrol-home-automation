package model.automations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// uma automacao e uma condicao que quando verificada executa acoes sobre dispositivos
// por exemplo se esta a chover entao fecha as cortinas (secao 4 do enunciado)
public class Automation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private String descricaoCondicao;
    private boolean ativa;

    // id do sensor que esta automacao observa
    private String idSensor;

    // valor a partir do qual a condicao e verdadeira
    private double limiar;

    // acao a executar nos dispositivos: "ligar" ou "desligar"
    private String acao;

    private List<String> idsDispositivos;

    public Automation(String id, String nome, String descricaoCondicao,
                      String idSensor, double limiar, String acao) {
        this.id = id;
        this.nome = nome;
        this.descricaoCondicao = descricaoCondicao;
        this.idSensor = idSensor;
        this.limiar = limiar;
        this.acao = acao;
        this.ativa = true;
        this.idsDispositivos = new ArrayList<>();
    }

    public Automation(Automation outra) {
        this.id = outra.id;
        this.nome = outra.nome;
        this.descricaoCondicao = outra.descricaoCondicao;
        this.idSensor = outra.idSensor;
        this.limiar = outra.limiar;
        this.acao = outra.acao;
        this.ativa = outra.ativa;
        this.idsDispositivos = new ArrayList<>(outra.idsDispositivos);
    }

    public void adicionarDispositivo(String idDispositivo) {
        if (idDispositivo == null || idDispositivo.isBlank())
            throw new IllegalArgumentException("O id do dispositivo não pode ser vazio.");
        this.idsDispositivos.add(idDispositivo);
    }

    // verifica se o valor atual do sensor ultrapassa o limiar definido
    public boolean avaliarCondicao(double valorSensor) {
        return valorSensor >= limiar;
    }

    public String getId()                    { return id; }
    public String getNome()                  { return nome; }
    public String getDescricaoCondicao()     { return descricaoCondicao; }
    public boolean isAtiva()                 { return ativa; }
    public String getIdSensor()              { return idSensor; }
    public double getLimiar()                { return limiar; }
    public String getAcao()                  { return acao; }

    public List<String> getIdsDispositivos() {
        return new ArrayList<>(idsDispositivos);
    }

    public void setAtiva(boolean ativa)      { this.ativa = ativa; }

    @Override
    public String toString() {
        return String.format("Automação: %s | Condição: %s | Ativa: %s | Dispositivos: %d",
            nome, descricaoCondicao, ativa ? "sim" : "não", idsDispositivos.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Automation)) return false;
        Automation a = (Automation) o;
        return this.id.equals(a.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}