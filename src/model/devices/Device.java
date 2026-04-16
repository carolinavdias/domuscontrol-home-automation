package model.devices;

import java.io.Serializable;
import java.time.LocalDateTime;


//classe base de todos os dispositivos do sistema

//qualquer dispositivo novo que queiramos adicionar tem de herdar desta classe
public abstract class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    //atributos que todos os dispositivos têm (secção 2 do enunciado)
    private String id;
    private String marca;
    private String modelo;
    private double consumoWh;
    private boolean ligado;

    //necessários para as estatísticas (secção 8.2 do enunciado)
    //quantas vezes foi ligado e quanto tempo esteve ligado no total
    private long totalMinutosLigado;
    private int numActivacoes;
    private LocalDateTime ultimaActivacao;


    //construtor principal
    public Device(String id, String marca, String modelo, double consumoWh) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.consumoWh = consumoWh;
        this.ligado = false;
        this.totalMinutosLigado = 0;
        this.numActivacoes = 0;
        this.ultimaActivacao = null;
    }

    // construtor de cópia, importante para nunca expor os objetos internos
    public Device(Device outro) {
        this.id = outro.id;
        this.marca = outro.marca;
        this.modelo = outro.modelo;
        this.consumoWh = outro.consumoWh;
        this.ligado = outro.ligado;
        this.totalMinutosLigado = outro.totalMinutosLigado;
        this.numActivacoes = outro.numActivacoes;
        this.ultimaActivacao = outro.ultimaActivacao;
    }


    //liga o dispositivo e regista o momento em que foi ligado
    //só faz alguma coisa se o dispositivo estiver desligado
    public void ligar(LocalDateTime agora) {
        if (!this.ligado) {
            this.ligado = true;
            this.numActivacoes++;
            this.ultimaActivacao = agora;
        }
    }

    //desliga o dispositivo e atualiza o tempo total que esteve ligado
    public void desligar(LocalDateTime agora) {
        if (this.ligado) {
            this.ligado = false;
            if (this.ultimaActivacao != null) {
                long minutos = java.time.Duration
                    .between(this.ultimaActivacao, agora)
                    .toMinutes();
                this.totalMinutosLigado += minutos;
            }
            this.ultimaActivacao = null;
        }
    }

    //calcula o consumo total em Wh com base no tempo que esteve ligado
    public double getConsumoTotal() {
        return (totalMinutosLigado / 60.0) * consumoWh;
    }


    //métodos abstratos que cada subclasse tem de implementar à sua maneira
    //é assim que o polimorfismo funciona, o sistema nunca precisa de usar instanceof
    public abstract String getEstadoDetalhado();
    public abstract Device clone();


    //getters
    public String getId()                { return id; }
    public String getMarca()             { return marca; }
    public String getModelo()            { return modelo; }
    public double getConsumoWh()         { return consumoWh; }
    public boolean isLigado()            { return ligado; }
    public long getTotalMinutosLigado()  { return totalMinutosLigado; }
    public int getNumActivacoes()        { return numActivacoes; }

    //setters com validação básica
    public void setMarca(String marca) {
        if (marca == null || marca.isBlank())
            throw new IllegalArgumentException("A marca nao pode ser vazia.");
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        if (modelo == null || modelo.isBlank())
            throw new IllegalArgumentException("O modelo nao pode ser vazio.");
        this.modelo = modelo;
    }

    public void setConsumoWh(double consumoWh) {
        if (consumoWh < 0)
            throw new IllegalArgumentException("O consumo nao pode ser negativo.");
        this.consumoWh = consumoWh;
    }


    @Override
    public String toString() {
        return String.format(
            "[%s] %s %s | Consumo: %.1f Wh | Estado: %s | Activacoes: %d | Tempo ligado: %d min",
            id, marca, modelo, consumoWh,
            getEstadoDetalhado(),
            numActivacoes, totalMinutosLigado
        );
    }

    //dois dispositivos são iguais se tiverem o mesmo id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Device)) return false;
        Device d = (Device) o;
        return this.id.equals(d.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}