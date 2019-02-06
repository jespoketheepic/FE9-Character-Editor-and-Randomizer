package contents.io;

import contents.datastructures.DisposFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.*;

public class Compressor {

    private static void LZ77(String mode, Collection<String> filePaths) throws IOException, InterruptedException {
        if(!mode.equals("c") && !mode.equals("d")) {
            throw new IllegalArgumentException("Invalid compression mode.");
        }

        // Check if CompressLZ77.exe in the temp directory, if not, put it there.
        File tempCompressorExe = new File(System.getProperty("java.io.tmpdir") + File.separator + "CompressLZ77.exe");

        // SYSTEM.OUT DEBUGGING for the jlink release
        System.out.println(tempCompressorExe.getPath());

        if(!tempCompressorExe.isFile()){
            Files.copy(Compressor.class.getResourceAsStream("/CompressLZ77.exe"), tempCompressorExe.toPath());
            tempCompressorExe.deleteOnExit();
        }

        for(String path : filePaths){
            // Try with the temp
            Process compressor;
            try {
                compressor = new ProcessBuilder(tempCompressorExe.getPath(), path, mode).start();
            } catch (IOException e){
                // If that didn't work, try if it is in the directory with the .jar file
                try {
                    File jarFile = new File(System.getProperty("java.class.path"));
                    compressor = new ProcessBuilder(jarFile.getParent() + File.separator + "CompressLZ77.exe", path, mode).start();
                } catch (IOException f){
                    e.printStackTrace();
                    f.printStackTrace();
                    throw new RuntimeException("In Compressor");
                }
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(compressor.getInputStream()));
            String line;
            while((line=br.readLine())!=null){
                System.out.println(line);
            }
            compressor.waitFor();
            System.out.println(compressor.exitValue());
            br.close();
        }
    }

    public static void compressLZ77(Collection<String> filePaths) throws IOException, InterruptedException{
        LZ77("c", filePaths);
    }

    public static void compressLZ77_disposFiles(Collection<DisposFile> files) throws IOException, InterruptedException{
        List<String> filePaths = new ArrayList<>();
        for(DisposFile disposFile : files){
            filePaths.add(disposFile.getFilePath());
        }

        LZ77("c", filePaths);
    }

    public static void compressLZ77(String filePath) throws IOException, InterruptedException{
        compressLZ77(Collections.singleton(filePath));
    }

    public static void decompressLZ77(Collection<String> filePaths) throws IOException, InterruptedException{
        LZ77("d", filePaths);
    }

    public static void decompressLZ77_disposFiles(Collection<DisposFile> files) throws IOException, InterruptedException{
        List<String> filePaths = new ArrayList<>();
        for(DisposFile disposFile : files){
            filePaths.add(disposFile.getFilePath());
        }

        LZ77("d", filePaths);
    }

    public static void decompressLZ77(String filePath) throws IOException, InterruptedException{
        decompressLZ77(Collections.singleton(filePath));
    }
}
