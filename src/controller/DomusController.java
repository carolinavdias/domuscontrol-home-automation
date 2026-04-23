package controller;

import exceptions.DeviceNotFoundException;
import exceptions.HouseNotFoundException;
import exceptions.InvalidPermissionException;
import exceptions.RoomNotFoundException;
import exceptions.UserNotFoundException;
import model.devices.Device;
import model.house.House;
import model.house.Room;
import model.users.Role;
import model.users.User;
import utils.SimulatedClock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import exceptions.DeviceNotFoundException;
import exceptions.RoomNotFoundException;
import model.devices.Device;

// classe principal do sistema, ainda em construçao!! :)
// por agora trata de UTILIZADORES, CASAS e DIVISOES
public class DomusController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, User> utilizadores;
    private Map<String, House> casas;
    private SimulatedClock relogio;

    // id do utilizador que esta autenticado neste momento
    private String idUtilizadorAtual;

    public DomusController() {
        this.utilizadores = new HashMap<>();
        this.casas = new HashMap<>();
        this.relogio = new SimulatedClock();
        this.idUtilizadorAtual = null;
    }


    // GESTAO DE UTILIZADORES

    // regista um utilizador no sistema
    public void registarUtilizador(User u) {
        if (u == null)
            throw new IllegalArgumentException("O utilizador não pode ser null.");
        if (utilizadores.containsKey(u.getId()))
            throw new IllegalArgumentException("Já existe um utilizador com esse id.");
        utilizadores.put(u.getId(), new User(u));
    }

    // autentica um utilizador com id e password
    public boolean autenticar(String id, String password) {
        User u = utilizadores.get(id);
        if (u != null && u.verificarPassword(password)) {
            this.idUtilizadorAtual = id;
            return true;
        }
        return false;
    }

    // termina a sessao do utilizador que esta autenticado
    public void terminarSessao() {
        this.idUtilizadorAtual = null;
    }

    // devolve o utilizador que esta atualmente autenticado
    public User getUtilizadorAtual() throws UserNotFoundException {
        if (idUtilizadorAtual == null)
            throw new UserNotFoundException("Nenhum utilizador autenticado.");
        return new User(utilizadores.get(idUtilizadorAtual));
    }



    // GESTAO DE CASAS

    // cria uma nova casa, quem a cria fica automaticamente admin dela
    public void criarCasa(House h) throws UserNotFoundException {
        User atual = utilizadores.get(idUtilizadorAtual);
        if (atual == null)
            throw new UserNotFoundException("Nenhum utilizador autenticado.");

        if (casas.containsKey(h.getId()))
            throw new IllegalArgumentException("Já existe uma casa com esse id.");

        casas.put(h.getId(), new House(h));

        // o utilizador que criou a casa fica admin dela
        utilizadores.get(idUtilizadorAtual).adicionarCasa(h.getId(), Role.ADMIN);
    }

    // devolve uma casa pelo id
    public House getCasa(String id) throws HouseNotFoundException {
        House h = casas.get(id);
        if (h == null) throw new HouseNotFoundException(id);
        return new House(h);
    }

    // devolve as casas a que o utilizador atual tem acesso
    public List<House> getCasasDoUtilizadorAtual() throws UserNotFoundException {
        User atual = utilizadores.get(idUtilizadorAtual);
        if (atual == null)
            throw new UserNotFoundException("Nenhum utilizador autenticado.");

        return atual.getCasas().keySet().stream()
            .filter(casas::containsKey)
            .map(id -> new House(casas.get(id)))
            .collect(Collectors.toList());
    }



    // GESTAO DE DIVISOES

    // adiciona uma divisao a uma casa, so admins podem fazer isto
    public void adicionarDivisao(String idCasa, Room r)
            throws HouseNotFoundException, InvalidPermissionException, UserNotFoundException {

        verificarPermissaoAdmin(idCasa);

        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);

        h.adicionarDivisao(r);
    }


    // SIMULAÇÃO DO TEMPO

    public void avancarTempo(long minutos) {
        relogio.avancarMinutos(minutos);
    }


    // GESTAO DE DISPOSITIVOS

    // adiciona um dispositivo a uma divisao de uma casa
    // so admins podem fazer isto!! 
    public void adicionarDispositivo(String idCasa, String idDivisao, Device d)
            throws HouseNotFoundException, RoomNotFoundException,
                   InvalidPermissionException, UserNotFoundException {

        verificarPermissaoAdmin(idCasa);

        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);

        Room r = h.getDivisao(idDivisao);
        if (r == null) throw new RoomNotFoundException(idDivisao);

        r.adicionarDispositivo(d);
    }

    // liga um dispositivo especifico numa divisao
    public void ligarDispositivo(String idCasa, String idDivisao, String idDispositivo)
            throws HouseNotFoundException, RoomNotFoundException, DeviceNotFoundException {

        Device d = getDispositivoInterno(idCasa, idDivisao, idDispositivo);
        d.ligar(relogio.getDataHoraAtual());
    }

    // desliga um dispositivo especifico numa divisao
    public void desligarDispositivo(String idCasa, String idDivisao, String idDispositivo)
            throws HouseNotFoundException, RoomNotFoundException, DeviceNotFoundException {

        Device d = getDispositivoInterno(idCasa, idDivisao, idDispositivo);
        d.desligar(relogio.getDataHoraAtual());
    }

    // método interno para ir buscar o dispositivo de forma direta
    // nao devolve copia porque precisamos de alterar mesmo o estado dele
    private Device getDispositivoInterno(String idCasa, String idDivisao, String idDispositivo)
            throws HouseNotFoundException, RoomNotFoundException, DeviceNotFoundException {

        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);

        Room r = h.getDivisao(idDivisao);
        if (r == null) throw new RoomNotFoundException(idDivisao);

        Device d = r.getDispositivo(idDispositivo);
        if (d == null) throw new DeviceNotFoundException(idDispositivo);

        return d;
    }


    //===========================================================
    // TO DO:
    // estatisticas (casa que mais consome, top 3 dispositivos, top 3 divisoes)
    // automacoes, escalonamentos e execucao de cenarios
    // gravar e carregar estado em ficheiro binario
    //===========================================================


    
    // METODO AUXILIAR DE PERMISSOES

    // verifica se o utilizador atual e admin da casa indicada
    private void verificarPermissaoAdmin(String idCasa)
            throws InvalidPermissionException, UserNotFoundException {

        User atual = utilizadores.get(idUtilizadorAtual);
        if (atual == null)
            throw new UserNotFoundException("Nenhum utilizador autenticado.");

        if (!atual.isAdmin(idCasa))
            throw new InvalidPermissionException(
                "Não tens permissão de administrador para esta casa.");
    }
}