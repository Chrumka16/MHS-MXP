package fileLogger;

import common.Configuration;
import common.DLSyntax;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileLogger {

    public static final String HYBRID_LOG_FILE__PREFIX = "hybrid";
    public static final String HYBRID_PARTIAL_EXPLANATIONS_LOG_FILE__PREFIX = "hybridPartialExplanations";
    public static final String LOG_FILE__POSTFIX = ".log";
    private static final String FILE_DIRECTORY = "logs";

    public static void appendToFile(String fileName, long currentTimeMillis, String log, boolean isOutput) {
        createFileIfNotExists(fileName, currentTimeMillis, isOutput);
        try {
            String file_path = getFilePath(fileName, currentTimeMillis, isOutput);
            System.out.println(Paths.get(file_path));
            Files.write(Paths.get(file_path), log.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void createFileIfNotExists(String fileName, long currentTimeMillis, boolean isOutput) {
        File file = new File(getFilePath(fileName, currentTimeMillis, isOutput));
        try {
            file.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static String getFilePath(String fileName, long currentTimeMillis, boolean isOutput) {
        String[] inputFile;
        try {
            inputFile = Configuration.INPUT_FILE.split(File.separator);
        }
        catch(Exception e) {
            inputFile = Configuration.INPUT_FILE.split("\\\\");
        }
        String input = inputFile[inputFile.length - 1];
        String inputFileName = input;
        String[] inputFileParts = input.split("\\.");
        if (inputFileParts.length > 0) {
            inputFileName = inputFileParts[0];
        }
        String directoryPath;
        if (isOutput){
            directoryPath = "outputs".concat(File.separator).concat(Configuration.REASONER.name()).concat(File.separator).concat(inputFileName);
        }
        else {
           directoryPath = FILE_DIRECTORY.concat(File.separator).concat(Configuration.REASONER.name()).concat(File.separator).concat(inputFileName);
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

//        String observation = Configuration.OBSERVATION.replaceAll("\\s+", "_").replaceAll(":", "-");
        String observation = observationToFilePath();
        if (isOutput){
            return directoryPath.concat(File.separator).concat("" + currentTimeMillis + "__").concat(observation + "__").concat(fileName).concat(".txt");
        }
        return directoryPath.concat(File.separator).concat("" + currentTimeMillis + "__").concat(observation + "__").concat(fileName).concat(LOG_FILE__POSTFIX);
    }

    private static String observationToFilePath(){
        String[] observation = Configuration.OBSERVATION.substring(0, Configuration.OBSERVATION.length()-1).split("\\"+ DLSyntax.LEFT_PARENTHESES);
        for (int i = 0; i < observation.length; i++){
            if (observation[i].contains(DLSyntax.DELIMITER_ONTOLOGY)){
                observation[i] = observation[i].substring(observation[i].indexOf(DLSyntax.DELIMITER_ONTOLOGY)+1);
            }
            else {
                observation[i] = observation[i].substring(observation[i].indexOf(DLSyntax.DELIMITER_ASSERTION)+1);
            }
        }
        return String.join("_", observation);
    }
}

