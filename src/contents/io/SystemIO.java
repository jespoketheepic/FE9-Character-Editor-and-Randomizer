package contents.io;

import contents.datastructures.ClassGrowths;
import contents.math.ByteMath;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SystemIO {

    private final static int freeRealEstateOffsetPAL = 0x001455A0;
    private final static int freeRealEstateOffsetNA = 0x0019FCB0;
    private static final int ClassTableOffsetPAL = 0x000071A4;
    private static final int ClassTableOffsetNA = 0x000071E4;
    private static final int NUMBER_OF_CLASSES = 0x73;
    private static final int OFFSET_TO_CLASS_GROWTHS = 0x54;

    // Returns a 2 element int array, containing the boost type and boot amount
    public static int[] readGrowthIncrease(String rootDirectory) throws IOException {
        RandomAccessFile systemCmpButCooler = new RandomAccessFile(rootDirectory + File.separator + "system.cmp", "r");
        systemCmpButCooler.seek(SVar.region == 0x10 ? freeRealEstateOffsetNA -3 : freeRealEstateOffsetPAL -3);
        int[] i = new int[2];
        i[0] = systemCmpButCooler.read();
        i[1] = systemCmpButCooler.read();
        systemCmpButCooler.close();
        return i;
    }

    public static void writeGrowthIncrease(String rootDirectory, int boostType, int growthIncrease) throws IOException {
        RandomAccessFile systemCmpButCooler = new RandomAccessFile(rootDirectory + File.separator + "system.cmp", "rw");
        systemCmpButCooler.seek(SVar.region == 0x10 ? freeRealEstateOffsetNA -3 : freeRealEstateOffsetPAL -3);
        systemCmpButCooler.writeByte(boostType);
        systemCmpButCooler.writeByte(growthIncrease);
        systemCmpButCooler.close();
    }

    public static ArrayList<ClassGrowths> readClassGrowths(String rootDirectory) throws IOException{
        RandomAccessFile systemCmpButCooler = new RandomAccessFile(rootDirectory + File.separator + "system.cmp", "r");
        ArrayList<ClassGrowths> growthsList = new ArrayList<>();
        byte [] growths = new byte[8];

        for (int i = 0; i < NUMBER_OF_CLASSES; i++){
            systemCmpButCooler.seek(SVar.region == 0x10 ?
                    ClassTableOffsetNA + OFFSET_TO_CLASS_GROWTHS + i*0x64 :
                    ClassTableOffsetPAL + OFFSET_TO_CLASS_GROWTHS+ i*0x64);
            systemCmpButCooler.read(growths);
            growthsList.add(new ClassGrowths(growths));
        }

        systemCmpButCooler.close();
        return growthsList;
    }

    public static void writeClassGrowths(String rootDirectory, ArrayList<ClassGrowths> growthsList) throws IOException {
        RandomAccessFile systemCmpButCooler = new RandomAccessFile(rootDirectory + File.separator + "system.cmp", "rw");

        for (int i = 0; i < NUMBER_OF_CLASSES; i++) {
            systemCmpButCooler.seek(SVar.region == 0x10 ?
                    ClassTableOffsetNA + OFFSET_TO_CLASS_GROWTHS + i * 0x64 :
                    ClassTableOffsetPAL + OFFSET_TO_CLASS_GROWTHS + i * 0x64);
            systemCmpButCooler.write(new byte[]{0,0,0,0,0,0,0,0} /*growthsList.get(i).getGrowths()*/);
        }

        systemCmpButCooler.close();
    }

    private static final int ASHNARDS_ARMOR = 0X1090;
    private static final int BLACKKNIGHTS_ARMOR = 0X2CA4;
    private static final byte[] ZERO_INT_BYTES = new byte[]{0,0,0,0};

    // Eliminate blessed armor
    public static void eliminateBlessedArmor(String rootDirectory) throws IOException{
        RandomAccessFile systemCmpButCooler = new RandomAccessFile(rootDirectory + File.separator + "system.cmp", "rw");

        // Ashnard
        systemCmpButCooler.seek(SVar.region == 0x10 ?
                ClassTableOffsetNA + ASHNARDS_ARMOR :
                ClassTableOffsetPAL + ASHNARDS_ARMOR);
        systemCmpButCooler.write(ZERO_INT_BYTES);

        // Black Knight
        systemCmpButCooler.seek(SVar.region == 0x10 ?
                ClassTableOffsetNA + BLACKKNIGHTS_ARMOR :
                ClassTableOffsetPAL + BLACKKNIGHTS_ARMOR);
        systemCmpButCooler.write(ZERO_INT_BYTES);

        systemCmpButCooler.close();
    }

    public static void paladins(String rootDirectory) throws IOException{
        RandomAccessFile systemCmpButCooler = new RandomAccessFile(rootDirectory + File.separator + "system.cmp", "rw");

        // Female axe paladin get swords
        systemCmpButCooler.seek(SVar.region == 0x10 ?
                ClassTableOffsetNA + 0x0C1C + 0x24:
                ClassTableOffsetPAL + 0x0C1C + 0x24);
        systemCmpButCooler.write(ByteMath.intToBytes(0x0001BC2B));

        // Female bow paladin get lance
        systemCmpButCooler.seek(SVar.region == 0x10 ?
                ClassTableOffsetNA + 0x0C80 + 0x24:
                ClassTableOffsetPAL + 0x0C80 + 0x24);
        systemCmpButCooler.write(ByteMath.intToBytes(0x0001BC0D));

        systemCmpButCooler.close();
    }

    public static void ch1ItemScriptStuff(String rootDirectory) throws IOException{
        RandomAccessFile C01 = new RandomAccessFile(rootDirectory + File.separator + "Scripts" + File.separator + "C01.cmb", "rw");
        RandomAccessFile C02 = new RandomAccessFile(rootDirectory + File.separator + "Scripts" + File.separator + "C02.cmb", "rw");
        RandomAccessFile C31 = new RandomAccessFile(rootDirectory + File.separator + "Scripts" + File.separator + "C31.cmb", "rw");

        C01.seek(0x8EA);
        byte[] b = new byte[]{0,0,0,0,0,0,0,0,0,0,0};
        C01.write(b);

        C02.seek(0x10C);
        b = new byte[]{0x4C, 0x41, 0x4E, 0x43, 0x45};
        C02.write(b);

        // Make the laguz royals not transform
        C31.seek(0x1038);
        for (int i = 0; i < 0x36; i++){
            C31.write(0);
        }
    }
}
