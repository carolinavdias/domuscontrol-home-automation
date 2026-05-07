package model.automations;

import java.io.Serializable;

// sugestão gerada pelo sistema — pode ser escalonamento ou automação
public class Suggestion implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Tipo { ESCALONAMENTO, AUTOMACAO }

    private Tipo tipo;
    private String descricao;
    private Scheduling escalonamento;
    private Automation automacao;

    private Suggestion() {}

    public static Suggestion deEscalonamento(String descricao, Scheduling s) {
        Suggestion sug  = new Suggestion();
        sug.tipo        = Tipo.ESCALONAMENTO;
        sug.descricao   = descricao;
        sug.escalonamento = s;
        return sug;
    }

    public static Suggestion deAutomacao(String descricao, Automation a) {
        Suggestion sug = new Suggestion();
        sug.tipo       = Tipo.AUTOMACAO;
        sug.descricao  = descricao;
        sug.automacao  = a;
        return sug;
    }

    public Tipo getTipo()                    { return tipo; }
    public String getDescricao()             { return descricao; }
    public Scheduling getEscalonamento()     { return escalonamento; }
    public Automation getAutomacao()         { return automacao; }
}