package contents.datastructures;

import contents.io.Compressor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DisposFile {

    private String filePath;
    private int length;
    private int header;
    private List<DisposEntry> entries;

    /////////////////
    // Constructor //
    /////////////////

    public DisposFile(String disposTxtLine, String rootDirectory) throws IOException, InterruptedException{
        entries = new ArrayList<>();

        String[] parameters = disposTxtLine.split(",");
        if(parameters.length < 3 || parameters.length % 2 == 0){
            throw new IllegalArgumentException("Invalid Dispos.txt line");
        }
        Iterator<String> parameterIterator = Arrays.asList(parameters).iterator();

        filePath = rootDirectory + File.separator + "zmap" + File.separator + parameterIterator.next() + File.separator + "dispos.cmp";
        length = Integer.parseInt(parameterIterator.next(), 16);
        header = Integer.parseInt(parameterIterator.next(), 16);

        // Special check for Ilyana's sake
        InputStream packtest = new FileInputStream(filePath);
        if(packtest.read() != 'p' || packtest.read() != 'a' || packtest.read() != 'c' || packtest.read() != 'k'){
            packtest.close();

            // Decompress
            Compressor.decompressLZ77(filePath);
        } else {
            packtest.close();
        }

        RandomAccessFile disposCmpRandomAccessRead = new RandomAccessFile(filePath, "r");

        String PID_name_buffer;
        int offsetBuffer;

        while (parameterIterator.hasNext()) {
            PID_name_buffer = parameterIterator.next();
            offsetBuffer = Integer.parseInt(parameterIterator.next(), 16);

            entries.add(new DisposEntry(PID_name_buffer, offsetBuffer, disposCmpRandomAccessRead, header));
        }

        disposCmpRandomAccessRead.close();
    }

    /////////////
    // Getters //
    /////////////

    public String getFilePath() {
        return filePath;
    }

    public int getLength() {
        return length;
    }

    public int getHeader() {
        return header;
    }

    public List<DisposEntry> getEntries() {
        return entries;
    }
}
