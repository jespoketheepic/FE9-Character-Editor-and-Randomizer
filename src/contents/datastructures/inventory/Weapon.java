package contents.datastructures.inventory;

import contents.datastructures.interfaces.Inventory;
import contents.io.SVar;
import contents.math.ByteMath;

public class Weapon implements Inventory {

    private String name;
    private String IID_name;
    private byte[] IID_pointer;
    private String type; // Lowercase, whole word

    /////////////////
    // Constructor //
    /////////////////

    public Weapon(String name, String IID_name, byte[] IID_pointer, String type) {
        this.name = name;
        this.IID_name = IID_name;
        this.IID_pointer = IID_pointer;
        this.type = type.toLowerCase();
    }

    public Weapon(String[] parameters){
        if(parameters.length != 4){
            throw new IllegalArgumentException();
        }
        this.name = parameters[0];
        this.IID_name = parameters[1];
        if (SVar.region == 0x10){
            // NA case
            this.IID_pointer = ByteMath.byteArrayAddInt(ByteMath.hexStringToByteArray(parameters[2]), -0x38);
        } else {
            // PAL case
            this.IID_pointer = ByteMath.hexStringToByteArray(parameters[2]);
        }
        this.type = parameters[3].toLowerCase();
    }

    /////////////
    // Getters //
    /////////////

    public String getName() {
        return name;
    }

    public String getIID_name() {
        return IID_name;
    }

    public byte[] getIID_pointer() {
        return IID_pointer;
    }

    public String getType(){ return type; }

    @Override
    public String getKey() {
        return IID_name;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getID_name() {
        return IID_name;
    }

    @Override
    public byte[] getID_pointer() {
        return IID_pointer;
    }
}
