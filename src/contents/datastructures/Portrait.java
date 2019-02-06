package contents.datastructures;

import contents.datastructures.interfaces.SystemCmpContents;
import contents.io.SVar;
import contents.math.ByteMath;

public class Portrait implements SystemCmpContents {

    private String FID_name;
    private byte[] FID_pointer;

    /////////////////
    // Constructor //
    /////////////////


    public Portrait(String FID_name, byte[] FID_pointer) {
        this.FID_name = FID_name;
        this.FID_pointer = FID_pointer;
    }

    public Portrait(String[] parameters){
        if(parameters.length != 2){
            throw new IllegalArgumentException();
        }

        this.FID_name = parameters[0];
        if (SVar.region == 0x10){
            // NA case
            this.FID_pointer = ByteMath.byteArrayAddInt(ByteMath.hexStringToByteArray(parameters[1]), 0);
        } else {
            // PAL case
            this.FID_pointer = ByteMath.hexStringToByteArray(parameters[1]);
        }
    }

    /////////////
    // Getters //
    /////////////

    public String getDisplayName(){
        return FID_name;
    }

    public String getFID_name() {
        return FID_name;
    }

    public byte[] getFID_pointer() {
        return FID_pointer;
    }

    public Integer getKey(){
        return ByteMath.bytesToInt(FID_pointer);
    }

    @Override
    public String getID_name() {
        return FID_name;
    }

    @Override
    public byte[] getID_pointer() {
        return FID_pointer;
    }
}
