package model.house;

import model.devices.Device;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// representa uma divisao da casa, por exemplo sala, cozinha, quarto, etc...
// cada divisao tem uma lista de dispositivos associados
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private List<Device> dispositivos;

    // construtor principal
    public Room(String id, String nome) {
        this.id = id;
        this.nome = nome;
        this.dispositivos = new ArrayList<>();
    }

    // construtor de copia
    public Room(Room outra) {
        this.id = outra.id;
        this.nome = outra.nome;
        // copia cada dispositivo individualmente para nao expor referencias internas
        this.dispositivos = new ArrayList<>();
        for (Device d : outra.dispositivos) {
            this.dispositivos.add(d.clone());
        }
    }

    // adiciona um dispositivo a esta divisao
    public void adicionarDispositivo(Device d) {
        if (d == null)
            throw new IllegalArgumentException("O dispositivo nao pode ser null.");
        this.dispositivos.add(d.clone());
    }

    // remove um dispositivo pelo id
    public boolean removerDispositivo(String idDispositivo) {
        return this.dispositivos.removeIf(d -> d.getId().equals(idDispositivo));
    }

    // verifica se um dispositivo existe nesta divisao
    public boolean existeDispositivo(String idDispositivo) {
        return this.dispositivos.stream()
            .anyMatch(d -> d.getId().equals(idDispositivo));
    }

    // devolve um dispositivo pelo id, ou null se nao existir
    public Device getDispositivo(String idDispositivo) {
        return this.dispositivos.stream()
            .filter(d -> d.getId().equals(idDispositivo))
            .findFirst()
            .orElse(null);
    }

    // devolve uma copia da lista de dispositivos
    public List<Device> getDispositivos() {
        List<Device> copia = new ArrayList<>();
        for (Device d : this.dispositivos) {
            copia.add(d.clone());
        }
        return copia;
    }

    // quantos dispositivos tem esta divisao
    public int getNumDispositivos() {
        return this.dispositivos.size();
    }

    // calcula o consumo total de todos os dispositivos desta divisao
    public double getConsumoTotal() {
        double total = 0;
        for (Device d : this.dispositivos) {
            total += d.getConsumoTotal();
        }
        return total;
    }

    // getters e setters
    public String getId()   { return id; }
    public String getNome() { return nome; }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("O nome da divisao nao pode ser vazio.");
        this.nome = nome;
    }

    @Override
    public String toString() {
        return String.format("Divisao: %s | Dispositivos: %d | Consumo total: %.1f Wh",
            nome, dispositivos.size(), getConsumoTotal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room r = (Room) o;
        return this.id.equals(r.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}