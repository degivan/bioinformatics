package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Data class keeps information about current loaded Spectres
 * and current candidate sequence.
 */
public class Data {
    private static List<Spectre> spectreList = null;
    private static String candidateSequence = null;
    private static String reversedCandidateSeq = null;
    private static List<Double> candidateCutList = new ArrayList<>();
    private static List<Double> reversedCutList = new ArrayList<>();

    private Data() {}

    public static void addSpectreFromFile(Path pathToFile) throws IOException {
        addSpectre(Spectre.getSpectreFromFile(pathToFile));
    }

    public static void addSpectre(Spectre spectre) {
        if (spectreList == null) {
            spectreList = new ArrayList<>();
        }
        spectreList.add(spectre);
    }

    public static List<Spectre> getSpectreList() {
        return spectreList;
    }

    public static void addCandidateSequence(String candidateSequence) {
        Data.candidateSequence = candidateSequence;
        reversedCandidateSeq = new StringBuffer(candidateSequence).reverse().toString();
    }

    public static String getCandidateSequence() {
        return candidateSequence;
    }

    public static void addCandidateSequenceFromFile(Path pathToFile) throws IOException {
        addCandidateSequence(getCandidateSequenceFromFile(pathToFile));
    }

    private static String getCandidateSequenceFromFile(Path pathToFile) throws IOException {
        return Files.newBufferedReader(pathToFile, Spectre.ENCODING).readLine();
    }

    public static void deleteAllSpectres() {
        Spectre.setFilesAmount(0);
        spectreList = new ArrayList<>();
    }

    public static List<Double> getCandidateCutList() {
        if(candidateCutList.size() == 0) {
            candidateCutList.addAll(createCutList(candidateSequence, 0.0));
        }
        return candidateCutList;
    }

    public static List<Double> getReversedCutList() {
        if(reversedCutList.size() == 0) {
            reversedCutList.addAll(createCutList(reversedCandidateSeq, 18.0));
        }
        return reversedCutList;
    }

    private static List<Double> createCutList(String candidateSeq, Double additionalMass) {
        Double mass = additionalMass;
        AminoAcidHolder acidHolder = new AminoAcidHolder();
        List<Double> cutList = new ArrayList<>();
        for(Character acid : candidateSeq.toCharArray()) {
            if(acidHolder.getAcidLetters().contains(acid)) {
                mass = acidHolder.getMassWithAminoAcid(mass, acid);
            }
            cutList.add(mass);
        }
        return cutList;
    }


}
