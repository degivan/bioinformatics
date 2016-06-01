package main;

/**
 * Created by Degtjarenko Ivan on 01.04.2016.
 */
public class Peak {
    private final static Double ALLOWABLE_ERROR = 0.00001;

    private final double mass;
    private final double intensity;
    private final int charge;

    public Peak(double mass, double intensity, int charge) {
        this.mass = mass;
        this.intensity = intensity;
        this.charge = charge;
    }

    public static Peak getPeakFromString(String string) {
        String[] strings = string.split("\\s+");
        if(strings[0].matches("\\d+\\.\\d+")) {
            double mass = Double.parseDouble(strings[0]);
            double intensity = Double.parseDouble(strings[1]);
            int charge = Integer.parseInt(strings[2]);
            return new Peak(mass, intensity, charge);
        } else {
            return null;
        }
    }

    public double getMass() {
        return mass;
    }

    public double getIntensity() {
        return intensity;
    }

    public int getCharge() {
        return charge;
    }

    public static boolean isConfirmingCut(Double mass, Double cut) {
        return Math.abs(mass - cut) < ALLOWABLE_ERROR * mass;
    }

    public String print() {
        return "mass: " + String.valueOf(mass) +
                ", intensity: " +
                intensity +
                ", charge: " +
                charge;
    }
}
