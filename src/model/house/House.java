package model.house;

import model.automations.Automation;
import model.automations.Scheduling;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// representa uma casa no sistema
// uma casa tem divisoes, e a cada divisao estao associados dispositivos
// as automacoes e os escalonamentos ficam associados a casa (secao 4 e 5 do enunciado)
public class House implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private String morada;
    private List<Room> divisoes;
    private List<Automation> automacoes;
    private List<Scheduling> escalonamentos;

    // construtor principal
    public House(String id, String nome, String morada) {
        this.id = id;
        this.nome = nome;
        this.morada = morada;
        this.divisoes = new ArrayList<>();
        this.automacoes = new ArrayList<>();
        this.escalonamentos = new ArrayList<>();
    }

    // construtor de copia
    public House(House outra) {
        this.id = outra.id;
        this.nome = outra.nome;
        this.morada = outra.morada;
        this.divisoes = new ArrayList<>();
        for (Room r : outra.divisoes) {
            this.divisoes.add(new Room(r));
        }
        this.automacoes = new ArrayList<>(outra.automacoes);
        this.escalonamentos = new ArrayList<>(outra.escalonamentos);
    }

    // adiciona uma divisao a casa
    public void adicionarDivisao(Room r) {
        if (r == null)
            throw new IllegalArgumentException("A divisao não pode ser null.");
        if (existeDivisao(r.getId()))
            throw new IllegalArgumentException("Já existe uma divisão com esse id.");
        this.divisoes.add(new Room(r));
    }

    // remove uma divisao pelo id
    public boolean removerDivisao(String idDivisao) {
        return this.divisoes.removeIf(r -> r.getId().equals(idDivisao));
    }

    // verifica se uma divisao existe nesta casa
    public boolean existeDivisao(String idDivisao) {
        return this.divisoes.stream()
            .anyMatch(r -> r.getId().equals(idDivisao));
    }

    // devolve uma divisao pelo id, ou null se nao existir
    public Room getDivisao(String idDivisao) {
        return this.divisoes.stream()
            .filter(r -> r.getId().equals(idDivisao))
            .findFirst()
            .orElse(null);
    }

    // devolve uma copia da lista de divisoes
    public List<Room> getDivisoes() {
        List<Room> copia = new ArrayList<>();
        for (Room r : this.divisoes) {
            copia.add(new Room(r));
        }
        return copia;
    }

    // calcula o consumo total de toda a casa
    // soma o consumo de todas as divisoes
    public double getConsumoTotal() {
        double total = 0;
        for (Room r : this.divisoes) {
            total += r.getConsumoTotal();
        }
        return total;
    }

    // quantas divisoes tem a casa
    public int getNumDivisoes() {
        return this.divisoes.size();
    }

    // adiciona uma automacao a esta casa
    public void adicionarAutomacao(Automation a) {
        if (a == null)
            throw new IllegalArgumentException("A automação não pode ser null.");
        this.automacoes.add(a);
    }

    // adiciona um escalonamento a esta casa
    public void adicionarEscalonamento(Scheduling s) {
        if (s == null)
            throw new IllegalArgumentException("O escalonamento não pode ser null.");
        this.escalonamentos.add(s);
    }

    // devolve copia das listas de automacoes e escalonamentos
    public List<Automation> getAutomacoes() {
        return new ArrayList<>(this.automacoes);
    }

    public List<Scheduling> getEscalonamentos() {
        return new ArrayList<>(this.escalonamentos);
    }

    // getters e setters
    public String getId()      { return id; }
    public String getNome()    { return nome; }
    public String getMorada()  { return morada; }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("O nome da casa não pode ser vazio.");
        this.nome = nome;
    }

    public void setMorada(String morada) {
        if (morada == null || morada.isBlank())
            throw new IllegalArgumentException("A morada não pode ser vazia.");
        this.morada = morada;
    }

    @Override
    public String toString() {
        return String.format("Casa: %s | Morada: %s | Divisões: %d | Consumo total: %.1f Wh",
            nome, morada, divisoes.size(), getConsumoTotal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof House)) return false;
        House h = (House) o;
        return this.id.equals(h.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}