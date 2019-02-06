package contents.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SystemIO {

    private final static int freeRealEstateOffsetPAL = 0x001455A0;
    private final static int freeRealEstateOffsetNA = 0x0019FCB0;

    public static int readGrowthIncrease(String rootDirectory) throws IOException {
        RandomAccessFile systemCmpButCooler = new RandomAccessFile(rootDirectory + File.separator + "system.cmp", "r");
        systemCmpButCooler.seek(SVar.region == 0x10 ? freeRealEstateOffsetNA -2 : freeRealEstateOffsetPAL -2);
        int i = systemCmpButCooler.read();
        systemCmpButCooler.close();
        return i;
    }

    public static void writeGrowthIncrease(String rootDirectory, int growthIncrease) throws IOException {
        RandomAccessFile systemCmpButCooler = new RandomAccessFile(rootDirectory + File.separator + "system.cmp", "rw");
        systemCmpButCooler.seek(SVar.region == 0x10 ? freeRealEstateOffsetNA -2 : freeRealEstateOffsetPAL -2);
        systemCmpButCooler.write(growthIncrease);
        systemCmpButCooler.close();
    }
}
