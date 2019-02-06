package contents.datastructures.feClass;

import contents.datastructures.interfaces.FEBaseClass;
import contents.math.ByteMath;

public class BaseFEClass implements FEBaseClass {

    private String displayName;
    private String JID_name;
    private byte[] JID_pointer;
    private String AID1_name;
    private byte[] AID1_pointer;
    private String AID2_name;
    private byte[] AID2_pointer;
    private String IID_name;
    private byte[] IID_pointer;
    private int frequency;

    //////////////////
    // Constructors //
    //////////////////

    public BaseFEClass(String displayName, String JID_name, byte[] JID_pointer, String AID1_name, byte[] AID1_pointer, String AID2_name, byte[] AID2_pointer, String IID_name, byte[] IID_pointer, int frequency) {
        this.displayName = displayName;
        this.JID_name = JID_name;
        this.JID_pointer = JID_pointer;
        this.AID1_name = AID1_name;
        this.AID1_pointer = AID1_pointer;
        this.AID2_name = AID2_name;
        this.AID2_pointer = AID2_pointer;
        this.IID_name = IID_name;
        this.IID_pointer = IID_pointer;
        this.frequency = frequency;
    }

    public BaseFEClass(String[] parameters){
        if(parameters.length != 11){
            throw new IllegalArgumentException();
        }
        this.displayName = parameters[0];
        this.JID_name = parameters[1];
        this.JID_pointer = ByteMath.hexStringToByteArray(parameters[2]);
        this.AID1_name = parameters[3];
        this.AID1_pointer = ByteMath.hexStringToByteArray(parameters[4]);
        this.AID2_name = parameters[5];
        this.AID2_pointer = ByteMath.hexStringToByteArray(parameters[6]);
        this.IID_name = parameters[7];
        this.IID_pointer = ByteMath.hexStringToByteArray(parameters[8]);
        // parameters[9] determines what implementation of FEClass gets called
        this.frequency = Integer.parseInt(parameters[10]);
    }

    /////////////
    // Getters //
    /////////////

    public String getDisplayName() {
        return displayName;
    }

    public byte[] getJID_pointer() {
        return JID_pointer;
    }

    public String getJID_name() {
        return JID_name;
    }

    public String getAID1_name() {
        return AID1_name;
    }

    public String getAID2_name() {
        return AID2_name;
    }

    public String getIID_name() {
        return IID_name;
    }

    public boolean isLaguz() {
        return false;
    }

    public boolean isPromoted() { return false; }

    public int getFrequency(){ return frequency; }

    public Integer getKey(){
        return ByteMath.bytesToInt(JID_pointer);
    }

    @Override
    public String getID_name() {
        return JID_name;
    }

    @Override
    public byte[] getID_pointer() {
        return JID_pointer;
    }

    public byte[] getAID1_pointer() {
        return AID1_pointer;
    }

    public byte[] getAID2_pointer() {
        return AID2_pointer;
    }

    public byte[] getIID_pointer() {
        return IID_pointer;
    }
}
