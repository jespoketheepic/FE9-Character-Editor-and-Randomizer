package contents.io;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class AutoReader {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Shit code so i no longer need to manually write the resource files.                       //
    // Don't delete the commented code, it is only commented to avoid accidentally running it.   //
    // I keep it around in case i need to re-make one of the resource files with new parameters. //
    ///////////////////////////////////////////////////////////////////////////////////////////////


    public static void readDataToFile(){

//        try {
//            File dest = new File("C:\\Users\\jespo\\Documents\\FE Randomizers\\PoR\\Path of Radiance editing\\system3.cmp");
//            RandomAccessFile destFile = new RandomAccessFile(dest, "rw");
//            destFile.seek(0x001455A0);
//
//            final byte[] rank = new byte[]{'-','E','D','C','B','A','S'};
//            byte[] buffer = new byte[]{'-','-','-','-','-','-','-','-','-',0x00};
//            destFile.write(buffer);
//
//            for(int prim = 0; prim < 8; prim++){
//                for(int sec = prim+1; sec < 8; sec++){
//                    for(byte primrank = 1; primrank < 7; primrank++){
//                        buffer[prim] = rank[primrank];
//                        for(byte secrank = 0; secrank < 7; secrank++){
//                            buffer[sec] = rank[secrank];
//                            destFile.write(buffer);
//                        }
//                    }
//                    buffer[sec] = '-';
//                }
//                buffer[prim] = '-';
//            }
//
//            destFile.close();
//        } catch (Exception e){
//            e.printStackTrace();
//        }

//        ////////////////////////////////////////////////////
//        // Hardcode what we are reading here for each run //
//        ////////////////////////////////////////////////////
//        File source = new File("C:\\Users\\jespo\\Documents\\FE Randomizers\\PoR\\Path of Radiance editing\\system_ORIGINAL.cmp.decompressed");
//        File dest = new File("C:\\Users\\jespo\\IdeaProjects\\FE9Randomizer2.0\\resources\\Items.txt");
//        byte entrySeparator = 0x00;
//        int sourceOffset = 0x000161F6;
//        ////////////////////////////////////////////////////
//
//        try (BufferedInputStream sourceStream = new BufferedInputStream(new FileInputStream(source));
//             BufferedWriter destStream = new BufferedWriter(new FileWriter(dest, true))){
//
//            if(sourceStream.skip(sourceOffset) != sourceOffset){
//                throw new IOException("Failed skip?");
//            }
//
//            String stringBuffer = new String(InputStreamTools.readUntilSeparator(sourceStream, entrySeparator), StandardCharsets.UTF_8);
//            while (stringBuffer.substring(0,4).equals("IID_")){
//                destStream.write(stringBuffer + ",000");
//                destStream.write(Integer.toHexString(sourceOffset - 0x200).toUpperCase());
//                sourceOffset += stringBuffer.length() + 1;
//                destStream.write(",Item\n");
//
//                stringBuffer = new String(InputStreamTools.readUntilSeparator(sourceStream, entrySeparator), StandardCharsets.UTF_8);
//                // Protect the .substring call in the while condition
//                if (stringBuffer.length() < 4){ break; }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("What are you up to with that AutoReader???");
//        }

        ////////////////////////////////////////////////////
//        // Hardcode what we are reading here for each run //
//        ////////////////////////////////////////////////////
//        File source = new File("C:\\Users\\jespo\\Documents\\FE Randomizers\\PoR\\Path of Radiance editing\\system_ORIGINAL.cmp.decompressed");
//        File dest = new File("C:\\Users\\jespo\\IdeaProjects\\FE9Randomizer2.0\\resources\\Portraits.txt");
//        byte entrySeparator = 0x00;
//        int sourceOffset = 0x00042D08; // 0x00015D49; //
//        ////////////////////////////////////////////////////
//
//        try (BufferedInputStream sourceStream = new BufferedInputStream(new FileInputStream(source));
//             BufferedWriter destStream = new BufferedWriter(new FileWriter(dest, true))){
//
//            if(sourceStream.skip(sourceOffset) != sourceOffset){
//                throw new IOException("Failed skip?");
//            }
//
//            String stringBuffer = new String(InputStreamTools.readUntilSeparator(sourceStream, entrySeparator), StandardCharsets.UTF_8);
//            while (stringBuffer.substring(0,4).equals("FID_")){
//                destStream.write(stringBuffer + ",000");
//                destStream.write(Integer.toHexString(sourceOffset - 0x200).toUpperCase());
//                sourceOffset += stringBuffer.length() + 1;
//                destStream.write("\n");
//
//                stringBuffer = new String(InputStreamTools.readUntilSeparator(sourceStream, entrySeparator), StandardCharsets.UTF_8);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("What are you up to with that AutoReader???");
//        }

        ////////////////////////////////////////////////////
        // Hardcode what we are reading here for each run //
        ////////////////////////////////////////////////////
        File source = new File("C:\\Users\\jespo\\Documents\\FE Randomizers\\PoR\\Path of Radiance editing\\system_ORIGINAL_decompressed.cmp");
        File dest = new File("C:\\Users\\jespo\\IdeaProjects\\FE9Randomizer2.0\\resources\\MPID.txt");
        byte entrySeparator = 0x00;
        int sourceOffset = 0x00018F4B;
        ////////////////////////////////////////////////////

        try (BufferedInputStream sourceStream = new BufferedInputStream(new FileInputStream(source));
             BufferedWriter destStream = new BufferedWriter(new FileWriter(dest, true))){

            if(sourceStream.skip(sourceOffset) != sourceOffset){
                throw new IOException("Failed skip?");
            }

            String stringBuffer = new String(InputStreamTools.readUntilSeparator(sourceStream, entrySeparator), StandardCharsets.UTF_8);
            while (stringBuffer.substring(0,5).equals("MPID_")){
                destStream.write(stringBuffer + ",000");
                destStream.write(Integer.toHexString(sourceOffset - 0x200).toUpperCase());
                sourceOffset += stringBuffer.length() + 1;
                destStream.write("\n");

                stringBuffer = new String(InputStreamTools.readUntilSeparator(sourceStream, entrySeparator), StandardCharsets.UTF_8);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("What are you up to with that AutoReader???");
        }


//        ////////////////////////////////////////////////////
//        // Hardcode what we are reading here for each run //
//        ////////////////////////////////////////////////////
//        File source = new File("C:\\Users\\jespo\\Documents\\FE Randomizers\\PoR\\Path of Radiance editing\\system_ORIGINAL.cmp.decompressed");
//        File dest = new File("C:\\Users\\jespo\\IdeaProjects\\FE9Randomizer2.0\\resources\\Portraits.txt");
//        byte entrySeparator = 0x00;
//        int sourceOffset = 0x00042D08; // 0x00015D49; //
//        ////////////////////////////////////////////////////
//
//        try (BufferedInputStream sourceStream = new BufferedInputStream(new FileInputStream(source));
//             BufferedWriter destStream = new BufferedWriter(new FileWriter(dest, true))){
//
//            if(sourceStream.skip(sourceOffset) != sourceOffset){
//                throw new IOException("Failed skip?");
//            }
//
//            String stringBuffer = new String(InputStreamTools.readUntilSeparator(sourceStream, entrySeparator), StandardCharsets.UTF_8);
//            while (stringBuffer.substring(0,4).equals("FID_")){
//                destStream.write(stringBuffer + ",000");
//                destStream.write(Integer.toHexString(sourceOffset - 0x200).toUpperCase());
//                sourceOffset += stringBuffer.length() + 1;
//                destStream.write("\n");
//
//                stringBuffer = new String(InputStreamTools.readUntilSeparator(sourceStream, entrySeparator), StandardCharsets.UTF_8);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("What are you up to with that AutoReader???");
//        }

//        ////////////////////////////////////////////////////
//        // Hardcode what we are reading here for each run //
//        ////////////////////////////////////////////////////
//        File source = new File("C:\\Users\\jespo\\Documents\\FE Randomizers\\PoR\\Path of Radiance editing\\system_ORIGINAL.cmp.decompressed");
//        File dest = new File("C:\\Users\\jespo\\IdeaProjects\\FE9Randomizer2.0\\resources\\Animations.txt");
//        byte entrySeparator = 0x00;
//        int sourceOffset = 0x00026754; //0x00014C9A;
//        ////////////////////////////////////////////////////
//
//        try (BufferedInputStream sourceStream = new BufferedInputStream(new FileInputStream(source));
//             BufferedWriter destStream = new BufferedWriter(new FileWriter(dest, true))){
//
//            if(sourceStream.skip(sourceOffset) != sourceOffset){
//                throw new IOException("Failed skip?");
//            }
//
//            String stringBuffer = new String(InputStreamTools.readUntilSeparator(sourceStream, entrySeparator), StandardCharsets.UTF_8);
//            while (stringBuffer.substring(0,4).equals("AID_")){
//                destStream.write(stringBuffer + ",000");
//                destStream.write(Integer.toHexString(sourceOffset - 0x200).toUpperCase());
//                sourceOffset += stringBuffer.length() + 1;
//                destStream.write("\n");
//
//                stringBuffer = new String(InputStreamTools.readUntilSeparator(sourceStream, entrySeparator), StandardCharsets.UTF_8);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("What are you up to with that AutoReader???");
//        }

//    // Shit code so i no longer need to manually write the resource files
//    public static void readDataToFileClass(){
//
//        ////////////////////////////////////////////////////
//        // Hardcode what we are reading here for each run //
//        ////////////////////////////////////////////////////
//        File source1 = new File("C:\\Users\\jespo\\IdeaProjects\\FE9Randomizer2.0\\resources\\ClassNames.txt");
//        File source2 = new File("C:\\Users\\jespo\\IdeaProjects\\FE9Randomizer2.0\\resources\\ClassSystemCmpPointers.bin");
//        File source3 = new File("C:\\Users\\jespo\\IdeaProjects\\FE9Randomizer2.0\\resources\\ClassData.bin");
//        File dest = new File("C:\\Users\\jespo\\IdeaProjects\\FE9Randomizer2.0\\resources\\Class.txt");
//        byte entrySeparator = 0x00;
////        int sourceOffset = 0x00;
//        ////////////////////////////////////////////////////
//
//        String stringBuffer;
//        byte[] buffer, buffer2;
//
//        try (BufferedReader source1Stream = new BufferedReader(new FileReader(source1));
//             BufferedInputStream source2Stream = new BufferedInputStream(new FileInputStream(source2));
//             BufferedInputStream source3Stream = new BufferedInputStream(new FileInputStream(source3));
//             BufferedWriter destStream = new BufferedWriter(new FileWriter(dest, true))){
//
//            Map<Integer, Animate> animationsMap = ExternalFileMapmaker.readAnimationsFile();
//            Map<Integer, Inventory> inventoryMap = ExternalFileMapmaker.readInventoryFile();
//
//            while((stringBuffer = source1Stream.readLine()) != null) {
//                destStream.write( stringBuffer + ","
//                        + new String(InputStreamTools.readUntilSeparator(source3Stream, entrySeparator), StandardCharsets.UTF_8) + ","
//                        + ByteMath.byteArrayToHexString(InputStreamTools.read4Bytes(source2Stream)) + ","
//                        + makeID_nameID_pointerPairString(animationsMap, new String(InputStreamTools.readUntilSeparator(source3Stream, entrySeparator), StandardCharsets.UTF_8))
//                        + makeID_nameID_pointerPairString(animationsMap, new String(InputStreamTools.readUntilSeparator(source3Stream, entrySeparator), StandardCharsets.UTF_8))
//                        + makeID_nameID_pointerPairString(inventoryMap, new String(InputStreamTools.readUntilSeparator(source3Stream, entrySeparator), StandardCharsets.UTF_8)));
//                // Fuck Java for needing this workaround for a freaking byte -> String conversion >:(
//                buffer = InputStreamTools.readUntilSeparator(source3Stream, entrySeparator);
//                buffer2 = new byte[]{buffer[0]};
//                destStream.write(new String(buffer2, StandardCharsets.UTF_8) + "," + buffer[1] + "\n");
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("What are you up to with that AutoReader???");
//        }
//    }
//
//    private static <K,V extends SystemCmpContents> String makeID_nameID_pointerPairString(Map<K,V> map, String ID_name){
//
//        Optional<V> v = map.values().stream().filter(thing -> thing.getID_name().equals(ID_name)).findFirst();
//        if (v.isPresent()){
//            return ID_name + "," + ByteMath.byteArrayToHexString(v.get().getID_pointer()) + ",";
//        } else {
//            throw new RuntimeException("The given ID_name is not in the map:" + ID_name);
//        }
    }
}
