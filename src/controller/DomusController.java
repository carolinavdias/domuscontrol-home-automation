package controller;

import model.house.House;
import model.users.User;
import utils.SimulatedClock;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// classe principal do sistema, ainda em construçao!! :)
// por agora só tem o básico de utilizadores e casas
public class DomusController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, User> utilizadores;
    private Map<String, House> casas;
    private SimulatedClock relogio;

    public DomusController() {
        this.utilizadores = new HashMap<>();
        this.casas = new HashMap<>();
        this.relogio = new SimulatedClock();
    }

    // registar um utilizador no sistema
    public void registarUtilizador(User u) {
        if (u == null)
            throw new IllegalArgumentException("O utilizador não pode ser null.");
        utilizadores.put(u.getId(), new User(u));
    }

    //===========================================================
    // TO DO:
    // autenticacao
    // gestao de casas e divisoes
    // gestao de dispositivos
    // estatisticas
    // gravar e carregar estado
}