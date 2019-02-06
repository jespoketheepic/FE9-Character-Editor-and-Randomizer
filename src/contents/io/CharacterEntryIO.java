package contents.io;


import contents.datastructures.CharacterEntry;
import contents.gui.RootControler;
import contents.math.ByteMath;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class CharacterEntryIO {

    private final static int headerPAL = 0x200;
    private final static int headerNA = 0x240;
    private final static int freeRealEstateOffsetPAL = 0x001455A0;
    private final static int freeRealEstateOffsetNA = 0x0019FCB0;

    // Function for taking character entries out of system.cmp into CharacterEntry objects.
    // Returns the specific Map implementation to reassure caller that the Map is in the expected order.
    @SuppressWarnings("Duplicates")
    public static LinkedHashMap<String, CharacterEntry> collectCharacterEntries(String rootDirectory) throws IOException{
        LinkedHashMap<String, CharacterEntry> characterEntryMap = new LinkedHashMap<>();
        byte[] InputBuffer = new byte[0x54];
        byte[] weaponRankBuffer = new byte[10];
        StringBuilder PID_name_builder = new StringBuilder();
        int readBuffer;
        int header;

        // Open files for read
        BufferedInputStream systemCmp = new BufferedInputStream(new FileInputStream(rootDirectory + File.separator + "system.cmp"));
        RandomAccessFile systemCmpButCooler = new RandomAccessFile(rootDirectory + File.separator + "system.cmp", "r");
        BufferedReader namesTxt = new BufferedReader(new InputStreamReader(CharacterEntryIO.class.getResourceAsStream("/Names.txt"), StandardCharsets.UTF_8));

        // Region check
        systemCmpButCooler.seek(5);
        SVar.region = systemCmpButCooler.read();
        header = regionalHeader(SVar.region);

        // Navigate to character entries
        if(systemCmp.skip(header + 0x10) < header + 0x10){
            throw new IOException("Failed to navigate to character entries in system.cmp");
        }

        // Load in the character entries
        for(int i = 0; i < 0x154; i++){
            // Read into buffer
            if(systemCmp.read(InputBuffer) < InputBuffer.length){
                throw new IOException("Failed to read character entry nr " + i);
            }

            // Get weapon ranks
            systemCmpButCooler.seek(ByteMath.bytesToInt(new byte[]{InputBuffer[24],InputBuffer[25],InputBuffer[26],InputBuffer[27]}) + header);
            systemCmpButCooler.read(weaponRankBuffer);

            // Get PID_name
            PID_name_builder.setLength(0);
            systemCmpButCooler.seek(ByteMath.bytesToInt(new byte[]{InputBuffer[0],InputBuffer[1],InputBuffer[2],InputBuffer[3]}) + header);
            while ((readBuffer = systemCmpButCooler.read()) != 0) {
                if (readBuffer == -1) {
                    throw new EOFException("Reading PID_name in entry " + i + " went wrong and hit EOF.");
                } else {
                    PID_name_builder.append(ByteMath.byteToString_UTF8((byte) readBuffer));
                }
            }

            // Get the name
            String name = namesTxt.readLine();
            // I don't feel like filling out the names for all the enemy entries.
            if(name == null){
                name = PID_name_builder.toString();
            }

            // Add new characterentry to the list
            CharacterEntry characterEntryBuffer = new CharacterEntry(InputBuffer, name, PID_name_builder.toString(), weaponRankBuffer.clone());
            characterEntryMap.put(characterEntryBuffer.getPID_name(), characterEntryBuffer);
        }

        systemCmp.close();
        namesTxt.close();

        return characterEntryMap;
    }


    // Write character entries to system.cmp
    // Calls for specific Map implementation to ensure it is correctly ordered.
    @SuppressWarnings("Duplicates")
    public static void writeCharacterEntries(LinkedHashMap<String, CharacterEntry> characterEntryMap, String rootDirectory) throws IOException {
        // Open file for read/write
        RandomAccessFile systemCmp = new RandomAccessFile(rootDirectory + File.separator + "system.cmp", "rw");
        int freeRealEstateOffset;
        int header;

        // Region check
        header = regionalHeader(SVar.region);

        // Regional free realestate
        if (SVar.region == 0x0E){
            // PAL case
            freeRealEstateOffset = freeRealEstateOffsetPAL;
        } else if (SVar.region == 0x10){
            // NA case
            freeRealEstateOffset = freeRealEstateOffsetNA;
        } else {
            throw new IOException("system.cmp not identified as either PAL or NA");
        }

        // Navigate to free real estate for weapon ranks
        try{
            systemCmp.seek(freeRealEstateOffset);
        } catch (IOException e){
            System.out.println(e.getMessage());
            throw new IOException("Failed to navigate to weapon rank space in system.cmp");
        }

        // Change the weapon rank pointers to the empty lot, and then put the desired ranks there
        for(CharacterEntry characterEntry : characterEntryMap.values()){
            systemCmp.write(characterEntry.getWeaponRanks());
            characterEntry.setWeaponlevel_pointer(ByteMath.intToBytes(freeRealEstateOffset - header));
            freeRealEstateOffset += 10;
        }

        // Navigate to character entries
        systemCmp.seek(header + 0x10);

        // Write the character entries
        for(CharacterEntry characterEntry : characterEntryMap.values()){
            systemCmp.write(characterEntry.getByteArray());
        }

        // Close stream
        systemCmp.close();
    }

    private static int regionalHeader(int region) throws IOException{
        if (region == 0x0E){
            // PAL case
            return headerPAL;

        } else if (region == 0x10){
            // NA case
            return headerNA;
        } else {
            throw new IOException("system.cmp not identified as either PAL or NA");
        }
    }
}
