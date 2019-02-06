package contents.io;

import contents.datastructures.Animate;
import contents.datastructures.Portrait;
import contents.datastructures.Skill;
import contents.datastructures.feClass.BaseFEClass;
import contents.datastructures.feClass.LaguzFEClass;
import contents.datastructures.feClass.PromotedFEClass;
import contents.datastructures.interfaces.FEClass;
import contents.datastructures.interfaces.Inventory;
import contents.datastructures.inventory.Item;
import contents.datastructures.inventory.Weapon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExternalFileMapmaker {

    // Read entries from a comma separated resource file
    private static ArrayList<String[]> readCommaSeparatedFile(String resourcePath, int parameterCount) throws IOException {
        BufferedReader resourceTxt = new BufferedReader(new InputStreamReader(ExternalFileMapmaker.class.getResourceAsStream(resourcePath), StandardCharsets.UTF_8));
        String line;
        ArrayList<String[]> parameterList = new ArrayList<>();

        // Loop on as long as there are lines to read
        while((line = resourceTxt.readLine()) != null){
            String[] parameters = line.split(",");
            if(parameters.length != parameterCount){
                throw new RuntimeException("Resource txt contains a bad entry?? At line:" + parameterList.size());
            }

            parameterList.add(parameters);
        }

        resourceTxt.close();
        return parameterList;
    }

    ///////////////////
    // Public Access //
    ///////////////////

    // Read skill entries from Skills.txt
    public static LinkedHashMap<Integer, Skill> readSkillFile() throws IOException {
        LinkedHashMap<Integer, Skill> skillsMap = new LinkedHashMap<>();
        List<String[]> parameterEntries = readCommaSeparatedFile("/Skills.txt", 4);

        skillsMap.put(0, new Skill("(none)", 0, "(none)", new byte[]{0,0,0,0}));

        for (String[] entry : parameterEntries){
            Skill next = new Skill(entry);
            skillsMap.put(next.getKey(), next);
        }

        return skillsMap;
    }

    // Read portrait entries from Portraits.txt
    public static LinkedHashMap<Integer, Portrait> readPortraitFile() throws IOException {
        LinkedHashMap<Integer, Portrait> portraitsMap = new LinkedHashMap<>();
        List<String[]> parameterEntries = readCommaSeparatedFile("/Portraits.txt", 2);

        portraitsMap.put(0, new Portrait("(none)", new byte[]{0,0,0,0}));

        for (String[] entry : parameterEntries){
            Portrait next = new Portrait(entry);
            portraitsMap.put(next.getKey(), next);
        }

        return portraitsMap;
    }

    // Read animation entries from Animations.txt
    public static LinkedHashMap<Integer, Animate> readAnimationsFile() throws IOException {
        LinkedHashMap<Integer, Animate> animationsMap = new LinkedHashMap<>();
        List<String[]> parameterEntries = readCommaSeparatedFile("/Animations.txt", 2);

        animationsMap.put(0, new Animate("(none)", new byte[]{0,0,0,0}));

        for (String[] entry : parameterEntries){
            Animate next = new Animate(entry);
            animationsMap.put(next.getKey(), next);
        }

        return animationsMap;
    }

    // Read inventory entries from Items.txt
    // Returns list of Inventory
    public static List<Inventory> readInventoryFile() throws IOException {
        List<Inventory> inventoryList = new ArrayList<>();
        List<String[]> parameterEntries = readCommaSeparatedFile("/Items.txt", 4);

        // Null items for each type
        inventoryList.add(new Weapon("(none)","IID_NULL", new byte[]{0,0,0,0},"(none)"));
        inventoryList.add(new Item("(none)","IID_NULL", new byte[]{0,0,0,0},"(none)"));

        // Instantiate the entries according to the "type" parameter
        for (String[] entry : parameterEntries){
            switch (entry[3].toLowerCase()){
                case "sword":
                case "lance":
                case "axe":
                case "bow":
                case "fire":
                case "thunder":
                case "wind":
                case "staff":
                case "knife":
                case "light":
                case "laguzweapon":
                    Weapon nextW = new Weapon(entry);
                    inventoryList.add(nextW);
                    break;
                case "item":
                case "accessory":
                case "scroll":
                    Item nextI = new Item(entry);
                    inventoryList.add(nextI);
                    break;
                case "limitedscroll":
                case "broken":
                    break;
                default:
                    throw new RuntimeException("Items.txt contains wrong data: " + entry[3]);
            }
        }

        return inventoryList;
    }

    // Read Class entries from Class.txt
    public static LinkedHashMap<Integer, FEClass> readFEClassFile(Map<Integer, BaseFEClass> baseFEClassMap, Map<Integer, PromotedFEClass> promotedFEClassMap, Map<Integer, LaguzFEClass> laguzFEClassMap) throws IOException {
        LinkedHashMap<Integer, FEClass> feClassMap = new LinkedHashMap<>();
        List<String[]> parameterEntries = readCommaSeparatedFile("/Class.txt", 11);

        // Instantiate the entries according to the "type" parameter
        for (String[] entry : parameterEntries){
            switch (entry[9]){
                case "B":
                    BaseFEClass baseFEClass = new BaseFEClass(entry);
                    baseFEClassMap.put(baseFEClass.getKey(), baseFEClass);
                    feClassMap.put(baseFEClass.getKey(), baseFEClass);
                    break;
                case "P":
                    PromotedFEClass promotedFEClass = new PromotedFEClass(entry);
                    promotedFEClassMap.put(promotedFEClass.getKey(), promotedFEClass);
                    feClassMap.put(promotedFEClass.getKey(), promotedFEClass);
                    break;
                case "L":
                    LaguzFEClass laguzFEClass = new LaguzFEClass(entry);
                    laguzFEClassMap.put(laguzFEClass.getKey(), laguzFEClass);
                    feClassMap.put(laguzFEClass.getKey(), laguzFEClass);
                    break;
            }
        }

        return feClassMap;
    }
}
