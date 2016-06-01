package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by Degtjarenko Ivan on 01.04.2016.
 */
public class Spectre {
    public static final Charset ENCODING = StandardCharsets.UTF_8;
    public static int filesAmount = 0;

    private final static Set<String> technologyNames = new HashSet<>(Arrays.asList("HCD", "ETD"));
    private final int fileNumber;
    private final double precursorMZ;
    private final double precursorMass;
    private final int precursorCharge;
    private ArrayList<Peak> peakList;
    private final String fileName;
    private final SpectreTechnology technology;

    private Spectre(int fileNumber, double precursorMZ, double precursorMass, int precursorCharge, ArrayList<Peak> peakList, String fileName, SpectreTechnology technology) {
        this.fileNumber = fileNumber;
        this.precursorMZ = precursorMZ;
        this.precursorMass = precursorMass;
        this.precursorCharge = precursorCharge;
        this.peakList = peakList;
        this.fileName = fileName;
        this.technology = technology;
    }

    public static Spectre getSpectreFromFile(Path pathToFile) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(pathToFile, ENCODING)) {
            String line;
            for(int i = 0; i < 3; i++) {
                reader.readLine();
            }
            SpectreTechnology technology = getSpectreTechnology(reader);
            double precursorMZ = Double.parseDouble(getParam(reader));
            int precursorCharge = Integer.parseInt(getParam(reader));
            double precursorMass = Double.parseDouble(getParam(reader));
            ArrayList<Peak> peakList = new ArrayList<>();
            while((line = reader.readLine()) != null) {
                Peak peak = Peak.getPeakFromString(line);
                if (peak != null) {
                    peakList.add(peak);
                }
            }
            filesAmount++;
            String fileName = pathToFile.toFile().getName();
            return new Spectre(filesAmount - 1, precursorMZ, precursorMass, precursorCharge, peakList, fileName, technology);
        }
    }

    private static SpectreTechnology getSpectreTechnology(BufferedReader reader) throws IOException {
        reader.mark(50);
        String technology = getParam(reader);
        if (technologyNames.contains(technology)) {
            if(technology.equalsIgnoreCase("ETD")) {
                return SpectreTechnology.ETD;
            }
            if(technology.equalsIgnoreCase("HCD")) {
                return SpectreTechnology.HCD;
            }
        }
        reader.reset();
        return SpectreTechnology.HCD;
    }

    private static String getParam(BufferedReader reader) throws IOException {
        String[] strings = reader.readLine().split("=");
        return strings[1];
    }

    public static void setFilesAmount(int filesAmount) {
        Spectre.filesAmount = filesAmount;
    }

    public double getPrecursorMZ() {
        return precursorMZ;
    }

    public double getPrecursorMass() {
        return precursorMass;
    }

    public int getPrecursorCharge() {
        return precursorCharge;
    }

    public List<Peak> getPeakList() {
        return peakList;
    }

    public List<Set<Peak>> confirmSequence(List<Double> cutList) {
        List<Set<Peak>> confirmed = new ArrayList<>();
        for(int i = 0; i < cutList.size(); i++) {
            confirmed.add(new HashSet<>());
        }
        for(Peak p: peakList) {
            for(int i = 0; i < cutList.size(); i++) {
                if(Peak.isConfirmingCut(p.getMass(), cutList.get(i))) {
                    confirmed.get(i).add(p);
                }
            }
        }
        return confirmed;
    }

    public int getFileNumber() {
        return fileNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public SpectreTechnology getTechnology() {
        return technology;
    }

    protected enum SpectreTechnology {
        HCD(0),
        ETD(1);

        private final int id;
        private String name;

        SpectreTechnology(final int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public static SpectreTechnology get(final int id) {
            return values()[id];
        }
    }
}
