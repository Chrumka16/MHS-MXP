package common;

import reasoner.ReasonerType;

import java.util.ArrayList;

public class Configuration {
    public static String OBSERVATION = "";
    public static String INPUT_FILE = "";
    public static ReasonerType REASONER;
    public static Integer DEPTH;
    public static Long TIMEOUT;
    public static ArrayList<String> ABDUCIBLES = new ArrayList<>();
    public static ArrayList<String> PREFIXES = new ArrayList<>();
}
