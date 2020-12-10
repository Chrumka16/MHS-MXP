package common;

import application.Application;
import application.ExitCode;
import reasoner.ReasonerType;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class ArgumentParser {

    public void parse(String[] args) {

        if (args.length != 1){
            System.err.println("Wrong number of argument for main function: Run program with one configuration input file as argument");
            Application.finish(ExitCode.ERROR);
        }
        ArrayList<String[]> lines = read_input_file(args[0]);

        boolean read_abd = false;
        boolean read_prefixes= false;

        for (String[] line: lines){
            String new_line = line[0].trim();
            if (read_abd || read_prefixes){
                if (new_line.equals("}")){
                    read_prefixes = false;
                    read_abd = false;
                } else if (read_abd){
                    add_abd(new_line);
                } else{
                    String last = (line.length == 2) ? line[1] : "";
                    add_prefix(new_line + " " + last);
                }
                continue;
            }
            String next = line[1];
            switch(new_line) {
                case "-f:":
                    if (!(new File(next).exists())){
                        System.err.println("Could not open -f file " + next);
                        Application.finish(ExitCode.ERROR);
                    }
                    Configuration.INPUT_FILE = next;
                    break;
                case "-o:":
//                    if (!next.matches("[a-zA-Z]+:[a-zA-Z]+")){
                    if (!next.matches("(([a-zA-Z]+:)|(" + DLSyntax.IRI_REGEX + "#))[A-Z]{1}[a-z]*\\(" + "(([a-zA-Z]+:)|(" + DLSyntax.IRI_REGEX + "#))[a-z]+" + "\\)")){
                        System.err.println("Observation " + next + " does not match the form A(a)");
                        Application.finish(ExitCode.ERROR);
                    }
                    Configuration.OBSERVATION = next;
                    break;
                case "-r:":
                    try {
                        Configuration.REASONER = ReasonerType.valueOf(next.toUpperCase());
                    }
                    catch (IllegalArgumentException e){
                        System.err.println("Reasoner type -r " + next + " is unknown, the only allowed reasoners are hermit|pellet|jfact");
                        Application.finish(ExitCode.ERROR);
                    }
                    break;
                case "-d:":
                    try {
                        Configuration.DEPTH = Integer.valueOf(next);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Wrong tree depth -d " + next + ", choose a whole number value");
                        Application.finish(ExitCode.ERROR);
                    }
                    break;
                case "-t:":
                    try {
                        Configuration.TIMEOUT = Long.valueOf(next);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Wrong timeout value -t " + next + ", choose a whole number value");
                        Application.finish(ExitCode.ERROR);
                    }
                    break;
                case "-a:":
                    if (next.equals("{")){
                        read_abd = true;
                    } else {
                        add_abd(next);
                    }
                    break;
                case "-p:":
                    if (next.equals("{")) {
                        read_prefixes = true;
                    } else {
                        String last = (line.length == 3) ? line[2] : "";
                        add_prefix(next + " " + last);
                    }
                    break;
                default:
                    System.err.println("Unknown option " + line[0] + " in input file");
                    Application.finish(ExitCode.ERROR);
            }
        }
        if (Configuration.INPUT_FILE.equals("") || Configuration.OBSERVATION.equals("")){
            System.err.println("Input file -f and observation -o are both required argument");
            Application.finish(ExitCode.ERROR);
        }
        if (Configuration.REASONER == null) {
            Configuration.REASONER = ReasonerType.HERMIT;
        }
        if (Configuration.DEPTH == null){
            Configuration.DEPTH = Integer.MAX_VALUE;
        }
    }

    private void add_prefix(String prefix){
        if (!prefix.matches("[a-zA-Z]+: " + DLSyntax.IRI_REGEX)){
            System.err.println("Prefix '" + prefix + "' does not match the form 'prefix_shortcut: prefix'");
            Application.finish(ExitCode.ERROR);
        }
        Configuration.PREFIXES.add(prefix);
    }

    private void add_abd(String abd){
        if (!abd.matches("[a-zA-Z]+:([a-zA-Z]+|[a-zA-Z]+\\(\\))|"+ DLSyntax.IRI_REGEX +"#([a-zA-Z]+|[a-zA-Z]+\\(\\))")){
            System.err.println("Abductible " + abd + " does not match the form 'prefix_shortcut:individual/concept/property' nor 'prefix#individual/concept/property'");
            Application.finish(ExitCode.ERROR);
        }
        Configuration.ABDUCIBLES.add(abd);
    }

    private ArrayList<String[]> read_input_file(String input_file_path) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(input_file_path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String strLine;
        ArrayList<String[]> lines = new ArrayList<>();
        try {
            while ((strLine = reader.readLine()) != null) {
                if (strLine.equals("")){
                    continue;
                }
                String[] words = strLine.split("\\s+");
                lines.add(words);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

}
