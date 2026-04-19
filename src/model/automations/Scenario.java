package model.automations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


// os cenarios pertencem ao utilizador 
public class Scenario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private String descricao;

    // cada acao e um par com o id do dispositivo e o que fazer
    // true significa ligar, false significa desligar
    private List<String[]> acoes;

    // construtor principal
    public Scenario(String id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.acoes = new ArrayList<>();
    }

    // construtor de copia
    public Scenario(Scenario outro) {
        this.id = outro.id;
        this.nome = outro.nome;
        this.descricao = outro.descricao;
        this.acoes = new ArrayList<>();
        for (String[] acao : outro.acoes) {
            this.acoes.add(acao.clone());
        }
    }

    // adiciona uma acao ao cenario
    // idDispositivo e o dispositivo a atuar
    // acao e "ligar" ou "desligar"
    public void adicionarAcao(String idDispositivo, String acao) {
        if (idDispositivo == null || idDispositivo.isBlank())
            throw new IllegalArgumentException("O id do dispositivo não pode ser vazio.");
        if (!acao.equals("ligar") && !acao.equals("desligar"))
            throw new IllegalArgumentException("A ação tem de ser ligar ou desligar.");
        this.acoes.add(new String[]{idDispositivo, acao});
    }

    // devolve uma copia da lista de acoes
    public List<String[]> getAcoes() {
        List<String[]> copia = new ArrayList<>();
        for (String[] acao : this.acoes) {
            copia.add(acao.clone());
        }
        return copia;
    }

    // getters
    public String getId()        { return id; }
    public String getNome()      { return nome; }
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return String.format("Cenário: %s | %s | Ações: %d",
            nome, descricao, acoes.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scenario)) return false;
        Scenario s = (Scenario) o;
        return this.id.equals(s.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

//=========================================
//secçãao 6 do enunciado --> cenarios