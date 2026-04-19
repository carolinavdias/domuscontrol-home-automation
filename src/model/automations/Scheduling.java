package model.automations;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// um escalonamento e uma regra baseada no tempo
// fica associado a casa tal como as automacoes
public class Scheduling implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;

    // hora a que o escalonamento é executado
    private LocalTime horaExecucao;

    // hora a que o escalonamento termina. 
    // pode ser null se for só um momento !
    private LocalTime horaFim;

    // se true executa todos os dias, se false executa SÓ UMA VEZ
    private boolean repeteDaily;
    private boolean ativo;

    // true significa ligar, false significa desligar
    private boolean ligar;

    // ids dos dispositivos afetados por este escalonamento
    private List<String> idsDispositivos;

    // construtor principal
    public Scheduling(String id, String nome, LocalTime horaExecucao,
                      LocalTime horaFim, boolean repeteDaily, boolean ligar) {
        this.id = id;
        this.nome = nome;
        this.horaExecucao = horaExecucao;
        this.horaFim = horaFim;
        this.repeteDaily = repeteDaily;
        this.ligar = ligar;
        this.ativo = true;
        this.idsDispositivos = new ArrayList<>();
    }

    // construtor de cópia
    public Scheduling(Scheduling outro) {
        this.id = outro.id;
        this.nome = outro.nome;
        this.horaExecucao = outro.horaExecucao;
        this.horaFim = outro.horaFim;
        this.repeteDaily = outro.repeteDaily;
        this.ligar = outro.ligar;
        this.ativo = outro.ativo;
        this.idsDispositivos = new ArrayList<>(outro.idsDispositivos);
    }

    // adiciona um dispositivo a lista de dispositivos afetados
    public void adicionarDispositivo(String idDispositivo) {
        if (idDispositivo == null || idDispositivo.isBlank())
            throw new IllegalArgumentException("O id do dispositivo não pode ser vazio.");
        this.idsDispositivos.add(idDispositivo);
    }

    // verifica se este escalonamento deve ser executado agora
    public boolean deveExecutar(LocalTime horaAtual) {
        return this.ativo && horaAtual.getHour() == horaExecucao.getHour()
                          && horaAtual.getMinute() == horaExecucao.getMinute();
    }

    // geters e setters 
    public String getId()              { return id; }
    public String getNome()            { return nome; }
    public LocalTime getHoraExecucao() { return horaExecucao; }
    public LocalTime getHoraFim()      { return horaFim; }
    public boolean isRepeteDaily()     { return repeteDaily; }
    public boolean isAtivo()           { return ativo; }
    public boolean isLigar()           { return ligar; }

    public List<String> getIdsDispositivos() {
        return new ArrayList<>(idsDispositivos);
    }

    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return String.format("Escalonamento: %s | Hora: %s | Repete diariamente: %s | Ativo: %s",
            nome, horaExecucao.toString(), repeteDaily ? "sim" : "não", ativo ? "sim" : "não");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scheduling)) return false;
        Scheduling s = (Scheduling) o;
        return this.id.equals(s.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}


//exemplo de escalonamento: todas as manhas as 7h abrir as cortinas (está na secao 5 do enunciado)
