package contents.datastructures;

import contents.datastructures.interfaces.SystemCmpContents;
import contents.io.SVar;
import contents.math.ByteMath;

public class Animate implements SystemCmpContents {

    private String AID_name;
    private byte[] AID_pointer;

    /////////////////
    // Constructor //
    /////////////////

    public Animate(String AID_name, byte[] AID_pointer) {
        this.AID_name = AID_name;
        this.AID_pointer = AID_pointer;
    }

    public Animate(String[] parameters) {
        if(parameters.length != 2){
            throw new IllegalArgumentException();
        }

        this.AID_name = parameters[0];
        if (SVar.region == 0x10){
            // NA case
            this.AID_pointer = ByteMath.byteArrayAddInt(ByteMath.hexStringToByteArray(parameters[1]), 0);
        } else {
            // PAL case
            this.AID_pointer = ByteMath.hexStringToByteArray(parameters[1]);
        }
    }


    /////////////
    // Getters //
    /////////////

    public String getAID_name() {
        return AID_name;
    }

    public byte[] getAID_pointer() {
        return AID_pointer;
    }

    @Override
    public String getDisplayName() {
        return AID_name;
    }

    @Override
    public Integer getKey() {
        return ByteMath.bytesToInt(AID_pointer);
    }

    @Override
    public String getID_name() {
        return AID_name;
    }

    @Override
    public byte[] getID_pointer() {
        return AID_pointer;
    }
}
