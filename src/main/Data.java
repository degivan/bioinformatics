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
    private static String candidateSequence = null;
    private static String reversedCandidateSeq = null;
    private static List<Double> candidateHCDCutList = new ArrayList<>();
    private static List<Double> reversedHCDCutList = new ArrayList<>();
    private static List<Double> candidateETDCutList = new ArrayList<>();
    private static List<Double> reversedETDCutList = new ArrayList<>();
    private static List<Spectre> spectreHCDList = null;
    private static List<Spectre> spectreETDList = null;

    private Data() {}

    public static Spectre addSpectreFromFile(Path pathToFile) throws IOException {
        return addSpectre(Spectre.getSpectreFromFile(pathToFile));
    }

    public static Spectre addSpectre(Spectre spectre) {
        switch(spectre.getTechnology()) {
            case HCD:
                if (spectreHCDList == null ) {
                    spectreHCDList = new ArrayList<>();
                }
                spectreHCDList.add(spectre);
                break;
            case ETD:
                if (spectreETDList == null ) {
                    spectreETDList = new ArrayList<>();
                }
                spectreETDList.add(spectre);
                break;
        }
        return spectre;
    }

    public static List<Spectre> getHCDSpectres() {
        return spectreHCDList;
    }

    public static List<Spectre> getETDSpectres() {
        return spectreETDList;
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
        spectreHCDList = new ArrayList<>();
        spectreETDList = new ArrayList<>();
    }

    public static List<Double> getCandidateHCDCutList() {
        return getCutList(candidateHCDCutList, candidateSequence, 0.0);
    }

    public static List<Double> getReversedHCDCutList() {
        return getCutList(reversedHCDCutList, reversedCandidateSeq, 18.0);
    }

    public static List<Double> getCandidateETDCutList() {
        return getCutList(candidateETDCutList, candidateSequence, 17.0);
    }

    public static List<Double> getReversedETDCutList() {
        return getCutList(reversedETDCutList, reversedCandidateSeq, -17.0);
    }

    private static List<Double> getCutList(List<Double> cutList, String candidateSeq, double additionalMass) {
        if(cutList.size() == 0) {
            cutList.addAll(createCutList(candidateSeq, additionalMass));
        }
        return cutList;
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
