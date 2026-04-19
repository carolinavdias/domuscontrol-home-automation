package model.users;

import model.automations.Scenario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// um utilizador pode ter acesso a varias casas com papeis diferentes
// por exemplo pode ser admin numa casa e regular noutra (secao 3 do enunciado)

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private String email;
    private String password;

    // associa o id de cada casa ao papel que o utilizador tem nessa casa
    private Map<String, Role> casas;

    // os cenarios sao do utilizador, nao da casa! (rever na secçao 6 do enunciado)
    private List<Scenario> cenarios;

    //======================== =)

    // construtor principal
    public User(String id, String nome, String email, String password) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.casas = new HashMap<>();
        this.cenarios = new ArrayList<>();
    }

    // construtor de cópia
    public User(User outro) {
        this.id = outro.id;
        this.nome = outro.nome;
        this.email = outro.email;
        this.password = outro.password;
        this.casas = new HashMap<>(outro.casas);
        this.cenarios = new ArrayList<>(outro.cenarios);
    }

    // associa o utilizador a uma casa com um determinado papel
    public void adicionarCasa(String idCasa, Role papel) {
        if (idCasa == null || idCasa.isBlank())
            throw new IllegalArgumentException("O id da casa não pode ser vazio.");
        if (papel == null)
            throw new IllegalArgumentException("O papel não pode ser null.");
        this.casas.put(idCasa, papel);
    }

    // remove o acesso do utilizador a uma casa
    public void removerCasa(String idCasa) {
        this.casas.remove(idCasa);
    }

    // verifica se o utilizador tem acesso a uma casa (******verificar se faz parte !)
    public boolean temAcessoACasa(String idCasa) {
        return this.casas.containsKey(idCasa);
    }

    // verifica se o utilizador e admin numa casa especifica
    public boolean isAdmin(String idCasa) {
        return this.casas.getOrDefault(idCasa, Role.REGULAR) == Role.ADMIN;
    }

    // devolve o papel do utilizador numa casa
    public Role getPapel(String idCasa) {
        return this.casas.get(idCasa);
    }

    // adiciona um cenario ao utilizador
    public void adicionarCenario(Scenario c) {
        if (c == null)
            throw new IllegalArgumentException("O cenário não pode ser null.");
        this.cenarios.add(c);
    }

    // remove um cenario pelo nome
    public boolean removerCenario(String nomeCenario) {
        return this.cenarios.removeIf(c -> c.getNome().equals(nomeCenario));
    }

    // devolve uma copia da lista de cenarios
    public List<Scenario> getCenarios() {
        return new ArrayList<>(this.cenarios);
    }

    // devolve uma copia do mapa de casas
    public Map<String, Role> getCasas() {
        return new HashMap<>(this.casas);
    }

    // getters e setters
    public String getId()       { return id; }
    public String getNome()     { return nome; }
    public String getEmail()    { return email; }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        this.nome = nome;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("O email não pode ser vazio.");
        this.email = email;
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("A password não pode ser vazia.");
        this.password = password;
    }

    // verifica se a password esta correta, sem nunca a expor diretamente
    public boolean verificarPassword(String password) {
        return this.password.equals(password);
    }

    @Override
    public String toString() {
        return String.format("Utilizador: %s | Email: %s | Casas: %d | Cenários: %d",
            nome, email, casas.size(), cenarios.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User u = (User) o;
        return this.id.equals(u.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

// representa um utilizador do sistema
// os cenarios pertencem ao utilizador (secao 6 do enunciado) !
