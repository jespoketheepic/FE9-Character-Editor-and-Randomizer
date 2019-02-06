package contents.datastructures;

import contents.datastructures.interfaces.Named;
import contents.exceptions.WrongSizeException;

public class CharacterEntry implements Named {
    private String entryName;
    private String PID_name;

    private byte[] PID_pointer;
    private byte[] MPID_pointer;
    private byte[] Null_pointer;
    private byte[] Portrait_pointer;
    private byte[] Class_pointer;
    private byte[] Affiliation_pointer;
    private byte[] Weaponlevel_pointer;
    private byte[] Skill1_pointer;
    private byte[] Skill2_pointer;
    private byte[] Skill3_pointer;
    private byte[] Animation1_pointer;
    private byte[] Animation2_pointer;
    private byte unknownbyte1;
    private byte unknownbyte2; // Something about order?
    private byte unknownbyte3;
    private byte unknownbyte4;
    private byte unknownbyte5;
    private byte unknownbyte6;
    private byte level;
    private byte build;
    private byte weight;
    private byte[] bases;
    private byte[] growths;
    private byte[] supportgrowth; // TODO: Unconfirmed, test it.
    private byte unknownbyte9;
    private byte unknownbyte10;
    private byte unknownbyte11;

    private byte[] weaponRanks; // This one is special, as it is not actually in the character entry. It is 10 bytes, the first 8 of which are data

    //////////////////
    // Constructors //
    //////////////////

    public CharacterEntry(byte[] bytes, String name, String PID_name, byte[] weaponRanks) throws WrongSizeException {

        if(bytes.length != 0x54){
            throw new WrongSizeException("A character entry was made out of an array of the wrong size.");
        }

        this.entryName = name;
        this.PID_name = PID_name;

        this.PID_pointer = new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]};
        this.MPID_pointer = new byte[]{bytes[4], bytes[5], bytes[6], bytes[7]};
        Null_pointer = new byte[]{bytes[8], bytes[9], bytes[10], bytes[11]};
        Portrait_pointer = new byte[]{bytes[12], bytes[13], bytes[14], bytes[15]};
        Class_pointer = new byte[]{bytes[16], bytes[17], bytes[18], bytes[19]};
        Affiliation_pointer = new byte[]{bytes[20], bytes[21], bytes[22], bytes[23]};
        Weaponlevel_pointer = new byte[]{bytes[24], bytes[25], bytes[26], bytes[27]};
        Skill1_pointer = new byte[]{bytes[28], bytes[29], bytes[30], bytes[31]};
        Skill2_pointer = new byte[]{bytes[32], bytes[33], bytes[34], bytes[35]};
        Skill3_pointer = new byte[]{bytes[36], bytes[37], bytes[38], bytes[39]};
        Animation1_pointer = new byte[]{bytes[40], bytes[41], bytes[42], bytes[43]};
        Animation2_pointer = new byte[]{bytes[44], bytes[45], bytes[46], bytes[47]};
        this.unknownbyte1 = bytes[48];
        this.unknownbyte2 = bytes[49];
        this.unknownbyte3 = bytes[50];
        this.unknownbyte4 = bytes[51];
        this.unknownbyte5 = bytes[52];
        this.unknownbyte6 = bytes[53];
        this.level = bytes[54];
        this.build = bytes[55];
        this.weight = bytes[56];
        this.bases = new byte[]{bytes[57], bytes[58], bytes[59], bytes[60], bytes[61], bytes[62], bytes[63], bytes[64]};
        this.growths = new byte[]{bytes[65], bytes[66], bytes[67], bytes[68], bytes[69], bytes[70], bytes[71], bytes[72]};
        this.supportgrowth = new byte[]{bytes[73], bytes[74], bytes[75], bytes[76], bytes[77], bytes[78], bytes[79], bytes[80]};
        this.unknownbyte9 = bytes[81];
        this.unknownbyte10 = bytes[82];
        this.unknownbyte11 = bytes[83];

        this.weaponRanks = weaponRanks;
    }

    /////////////
    // Methods //
    /////////////

    // Recombine the variables of the actual character entry portion of this class into a single byte array
    public byte[] getByteArray(){

        byte[] b = new byte[0x54];

        System.arraycopy(PID_pointer, 0, b, 0, 4);
        System.arraycopy(MPID_pointer, 0, b, 4, 4);
        System.arraycopy(Null_pointer, 0, b, 8, 4);
        System.arraycopy(Portrait_pointer, 0, b, 12, 4);
        System.arraycopy(Class_pointer, 0, b, 16, 4);
        System.arraycopy(Affiliation_pointer, 0, b, 20, 4);
        System.arraycopy(Weaponlevel_pointer, 0, b, 24, 4);
        System.arraycopy(Skill1_pointer, 0, b, 28, 4);
        System.arraycopy(Skill2_pointer, 0, b, 32, 4);
        System.arraycopy(Skill3_pointer, 0, b, 36, 4);
        System.arraycopy(Animation1_pointer, 0, b, 40, 4);
        System.arraycopy(Animation2_pointer, 0, b, 44, 4);
        b[48] = unknownbyte1;
        b[49] = unknownbyte2;
        b[50] = unknownbyte3;
        b[51] = unknownbyte4;
        b[52] = unknownbyte5;
        b[53] = unknownbyte6;
        b[54] = level;
        b[55] = build;
        b[56] = weight;
        System.arraycopy(bases, 0, b, 57, 8);
        System.arraycopy(growths, 0, b, 65, 8);
        System.arraycopy(supportgrowth, 0, b, 73, 8);
        b[81] = unknownbyte9;
        b[82] = unknownbyte10;
        b[83] = unknownbyte11;

        return b;
    }


    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public String getDisplayName(){return entryName;}

    public String getEntryName() {
        return entryName;
    }

    public byte[] getPID_pointer() {
        return PID_pointer;
    }

    public void setPID_pointer(byte[] PID_pointer) {
        this.PID_pointer = PID_pointer;
    }

    public byte[] getMPID_pointer() {
        return MPID_pointer;
    }

    public void setMPID_pointer(byte[] MPID_pointer) {
        this.MPID_pointer = MPID_pointer;
    }

    public byte[] getNull_pointer() {
        return Null_pointer;
    }

    public void setNull_pointer(byte[] null_pointer) {
        Null_pointer = null_pointer;
    }

    public byte[] getPortrait_pointer() {
        return Portrait_pointer;
    }

    public void setPortrait_pointer(byte[] portrait_pointer) {
        Portrait_pointer = portrait_pointer;
    }

    public byte[] getClass_pointer() {
        return Class_pointer;
    }

    public void setClass_pointer(byte[] class_pointer) {
        Class_pointer = class_pointer;
    }

    public byte[] getAffiliation_pointer() {
        return Affiliation_pointer;
    }

    public void setAffiliation_pointer(byte[] affiliation_pointer) {
        Affiliation_pointer = affiliation_pointer;
    }

    public byte[] getWeaponlevel_pointer() {
        return Weaponlevel_pointer;
    }

    public void setWeaponlevel_pointer(byte[] weaponlevel_pointer) {
        Weaponlevel_pointer = weaponlevel_pointer;
    }

    public byte[] getSkill1_pointer() {
        return Skill1_pointer;
    }

    public void setSkill1_pointer(byte[] skill1_pointer) {
        Skill1_pointer = skill1_pointer;
    }

    public byte[] getSkill2_pointer() {
        return Skill2_pointer;
    }

    public void setSkill2_pointer(byte[] skill2_pointer) {
        Skill2_pointer = skill2_pointer;
    }

    public byte[] getSkill3_pointer() {
        return Skill3_pointer;
    }

    public void setSkill3_pointer(byte[] skill3_pointer) {
        Skill3_pointer = skill3_pointer;
    }

    public byte[] getAnimation1_pointer() {
        return Animation1_pointer;
    }

    public void setAnimation1_pointer(byte[] animation1_pointer) {
        Animation1_pointer = animation1_pointer;
    }

    public byte[] getAnimation2_pointer() {
        return Animation2_pointer;
    }

    public void setAnimation2_pointer(byte[] animation2_pointer) {
        Animation2_pointer = animation2_pointer;
    }

    public byte getUnknownbyte1() {
        return unknownbyte1;
    }

    public void setUnknownbyte1(byte unknownbyte1) {
        this.unknownbyte1 = unknownbyte1;
    }

    public byte getUnknownbyte2() {
        return unknownbyte2;
    }

    public void setUnknownbyte2(byte unknownbyte2) {
        this.unknownbyte2 = unknownbyte2;
    }

    public byte getUnknownbyte3() {
        return unknownbyte3;
    }

    public void setUnknownbyte3(byte unknownbyte3) {
        this.unknownbyte3 = unknownbyte3;
    }

    public byte getUnknownbyte4() {
        return unknownbyte4;
    }

    public void setUnknownbyte4(byte unknownbyte4) {
        this.unknownbyte4 = unknownbyte4;
    }

    public byte getUnknownbyte5() {
        return unknownbyte5;
    }

    public void setUnknownbyte5(byte unknownbyte5) {
        this.unknownbyte5 = unknownbyte5;
    }

    public byte getUnknownbyte6() {
        return unknownbyte6;
    }

    public void setUnknownbyte6(byte unknownbyte6) {
        this.unknownbyte6 = unknownbyte6;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getBuild() {
        return build;
    }

    public void setBuild(byte build) {
        this.build = build;
    }

    public byte getWeight() {
        return weight;
    }

    public void setWeight(byte weight) {
        this.weight = weight;
    }

    public byte[] getBases() {
        return bases;
    }

    public void setBases(byte[] bases) {
        this.bases = bases;
    }

    public byte[] getGrowths() {
        return growths;
    }

    public void setGrowths(byte[] growths) {
        this.growths = growths;
    }

    public byte[] getSupportgrowth() {
        return supportgrowth;
    }

    public void setSupportgrowth(byte[] supportgrowth) {
        this.supportgrowth = supportgrowth;
    }

    public byte getUnknownbyte9() {
        return unknownbyte9;
    }

    public void setUnknownbyte9(byte unknownbyte9) {
        this.unknownbyte9 = unknownbyte9;
    }

    public byte getUnknownbyte10() {
        return unknownbyte10;
    }

    public void setUnknownbyte10(byte unknownbyte10) {
        this.unknownbyte10 = unknownbyte10;
    }

    public byte getUnknownbyte11() {
        return unknownbyte11;
    }

    public void setUnknownbyte11(byte unknownbyte11) {
        this.unknownbyte11 = unknownbyte11;
    }


    public byte[] getWeaponRanks() {
        return weaponRanks;
    }

    public void setWeaponRanks(byte[] weaponRanks) {
        this.weaponRanks = weaponRanks;
    }

    public String getPID_name() {
        return PID_name;
    }
}
