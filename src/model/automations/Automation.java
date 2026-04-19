package model.automations;

import model.devices.Device;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


// as automacoes ficam associadas a casa mas sao criadas pelos utilizadores
public class Automation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private String descricaoCondicao;
    private boolean ativa;

    // ids dos dispositivos que esta automação vai atuar quando a condição for verdade
    private List<String> idsDispositivos;

    // construtor principal
    public Automation(String id, String nome, String descricaoCondicao) {
        this.id = id;
        this.nome = nome;
        this.descricaoCondicao = descricaoCondicao;
        this.ativa = true;
        this.idsDispositivos = new ArrayList<>();
    }

    // construtor de copia
    public Automation(Automation outra) {
        this.id = outra.id;
        this.nome = outra.nome;
        this.descricaoCondicao = outra.descricaoCondicao;
        this.ativa = outra.ativa;
        this.idsDispositivos = new ArrayList<>(outra.idsDispositivos);
    }

    // adiciona um dispositivo a lista de dispositivos afetados por esta automacao
    public void adicionarDispositivo(String idDispositivo) {
        if (idDispositivo == null || idDispositivo.isBlank())
            throw new IllegalArgumentException("O id do dispositivo não pode ser vazio.");
        this.idsDispositivos.add(idDispositivo);
    }

    // verifica se a automacao deve ser ativada com base no valor do sensor
    // este metodo vai ser chamado pelo ->controller<- quando o sensor atualizar o valor (!!!)
    public boolean avaliarCondicao(double valorSensor, double limiar) {
        return valorSensor >= limiar;
    }

    // getters e setters
    public String getId()                  { return id; }
    public String getNome()                { return nome; }
    public String getDescricaoCondicao()   { return descricaoCondicao; }
    public boolean isAtiva()               { return ativa; }

    public List<String> getIdsDispositivos() {
        return new ArrayList<>(idsDispositivos);
    }

    public void setAtiva(boolean ativa)    { this.ativa = ativa; }

    @Override
    public String toString() {
        return String.format("Automação: %s | Condição: %s | Ativa: %s | Dispositivos afetados: %d",
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


// bg: uma automacao é uma condicao que quando verificada executa acoes sobre dispositivos
// por ex:se esta a chover entao fecha as cortinas (ver na secção 4 do enunciado)