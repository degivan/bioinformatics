package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Degtjarenko Ivan on 14.04.2016.
 */
public class AminoAcidHolder {
    private Map<Character, Double> massMap;

    public AminoAcidHolder () {
        massMap = new HashMap<>();
        massMap.put('G', 57.021464);
        massMap.put('A', 71.037114);
        massMap.put('S', 87.032029);
        massMap.put('P', 97.052764);
        massMap.put('V', 99.068414);
        massMap.put('T', 101.04768);
        massMap.put('C', 103.00919);
        massMap.put('L', 113.08406);
        massMap.put('I', 113.08406);
        massMap.put('N', 114.04293);
        massMap.put('D', 115.02694);
        massMap.put('Q', 128.05858);
        massMap.put('K', 128.09496);
        massMap.put('E', 129.04259);
        massMap.put('M', 131.04048);
        massMap.put('H', 137.05891);
        massMap.put('F', 147.06841);
        massMap.put('R', 156.10111);
        massMap.put('Y', 163.06333);
        massMap.put('W', 186.07931);
    }

    public Set<Character> getAcidLetters() {
        return massMap.keySet();
    }
    public Double getMassWithAminoAcid(Double massBefore, Character aminoAcid) {
        return (massBefore + massMap.get(aminoAcid));
    }
}
