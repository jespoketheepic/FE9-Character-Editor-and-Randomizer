package contents.math;

import contents.datastructures.CharacterEntry;
import contents.datastructures.DisposEntry;
import contents.datastructures.Skill;
import contents.datastructures.feClass.BaseFEClass;
import contents.datastructures.feClass.PromotedFEClass;
import contents.datastructures.interfaces.FEClass;
import contents.datastructures.inventory.Weapon;
import contents.gui.RootControler;
import contents.io.SVar;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Randomize {

    public static void randomizeClass(CharacterEntry entry, DisposEntry disposEntry, Map<Integer, FEClass> feClassMap, Map<String, Weapon> weaponMap){
        FEClass feClass = feClassMap.get(ByteMath.bytesToInt(entry.getClass_pointer()));
        int effectiveLevel;

        do {
            // Promoted or not?
            if (feClass.isLaguz()) {
                // Laguz case
                effectiveLevel = entry.getLevel() * 2;
                if (entry.getLevel() <= 10) {
                    // Base case
                    feClass = randomClass(feClassMap, false);
                } else {
                    // Promoted case
                    feClass = randomClass(feClassMap, true);
                }
            } else {
                // Beorc case
                if (feClass.isPromoted()) {
                    effectiveLevel = entry.getLevel() + 20;
                } else {
                    effectiveLevel = entry.getLevel();
                }
                feClass = randomClass(feClassMap, feClass.isPromoted());
            }

            // Reroll on unacceptable results
        } while (!feClassAccept(feClass, entry));

        // Class decided, now apply it
        entry.setClass_pointer(feClass.getJID_pointer());
        disposEntry.setJID_name(feClass.getJID_name());
        entry.setAnimation1_pointer(feClass.getAID1_pointer());
        entry.setAnimation2_pointer(feClass.getAID2_pointer());

        // Scale levels
        if(feClass.isLaguz()){
            entry.setLevel((byte)((effectiveLevel+1)/2));
        } else if (feClass.isPromoted()){
            entry.setLevel((byte)(effectiveLevel - 20));
        } else {
            entry.setLevel((byte)effectiveLevel);
        }

        // Weapon. Goes for the last weapon in their inventory, none if there is none.
        if (!disposEntry.getWeapon4_IID_name().equals("IID_NULL")){
            disposEntry.setWeapon4_IID_name(feClass.getIID_name());
        } else if (!disposEntry.getWeapon3_IID_name().equals("IID_NULL")){
            disposEntry.setWeapon3_IID_name(feClass.getIID_name());
        } else if (!disposEntry.getWeapon2_IID_name().equals("IID_NULL")){
            disposEntry.setWeapon2_IID_name(feClass.getIID_name());
        } else if (!disposEntry.getWeapon1_IID_name().equals("IID_NULL")) {
            disposEntry.setWeapon1_IID_name(feClass.getIID_name());
        }

        // Weapon ranks. Copies the highest one over onto the current class's weapon type.
        // Doesn't account for S-rank because who has base S-rank? (Stefan, thats who, but the S is his only one so its fiiine)
        // The options in weaponrank are -, E, D, C, B, A, S, and *. The first and last are equivalent to E in this case.
        byte buffer = 'E';
        for (byte weaponrank : entry.getWeaponRanks()){
            if(weaponrank != '-' && weaponrank != '*' && weaponrank != 0x00 && weaponrank < buffer){
                buffer = weaponrank;
            }
        }

        // Pity points for laguz and theives
        if(buffer == 'E'){
            if(effectiveLevel >= 34){
                buffer = 'S';
            } else if (effectiveLevel >= 28){
                buffer = 'A';
            } else if (effectiveLevel >= 20){
                buffer = 'B';
            } else if (effectiveLevel >= 14){
                buffer = 'C';
            } else if (effectiveLevel >= 6){
                buffer = 'D';
            }
        }

        // Use new class' IID_name -> to find the weapon -> whose type -> shows what weaponrank to give
        switch (weaponMap.get(feClass.getIID_name()).getType()){
            case "sword":
                entry.getWeaponRanks()[0] = buffer;
                break;
            case "lance":
                entry.getWeaponRanks()[1] = buffer;
                break;
            case "axe":
                entry.getWeaponRanks()[2] = buffer;
                break;
            case "bow":
                entry.getWeaponRanks()[3] = buffer;
                break;
            case "fire":
                entry.getWeaponRanks()[4] = buffer;
                break;
            case "thunder":
                entry.getWeaponRanks()[5] = buffer;
                break;
            case "wind":
                entry.getWeaponRanks()[6] = buffer;
                break;
            case "staff":
            case "light":
                entry.getWeaponRanks()[7] = buffer;
                break;
        }
    }

    @SuppressWarnings("Duplicates")
    // Randomize bases
    public static void randomizeBases(CharacterEntry entry, Map<String, Weapon> weaponsMap, Map<Integer, FEClass> feClassMap){
        byte[] bases = entry.getBases();
        double[] weights = new double[8];
        int total = 0;
        int spenttotal = 0;
        double weightTotal = 0;


        // Tally up the bases and randomize weights
        for(int i = 0; i < 8; i++){
            total += bases[i];
            weights[i] = Math.random();
            weightTotal += weights[i];
        }
        // Put aside points for luck
        total -= 3;

        // Spread the goods
        for (int i = 0; i < 8; i++){
            bases[i] = (byte)Math.floor(weights[i]*total/weightTotal);
            spenttotal += bases[i];
        }
        // Re-add the luck
        bases[5] += 3;

        // Remainders are spent randomly
        while (total - spenttotal > 0){
            bases[(int)(Math.random() * 8)]++;
            spenttotal++;
        }


        // Swap Str and Mag if they don't match the weapon
        // It is real simple, my data structure is just not set up for it
        switch (weaponsMap.get(feClassMap.get(ByteMath.bytesToInt(entry.getClass_pointer())).getIID_name()).getType()){
            case "sword":
            case "lance":
            case "axe":
            case "bow":
            case "laguzweapon":
                // If Mag is higher, swap
                if(bases[2]>bases[1]){
                    byte b = bases[2];
                    bases[2] = bases[1];
                    bases[1] = b;
                }
                break;
            case "fire":
            case "thunder":
            case "wind":
            case "staff":
            case "light":
                // If Str is higher, swap
                if(bases[1]>bases[2]){
                    byte b = bases[2];
                    bases[2] = bases[1];
                    bases[1] = b;
                }
                break;
        }

        // I think this is bad Java'ing, but it is more readable
        entry.setBases(bases);
    }

    @SuppressWarnings("Duplicates")
    // Randomize growths
    public static void randomizeGrowths(CharacterEntry entry, Map<String, Weapon> weaponsMap, Map<Integer, FEClass> feClassMap){
        byte[] growths = entry.getGrowths();
        double[] weights = new double[8];
        int total = 0;
        double weightTotal = 0;
        int spent = 0;

        // Tally up the growths and randomize weights
        for(int i = 0; i < 8; i++){
            total += growths[i];
            weights[i] = Math.random();
            weightTotal += weights[i];
        }
        // Put aside points for luck
        total -= 4;

        // Spread the goods
        for (int i = 0; i < 8; i++){
            growths[i] = (byte)(Math.floor((weights[i]*total)/(weightTotal*5))*5);
            spent += growths[i];
        }

        // Remainder dumped in HP
        growths[0] += total-spent;

        // Swap Str and Mag if they don't match the weapon
        // It is real simple, my data structure is just not set up for it
        switch (weaponsMap.get(feClassMap.get(ByteMath.bytesToInt(entry.getClass_pointer())).getIID_name()).getType()){
            case "sword":
            case "lance":
            case "axe":
            case "bow":
            case "laguzweapon":
                // If Mag is higher, swap
                if(ByteMath.unsignedByteGreaterThan(growths[2], growths[1])){
                    byte b = growths[2];
                    growths[2] = growths[1];
                    growths[1] = b;
                }
                break;
            case "fire":
            case "thunder":
            case "wind":
            case "staff":
            case "light":
                // If Str is higher, swap
                if(ByteMath.unsignedByteGreaterThan(growths[1], growths[2])){
                    byte b = growths[2];
                    growths[2] = growths[1];
                    growths[1] = b;
                }
                break;
        }

        // I think this is bad Java'ing, but it is more readable
        entry.setGrowths(growths);
    }

    // Randomize the skills that we can do that to
    public static void randomizeSkills(CharacterEntry entry){
        int skill = ByteMath.bytesToInt(entry.getSkill1_pointer());
        if (skill != 0 && !isImmutableSkill(skill) && isClassConnectedSkill(skill)){
            entry.setSkill1_pointer(randomSkill().getSID_pointer());
        }

        skill = ByteMath.bytesToInt(entry.getSkill2_pointer());
        if (skill != 0 && !isImmutableSkill(skill) && isClassConnectedSkill(skill)){
            entry.setSkill1_pointer(randomSkill().getSID_pointer());
        }

        skill = ByteMath.bytesToInt(entry.getSkill3_pointer());
        if (skill != 0 && !isImmutableSkill(skill) && isClassConnectedSkill(skill)){
            entry.setSkill3_pointer(randomSkill().getSID_pointer());
        }
    }

    /////////////
    // Support //
    /////////////

    // This is hella inefficient use of streams i think, but this whole project is practice anyway
    private static FEClass randomClass(Map<Integer, FEClass> feClassMap, boolean promoted){
        // New class, random from a list filtered
        List<FEClass> feClasses = feClassMap.values().stream().filter(Predicate.not(fec -> {
            if(promoted){
                return fec instanceof BaseFEClass;
            } else {
                return fec instanceof PromotedFEClass;
            }
        }))
            // filter the classes that shouldnt be in rand
            .filter(fec -> {
                if(fec.getFrequency() == 3){
                    return false;
                } else{
                    return true;
                }})
            .collect(Collectors.toList());
        return feClasses.get((int)(Math.random()*feClasses.size()));
    }

    // Just stuff a whole bunch of conditions in here
    // Each special case handling can be put here too
    private static boolean feClassAccept(FEClass feClass, CharacterEntry entry){

        // Herons: They need chant, and for that they need an open skill slot
        if((feClass.getJID_name().equals("JID_BIRD_HE_W") || feClass.getJID_name().equals("JID_BIRD_HE_W/F"))){
            if (!overwriteSkillIfPossible(entry, 0x0001BB0E)){
                return false;
            }
        } else {
            // Non-herons random roll away Chant
            if (ByteMath.bytesToInt(entry.getSkill1_pointer()) == 0x0001BB0E){
                entry.setSkill1_pointer(randomSkill().getSID_pointer());
            } else if (ByteMath.bytesToInt(entry.getSkill2_pointer()) == 0x0001BB0E){
                entry.setSkill2_pointer(randomSkill().getSID_pointer());
            } else if (ByteMath.bytesToInt(entry.getSkill3_pointer()) == 0x0001BB0E){
                entry.setSkill3_pointer(randomSkill().getSID_pointer());
            }
        }

        // Thief: They need lockpick, and for that they need an open skill slot
        if((feClass.getJID_name().equals("JID_THIEF"))){
            if (!overwriteSkillIfPossible(entry, 0x0001BD6F)){
                return false;
            }
        } else {
            // Non-theives random roll away Lockpick
            if (ByteMath.bytesToInt(entry.getSkill1_pointer()) == 0x0001BD6F){
                entry.setSkill1_pointer(randomSkill().getSID_pointer());
            } else if (ByteMath.bytesToInt(entry.getSkill2_pointer()) == 0x0001BD6F){
                entry.setSkill2_pointer(randomSkill().getSID_pointer());
            } else if (ByteMath.bytesToInt(entry.getSkill3_pointer()) == 0x0001BD6F){
                entry.setSkill3_pointer(randomSkill().getSID_pointer());
            }
        }

        // Shinon: His recruitment loses his weapon, which breaks laguz
        if(entry.getPID_name().equals("PID_CHINON") && feClass.isLaguz()){
            return false;
        }

        return true;
    }

    // Returns whether the skill could be applied
    private static boolean overwriteSkillIfPossible(CharacterEntry entry, int skillTxtPointer){
        if (SVar.region == 0x10){
            // Apply the NA skill table offset difference
            skillTxtPointer += -0x78;
        }

        int buffer;
        if ((buffer = ByteMath.bytesToInt(entry.getSkill1_pointer())) != 0 && !isImmutableSkill(buffer)){
            entry.setSkill1_pointer(ByteMath.intToBytes(skillTxtPointer));
        } else if ((buffer = ByteMath.bytesToInt(entry.getSkill2_pointer())) != 0 && !isImmutableSkill(buffer)){
            entry.setSkill2_pointer(ByteMath.intToBytes(skillTxtPointer));
        } else if ((buffer = ByteMath.bytesToInt(entry.getSkill3_pointer())) != 0 && !isImmutableSkill(buffer)){
            entry.setSkill3_pointer(ByteMath.intToBytes(skillTxtPointer));
        } else {
            return false;
        }
        return true;
    }

    // Return whether the skill is on the "DONT REMOVE" list
    private static boolean isImmutableSkill(int skillPointer){
        switch (skillPointer){
            case 0x0001BD28: // Protagonist
            case 0x0001BE90: // Temp_on_die
                return true;
            default:
                return false;
        }
    }

    private static boolean isClassConnectedSkill(int skillPointer){
        switch (skillPointer){
            case 0x0001BB0E: // Chant
            case 0x0001BD6F: // Lockpick
                return true;
            default:
                return false;
        }
    }

    private static List<Skill> availableSkills;

    public static void updateAvailableSkills(Collection<Skill> skills){
        availableSkills = skills.stream().filter(s -> (s.getCategory() < 3)).collect(Collectors.toList());
    }

    private static Skill randomSkill(){
        if (availableSkills == null){
            throw new IllegalStateException("Availableskills was never set");
        }

        int rn = (int)(Math.random() * availableSkills.size());

        return availableSkills.get(rn);
    }
}
