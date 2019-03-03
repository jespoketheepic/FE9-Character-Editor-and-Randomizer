package contents.datastructures;

import contents.datastructures.interfaces.SystemCmpContents;
import contents.io.SVar;
import contents.math.ByteMath;

// TODO: Test CRITRISE, CHARISMA, 50Keys, EQ_B, Key0, Fixedbeast. Lumina on someone without staves but with staff rank. REMEMBER THAT CRITRISE IS NOT IN JP!!
public class Skill implements SystemCmpContents{

    private String skillName;
    private int category; // 1 for normal, 2 for mastery, 3 for things that might work, 4 for things to keep your hands off
    private String SID_name;
    private byte[] SID_pointer; // The system.cmp SID_pointer of the SID_name

    /////////////////
    // Constructor //
    /////////////////

    public  Skill(String skillName, int category, String SID_name, byte[] SID_pointer) {
        this.skillName = skillName;
        this.category = category;
        this.SID_name = SID_name;
        this.SID_pointer = SID_pointer;
    }

    public Skill(String[] parameters){
        if(parameters.length != 4){
            throw new IllegalArgumentException();
        }

        this.skillName = parameters[0];
        this.category = Integer.parseInt(parameters[1]);
        this.SID_name = parameters[2];
        // the Skill table was moved a little between versions
        if (SVar.region == 0x10){
            // NA case
            this.SID_pointer = ByteMath.byteArrayAddInt(ByteMath.hexStringToByteArray(parameters[3]), -0x78);
        } else {
            // PAL case
            this.SID_pointer = ByteMath.hexStringToByteArray(parameters[3]);
        }
    }


    /////////////
    // Getters //
    /////////////

    public String getDisplayName(){
        return skillName;
    }

    public String getSkillName() {
        return skillName;
    }

    public int getCategory() {
        return category;
    }

    public String getSID_name() {
        return SID_name;
    }

    public byte[] getSID_pointer() {
        return SID_pointer;
    }

    public Integer getKey(){
        return ByteMath.bytesToInt(SID_pointer);
    }

    @Override
    public String getID_name() {
        return SID_name;
    }

    @Override
    public byte[] getID_pointer() {
        return SID_pointer;
    }
}
