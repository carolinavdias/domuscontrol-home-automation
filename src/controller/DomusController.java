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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    //==========================================================
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


    //==========================================================
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


    //==========================================================
    // GESTAO DE DIVISOES

    // adiciona uma divisao a uma casa, so admins podem fazer isto
    public void adicionarDivisao(String idCasa, Room r)
            throws HouseNotFoundException, InvalidPermissionException, UserNotFoundException {

        verificarPermissaoAdmin(idCasa);

        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);

        h.adicionarDivisao(r);
    }

    //==========================================================
    // SIMULAÇÃO DO TEMPO

    public void avancarTempo(long minutos) {
        relogio.avancarMinutos(minutos);
    }


    //==========================================================    
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


    //==========================================================
    // ESTATÍSTICAS

    // devolve a casa que mais consome energia em todo o sistema
    public House getCasaQueMaisConsome() {
        return casas.values().stream()
            .max(Comparator.comparingDouble(House::getConsumoTotal))
            .map(House::new)
            .orElse(null);
    }

    // devolve os 3 dispositivos mais utilizados de uma casa, por número de activações
    public List<Device> getTop3DispositivosPorActivacoes(String idCasa)
            throws HouseNotFoundException {

        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);

        // junta todos os dispositivos de todas as divisões da casa
        List<Device> todos = new ArrayList<>();
        for (Room r : h.getDivisoes()) {
            todos.addAll(r.getDispositivos());
        }

        // ordena por número de activações, do maior para o menor, e devolve os 3 primeiros
        return todos.stream()
            .sorted(Comparator.comparingInt(Device::getNumActivacoes).reversed())
            .limit(3)
            .collect(Collectors.toList());
    }

    // devolve os 3 dispositivos mais utilizados de uma casa, por tempo total ligado
    public List<Device> getTop3DispositivosPorTempo(String idCasa)
            throws HouseNotFoundException {

        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);

        List<Device> todos = new ArrayList<>();
        for (Room r : h.getDivisoes()) {
            todos.addAll(r.getDispositivos());
        }

        return todos.stream()
            .sorted(Comparator.comparingLong(Device::getTotalMinutosLigado).reversed())
            .limit(3)
            .collect(Collectors.toList());
    }

    // devolve as 3 divisões com mais dispositivos em todo o sistema
    // inclui o nome da casa a que cada divisão pertence
    public List<String> getTop3DivisoesPorDispositivos() {
        List<String[]> pares = new ArrayList<>();

        // percorre todas as casas e todas as divisões e guarda pares (descrição, nº dispositivos)
        for (House h : casas.values()) {
            for (Room r : h.getDivisoes()) {
                pares.add(new String[]{
                    h.getNome() + " > " + r.getNome(),
                    String.valueOf(r.getNumDispositivos())
                });
            }
        }

        // ordena pelo número de dispositivos, do maior para o menor, e devolve os 3 primeiros
        return pares.stream()
            .sorted((a, b) -> Integer.compare(
                Integer.parseInt(b[1]), Integer.parseInt(a[1])))
            .limit(3)
            .map(p -> p[0] + " | Dispositivos: " + p[1])
            .collect(Collectors.toList());
    }

    // método de consulta extra: consumo total de uma casa específica
    public double getConsumoTotalDaCasa(String idCasa) throws HouseNotFoundException {
        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);
        return h.getConsumoTotal();
    }


    //===========================================================
    // TO DO:
    // automações, escalonamentos e execução de cenários
    // gravar e carregar estado em ficheiro binário
    //===========================================================

    

    //==========================================================
    // METODO AUXILIAR DE PERMISSOES

    // verifica se o utilizador atual e admin da casa indicada
    private void verificarPermissaoAdmin(String idCasa)
            throws InvalidPermissionException, UserNotFoundException {

        User atual = utilizadores.get(idUtilizadorAtual);
        if (atual == null)
            throw new UserNotFoundException("Nenhum utilizador autenticado.");

        if (!atual.isAdmin(idCasa))
            throw new InvalidPermissionException(
                "Não tens permissão de administrador para esta casa!");
    }
}