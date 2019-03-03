package contents.datastructures;

import contents.io.InputStreamTools;
import contents.math.ByteMath;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisposEntry {

    private String PID_name;
    private int offset;
    private byte[] data;

    private byte[] class_pointer;
    private String JID_name;
    private byte[] weapon1_pointer;
    private String weapon1_IID_name;
    private byte[] weapon2_pointer;
    private String weapon2_IID_name;
    private byte[] weapon3_pointer;
    private String weapon3_IID_name;
    private byte[] weapon4_pointer;
    private String weapon4_IID_name;
    private byte[] item1_pointer;
    private String item1_IID_name;
    private byte[] item2_pointer;
    private String item2_IID_name;
    private byte[] item3_pointer;
    private String item3_IID_name;
    private byte[] item4_pointer;
    private String item4_IID_name;
    private byte[] coordinates;

    // The entries for pre-joining appearances
    private List <DisposEntry> subEntries;

    /////////////////
    // Constructor //
    /////////////////

    public DisposEntry(String PID_name, int offset, RandomAccessFile disposCmp, int header) throws IOException {
        this.PID_name = PID_name;
        this.offset = offset;
        this.data = new byte[0x6C];

        disposCmp.seek(offset);
        if(disposCmp.read(data) != 0x6C){
            throw new IOException("Failed a read() on " + PID_name);
        }

        this.class_pointer = Arrays.copyOfRange(data,4,8);
        this.weapon1_pointer = Arrays.copyOfRange(data,12,16);
        this.weapon2_pointer = Arrays.copyOfRange(data,16,20);
        this.weapon3_pointer = Arrays.copyOfRange(data,20,24);
        this.weapon4_pointer = Arrays.copyOfRange(data,24,28);
        this.item1_pointer = Arrays.copyOfRange(data,28,32);
        this.item2_pointer = Arrays.copyOfRange(data,32,36);
        this.item3_pointer = Arrays.copyOfRange(data,36,40);
        this.item4_pointer = Arrays.copyOfRange(data,40,44);
        this.coordinates = Arrays.copyOfRange(data, 0x5C, 0x60);
        
        disposCmp.seek(ByteMath.bytesToInt(class_pointer) + header);
        this.JID_name = new String(InputStreamTools.readUntilSeparator(disposCmp, (byte) 0x00), StandardCharsets.UTF_8);

        this.weapon1_IID_name = readItemName(weapon1_pointer, disposCmp, header);
        this.weapon2_IID_name = readItemName(weapon2_pointer, disposCmp, header);
        this.weapon3_IID_name = readItemName(weapon3_pointer, disposCmp, header);
        this.weapon4_IID_name = readItemName(weapon4_pointer, disposCmp, header);
        this.item1_IID_name = readItemName(item1_pointer, disposCmp, header);
        this.item2_IID_name = readItemName(item2_pointer, disposCmp, header);
        this.item3_IID_name = readItemName(item3_pointer, disposCmp, header);
        this.item4_IID_name = readItemName(item4_pointer, disposCmp, header);

    }

    /////////////////////
    // Support Methods //
    /////////////////////

    private String readItemName(byte[] pointer, RandomAccessFile disposCmp, int header) throws IOException{
        if((ByteMath.bytesToInt(pointer)) != 0){
            disposCmp.seek(ByteMath.bytesToInt(pointer) + header);
            return new String(InputStreamTools.readUntilSeparator(disposCmp, (byte) 0x00), StandardCharsets.UTF_8);
        } else {
            return "IID_NULL";
        }
    }

    public void setSubEntries(DisposEntry previousSubEntry){
        // If there was a previous subentry, add it and any subentries it has as subentries of this entry.
        if(previousSubEntry != null){
            this.subEntries = (previousSubEntry.getSubEntries() != null) ?
                    new ArrayList<>(previousSubEntry.getSubEntries()) :
                    new ArrayList<>();
            this.subEntries.add(previousSubEntry);
        }
    }
    
    /////////////
    // Getters //
    /////////////

    public String getPID_name() {
        return PID_name;
    }

    public int getOffset() {
        return offset;
    }

    // Update data with the data from the other fields, and then return it
    public byte[] getData() {
        System.arraycopy(class_pointer,0,data,4,4);
        System.arraycopy(weapon1_pointer,0,data,12,4);
        System.arraycopy(weapon2_pointer,0,data,16,4);
        System.arraycopy(weapon3_pointer,0,data,20,4);
        System.arraycopy(weapon4_pointer,0,data,24,4);
        System.arraycopy(item1_pointer,0,data,28,4);
        System.arraycopy(item2_pointer,0,data,32,4);
        System.arraycopy(item3_pointer,0,data,36,4);
        System.arraycopy(item4_pointer,0,data,40,4);
        System.arraycopy(coordinates, 0, data, 0x5C, 4);

        return data;
    }

    public String getJID_name() {
        return JID_name;
    }

    public void setJID_name(String JID_name) {
        this.JID_name = JID_name;

        // Sync subentries
        if(this.subEntries != null) {
            for (DisposEntry entry : this.subEntries) {
                entry.setJID_name(JID_name);
            }
        }
    }

    public byte[] getClass_pointer() {
        return class_pointer;
    }

    public void setClass_pointer(byte[] class_pointer) {
        this.class_pointer = class_pointer;

        // Sync subentries
        if(this.subEntries != null) {
            for (DisposEntry entry : this.subEntries) {
                entry.setClass_pointer(class_pointer);
            }
        }
    }

    public String getWeapon1_IID_name() {
        return weapon1_IID_name;
    }

    public void setWeapon1_IID_name(String weapon1_IID_name) {
        this.weapon1_IID_name = weapon1_IID_name;

        // Sync subentries
        if(this.subEntries != null) {
            for (DisposEntry entry : this.subEntries) {
                entry.setWeapon1_IID_name(weapon1_IID_name);
            }
        }
    }

    public String getWeapon2_IID_name() {
        return weapon2_IID_name;
    }

    public void setWeapon2_IID_name(String weapon2_IID_name) {
        this.weapon2_IID_name = weapon2_IID_name;

        // Sync subentries
        if(this.subEntries != null) {
            for (DisposEntry entry : this.subEntries) {
                entry.setWeapon2_IID_name(weapon2_IID_name);
            }
        }
    }

    public String getWeapon3_IID_name() {
        return weapon3_IID_name;
    }

    public void setWeapon3_IID_name(String weapon3_IID_name) {
        this.weapon3_IID_name = weapon3_IID_name;

        // Sync subentries
        if(this.subEntries != null) {
            for (DisposEntry entry : this.subEntries) {
                entry.setWeapon3_IID_name(weapon3_IID_name);
            }
        }
    }

    public String getWeapon4_IID_name() {
        return weapon4_IID_name;
    }

    public void setWeapon4_IID_name(String weapon4_IID_name) {
        this.weapon4_IID_name = weapon4_IID_name;

        // Sync subentries
        if(this.subEntries != null) {
            for (DisposEntry entry : this.subEntries) {
                entry.setWeapon4_IID_name(weapon4_IID_name);
            }
        }
    }

    public String getItem1_IID_name() {
        return item1_IID_name;
    }

    public void setItem1_IID_name(String item1_IID_name) {
        this.item1_IID_name = item1_IID_name;

        // Sync subentries
        if(this.subEntries != null) {
            for (DisposEntry entry : this.subEntries) {
                entry.setItem1_IID_name(item1_IID_name);
            }
        }
    }

    public String getItem2_IID_name() {
        return item2_IID_name;
    }

    public void setItem2_IID_name(String item2_IID_name) {
        this.item2_IID_name = item2_IID_name;

        // Sync subentries
        if(this.subEntries != null) {
            for (DisposEntry entry : this.subEntries) {
                entry.setItem2_IID_name(item2_IID_name);
            }
        }
    }

    public String getItem3_IID_name() {
        return item3_IID_name;
    }

    public void setItem3_IID_name(String item3_IID_name) {
        this.item3_IID_name = item3_IID_name;

        // Sync subentries
        if(this.subEntries != null) {
            for (DisposEntry entry : this.subEntries) {
                entry.setItem3_IID_name(item3_IID_name);
            }
        }
    }

    public String getItem4_IID_name() {
        return item4_IID_name;
    }

    public void setItem4_IID_name(String item4_IID_name) {
        this.item4_IID_name = item4_IID_name;

        // Sync subentries
        if(this.subEntries != null) {
            for (DisposEntry entry : this.subEntries) {
                entry.setItem4_IID_name(item4_IID_name);
            }
        }
    }

    public byte[] getWeapon1_pointer() {
        return weapon1_pointer;
    }

    public void setWeapon1_pointer(byte[] weapon1_pointer) {
        this.weapon1_pointer = weapon1_pointer;
    }

    public byte[] getWeapon2_pointer() {
        return weapon2_pointer;
    }

    public void setWeapon2_pointer(byte[] weapon2_pointer) {
        this.weapon2_pointer = weapon2_pointer;
    }

    public byte[] getWeapon3_pointer() {
        return weapon3_pointer;
    }

    public void setWeapon3_pointer(byte[] weapon3_pointer) {
        this.weapon3_pointer = weapon3_pointer;
    }

    public byte[] getWeapon4_pointer() {
        return weapon4_pointer;
    }

    public void setWeapon4_pointer(byte[] weapon4_pointer) {
        this.weapon4_pointer = weapon4_pointer;
    }

    public byte[] getItem1_pointer() {
        return item1_pointer;
    }

    public void setItem1_pointer(byte[] item1_pointer) {
        this.item1_pointer = item1_pointer;
    }

    public byte[] getItem2_pointer() {
        return item2_pointer;
    }

    public void setItem2_pointer(byte[] item2_pointer) {
        this.item2_pointer = item2_pointer;
    }

    public byte[] getItem3_pointer() {
        return item3_pointer;
    }

    public void setItem3_pointer(byte[] item3_pointer) {
        this.item3_pointer = item3_pointer;
    }

    public byte[] getItem4_pointer() {
        return item4_pointer;
    }

    public void setItem4_pointer(byte[] item4_pointer) {
        this.item4_pointer = item4_pointer;
    }

    public byte[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(byte[] coordinates) {
        this.coordinates = coordinates;
    }

    public void setCoordinates(byte sX, byte sY, byte fX, byte fY) {
        this.coordinates[0] = sX;
        this.coordinates[1] = sY;
        this.coordinates[2] = fX;
        this.coordinates[3] = fY;
    }

    public void setCoordinates(int sX, int sY, int fX, int fY) {
        setCoordinates((byte) sX, (byte) sY, (byte) fX, (byte) fY);
    }

    public List<DisposEntry> getSubEntries() {
        return subEntries;
    }
}
