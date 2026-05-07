package controller;

import exceptions.DeviceNotFoundException;
import exceptions.HouseNotFoundException;
import exceptions.InvalidPermissionException;
import exceptions.RoomNotFoundException;
import exceptions.UserNotFoundException;
import model.automations.Automation;
import model.automations.Scenario;
import model.automations.Scheduling;
import model.devices.Device;
import model.house.House;
import model.house.Room;
import model.users.Role;
import model.users.User;
import utils.SimulatedClock;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// classe central do sistema, gere tudo
public class DomusController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, User> utilizadores;
    private Map<String, House> casas;
    private SimulatedClock relogio;
    private String idUtilizadorAtual;

    public DomusController() {
        this.utilizadores = new HashMap<>();
        this.casas = new HashMap<>();
        this.relogio = new SimulatedClock();
        this.idUtilizadorAtual = null;
    }


    //==========================================================
    // UTILIZADORES

    public void registarUtilizador(User u) {
        if (u == null)
            throw new IllegalArgumentException("O utilizador não pode ser null.");
        if (utilizadores.containsKey(u.getId()))
            throw new IllegalArgumentException("Já existe um utilizador com esse id.");
        utilizadores.put(u.getId(), new User(u));
    }

    public boolean autenticar(String id, String password) {
        User u = utilizadores.get(id);
        if (u != null && u.verificarPassword(password)) {
            this.idUtilizadorAtual = id;
            return true;
        }
        return false;
    }

    public void terminarSessao() {
        this.idUtilizadorAtual = null;
    }

    public User getUtilizadorAtual() throws UserNotFoundException {
        if (idUtilizadorAtual == null)
            throw new UserNotFoundException("Nenhum utilizador autenticado.");
        return new User(utilizadores.get(idUtilizadorAtual));
    }

    public List<User> getTodosUtilizadores() {
        return utilizadores.values().stream()
            .map(User::new)
            .collect(Collectors.toList());
    }


    //==========================================================
    // CASAS

    // quem cria a casa fica automaticamente admin dela
    public void criarCasa(House h) throws UserNotFoundException {
        User atual = utilizadores.get(idUtilizadorAtual);
        if (atual == null)
            throw new UserNotFoundException("Nenhum utilizador autenticado.");
        if (casas.containsKey(h.getId()))
            throw new IllegalArgumentException("Já existe uma casa com esse id.");

        casas.put(h.getId(), new House(h));
        utilizadores.get(idUtilizadorAtual).adicionarCasa(h.getId(), Role.ADMIN);
    }

    public House getCasa(String id) throws HouseNotFoundException {
        House h = casas.get(id);
        if (h == null) throw new HouseNotFoundException(id);
        return new House(h);
    }

    public List<House> getCasasDoUtilizadorAtual() throws UserNotFoundException {
        User atual = utilizadores.get(idUtilizadorAtual);
        if (atual == null)
            throw new UserNotFoundException("Nenhum utilizador autenticado.");

        return atual.getCasas().keySet().stream()
            .filter(casas::containsKey)
            .map(id -> new House(casas.get(id)))
            .collect(Collectors.toList());
    }

    public List<House> getTodasCasas() {
        return casas.values().stream()
            .map(House::new)
            .collect(Collectors.toList());
    }


    //==========================================================
    // DIVISÕES

    // só admins podem adicionar divisões
    public void adicionarDivisao(String idCasa, Room r)
            throws HouseNotFoundException, InvalidPermissionException, UserNotFoundException {

        verificarPermissaoAdmin(idCasa);

        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);

        h.adicionarDivisao(r);
    }

    public Room getDivisao(String idCasa, String idDivisao)
            throws HouseNotFoundException, RoomNotFoundException {

        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);

        Room r = h.getDivisao(idDivisao);
        if (r == null) throw new RoomNotFoundException(idDivisao);

        return new Room(r);
    }


    //==========================================================
    // DISPOSITIVOS

    // só admins podem adicionar dispositivos
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

    public void ligarDispositivo(String idCasa, String idDivisao, String idDispositivo)
            throws HouseNotFoundException, RoomNotFoundException, DeviceNotFoundException {
        Device d = getDispositivoInterno(idCasa, idDivisao, idDispositivo);
        d.ligar(relogio.getDataHoraAtual());
    }

    public void desligarDispositivo(String idCasa, String idDivisao, String idDispositivo)
            throws HouseNotFoundException, RoomNotFoundException, DeviceNotFoundException {
        Device d = getDispositivoInterno(idCasa, idDivisao, idDispositivo);
        d.desligar(relogio.getDataHoraAtual());
    }

    // devolve referência direta, não cópia, porque precisamos alterar o estado
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

    public House getCasaQueMaisConsome() {
        return casas.values().stream()
            .max(Comparator.comparingDouble(House::getConsumoTotal))
            .map(House::new)
            .orElse(null);
    }

    public List<Device> getTop3DispositivosPorActivacoes(String idCasa)
            throws HouseNotFoundException {

        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);

        List<Device> todos = new ArrayList<>();
        for (Room r : h.getDivisoes())
            todos.addAll(r.getDispositivos());

        return todos.stream()
            .sorted(Comparator.comparingInt(Device::getNumActivacoes).reversed())
            .limit(3)
            .collect(Collectors.toList());
    }

    public List<Device> getTop3DispositivosPorTempo(String idCasa)
            throws HouseNotFoundException {

        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);

        List<Device> todos = new ArrayList<>();
        for (Room r : h.getDivisoes())
            todos.addAll(r.getDispositivos());

        return todos.stream()
            .sorted(Comparator.comparingLong(Device::getTotalMinutosLigado).reversed())
            .limit(3)
            .collect(Collectors.toList());
    }

    public List<String> getTop3DivisoesPorDispositivos() {
        List<String[]> pares = new ArrayList<>();

        for (House h : casas.values()) {
            for (Room r : h.getDivisoes()) {
                pares.add(new String[]{
                    h.getNome() + " > " + r.getNome(),
                    String.valueOf(r.getNumDispositivos())
                });
            }
        }

        return pares.stream()
            .sorted((a, b) -> Integer.compare(
                Integer.parseInt(b[1]), Integer.parseInt(a[1])))
            .limit(3)
            .map(p -> p[0] + " | Dispositivos: " + p[1])
            .collect(Collectors.toList());
    }

    public double getConsumoTotalDaCasa(String idCasa) throws HouseNotFoundException {
        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);
        return h.getConsumoTotal();
    }


    //==========================================================
    // CENÁRIOS

    public void adicionarCenarioAoUtilizador(Scenario c) throws UserNotFoundException {
        User atual = utilizadores.get(idUtilizadorAtual);
        if (atual == null)
            throw new UserNotFoundException("Nenhum utilizador autenticado.");
        atual.adicionarCenario(c);
    }

    // percorre todas as casas à procura dos dispositivos do cenário
    public void executarCenario(String idCenario) throws UserNotFoundException {
        User atual = utilizadores.get(idUtilizadorAtual);
        if (atual == null)
            throw new UserNotFoundException("Nenhum utilizador autenticado.");

        Scenario cenario = atual.getCenarios().stream()
            .filter(c -> c.getId().equals(idCenario))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cenário não encontrado: " + idCenario));

        for (String[] acao : cenario.getAcoes()) {
            String idDisp = acao[0];
            String tipoAcao = acao[1];
            for (House h : casas.values()) {
                for (Room r : h.getDivisoes()) {
                    if (r.existeDispositivo(idDisp)) {
                        try {
                            if (tipoAcao.equals("ligar"))
                                ligarDispositivo(h.getId(), r.getId(), idDisp);
                            else
                                desligarDispositivo(h.getId(), r.getId(), idDisp);
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
    }
    
    //==========================================================
    // AUTOMAÇÕES

    // adiciona uma automação a uma casa
    public void adicionarAutomacao(String idCasa, Automation a)
            throws HouseNotFoundException {
        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);
        h.adicionarAutomacao(a);
    }

    // verifica todas as automações ativas e executa as que a condição for verdade
    // deve ser chamado sempre que um sensor atualiza o seu valor
    public void verificarAutomacoes(String idCasa) throws HouseNotFoundException {
        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);

        for (Automation auto : h.getAutomacoes()) {
            if (!auto.isAtiva()) continue;

            // procura o sensor em todas as divisões da casa
            for (Room r : h.getDivisoes()) {
                Device sensor = r.getDispositivo(auto.getIdSensor());
                if (sensor instanceof model.devices.Sensor) {
                    double valorAtual = ((model.devices.Sensor) sensor).getValorAtual();
                    if (auto.avaliarCondicao(valorAtual)) {
                        // condicao verificada, executa a acao em todos os dispositivos
                        for (String idDisp : auto.getIdsDispositivos()) {
                            for (Room div : h.getDivisoes()) {
                                if (div.existeDispositivo(idDisp)) {
                                    try {
                                        if (auto.getAcao().equals("ligar"))
                                            ligarDispositivo(h.getId(), div.getId(), idDisp);
                                        else
                                            desligarDispositivo(h.getId(), div.getId(), idDisp);
                                    } catch (Exception ignored) {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // atualiza o valor de um sensor e verifica automações automaticamente
    public void atualizarSensor(String idCasa, String idDivisao, String idSensor, double novoValor)
            throws HouseNotFoundException, RoomNotFoundException, DeviceNotFoundException {

        Device d = getDispositivoInterno(idCasa, idDivisao, idSensor);
        if (!(d instanceof model.devices.Sensor))
            throw new IllegalArgumentException("O dispositivo indicado não é um sensor.");

        ((model.devices.Sensor) d).setValorAtual(novoValor);
        verificarAutomacoes(idCasa);
    }

    //==========================================================
    // ESCALONAMENTOS

    public void adicionarEscalonamento(String idCasa, Scheduling s)
            throws HouseNotFoundException {
        House h = casas.get(idCasa);
        if (h == null) throw new HouseNotFoundException(idCasa);
        h.adicionarEscalonamento(s);
    }

    // chamar este método sempre que o tempo avançar
    public void verificarEscalonamentos() {
        LocalTime agora = relogio.getDataHoraAtual().toLocalTime();
        for (House h : casas.values()) {
            for (Scheduling s : h.getEscalonamentos()) {
                if (s.deveExecutar(agora)) {
                    for (String idDisp : s.getIdsDispositivos()) {
                        for (Room r : h.getDivisoes()) {
                            if (r.existeDispositivo(idDisp)) {
                                try {
                                    if (s.isLigar())
                                        ligarDispositivo(h.getId(), r.getId(), idDisp);
                                    else
                                        desligarDispositivo(h.getId(), r.getId(), idDisp);
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            }
        }
    }


    //==========================================================
    // SIMULAÇÃO DO TEMPO

    public void avancarTempo(long minutos) {
        relogio.avancarMinutos(minutos);
        verificarEscalonamentos();
    }

    public SimulatedClock getRelogio() {
        return relogio;
    }


    //==========================================================
    // GRAVAR E CARREGAR ESTADO

    public void gravarEstado(String caminho) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(caminho))) {
            oos.writeObject(this);
        }
    }

    public static DomusController carregarEstado(String caminho)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(caminho))) {
            return (DomusController) ois.readObject();
        }
    }


    //==========================================================
    // PERMISSÕES

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