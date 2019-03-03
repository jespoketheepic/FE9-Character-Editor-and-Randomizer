package contents.datastructures;

import contents.datastructures.interfaces.SystemCmpContents;
import contents.io.SVar;
import contents.math.ByteMath;

public class MPID implements SystemCmpContents {

    private String MPID_name;
    private byte[] MPID_pointer;

    public MPID(String MPID_name, byte[] MPID_pointer) {
        this.MPID_name = MPID_name;
        this.MPID_pointer = MPID_pointer;
    }

    public MPID(String[] parameters){
        if(parameters.length != 2){
            throw new IllegalArgumentException();
        }

        this.MPID_name = parameters[0];
        if (SVar.region == 0x10){
            // NA case
            this.MPID_pointer = ByteMath.byteArrayAddInt(ByteMath.hexStringToByteArray(parameters[1]), -0x66);
        } else {
            // PAL case
            this.MPID_pointer = ByteMath.hexStringToByteArray(parameters[1]);
        }
    }

    @Override
    public Integer getKey() {
        return ByteMath.bytesToInt(MPID_pointer);
    }

    @Override
    public String getID_name() {
        return MPID_name;
    }

    @Override
    public byte[] getID_pointer() {
        return MPID_pointer;
    }

    @Override
    public String getDisplayName() {
        return MPID_name;
    }
}
