package utils;

// cores ANSI para o terminal — só funciona em terminais linux/mac
public class UIColors {

    public static final String RESET   = "\033[0m";
    public static final String BOLD    = "\033[1m";
    public static final String DIM     = "\033[2m";

    public static final String CYAN    = "\033[36m";
    public static final String BCYAN   = "\033[96m";
    public static final String GREEN   = "\033[32m";
    public static final String BGREEN  = "\033[92m";
    public static final String RED     = "\033[31m";
    public static final String BRED    = "\033[91m";
    public static final String YELLOW  = "\033[33m";
    public static final String BYELLOW = "\033[93m";
    public static final String WHITE   = "\033[97m";

    private UIColors() {}
}