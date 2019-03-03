package contents.io;

import contents.datastructures.DisposEntry;
import contents.datastructures.DisposFile;
import contents.gui.RootControler;
import contents.math.ByteMath;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DisposIO {

    private List<DisposFile> disposFileList;
    private String rootDirectory;

    public DisposIO(String rootDirectory) {
        this.disposFileList = new ArrayList<>();
        this.rootDirectory = rootDirectory;
    }

    // key is PID_name, value is the corresponding DisposEntry
    // Sorry for the disposNamingScheme
    public Map<String, DisposEntry> collectDisposEntries() throws IOException, InterruptedException{
        Map<String, DisposEntry> disposMap = new HashMap<>();
        BufferedReader disposTxt = new BufferedReader(new InputStreamReader(RootControler.class.getResourceAsStream("/Dispos.txt")));
        String disposTxtLine;
        DisposFile disposFile;

        while (((disposTxtLine = disposTxt.readLine())) != null){
            disposFile = new DisposFile(disposTxtLine, rootDirectory);
            disposFileList.add(disposFile);
            for (DisposEntry entry : disposFile.getEntries()){

                // check for earlier entries of this character. If they exist, make them subentries of this one
                DisposEntry prev = disposMap.get(entry.getPID_name());
                if(prev != null){
                    entry.setSubEntries(prev);
                }

                // This will *replace* entries that are duplicate keys from previous files, and that is very much intentional!
                // This way, the only editable entry for a character is the last one, which passes the variables that need to be synced to its subentries (the earlier ones)
                disposMap.put(entry.getPID_name(), entry);
            }
        }

        disposTxt.close();
        return disposMap;
    }


    public void writeDisposEntries() throws IOException{
        for (DisposFile disposFile : disposFileList){
            RandomAccessFile disposCmp = new RandomAccessFile(disposFile.getFilePath(), "rw");
            int currentEnd = disposFile.getLength();

            for(DisposEntry entry : disposFile.getEntries()){

                disposCmp.seek(currentEnd);
                disposCmp.write(entry.getJID_name().getBytes(StandardCharsets.UTF_8));
                disposCmp.write(0x00);
                entry.setClass_pointer(ByteMath.intToBytes(currentEnd - disposFile.getHeader()));
                currentEnd += entry.getJID_name().length() + 1;

                try {
                    currentEnd = writeItemToFileAndUpdatePointer(disposCmp, disposFile, entry, currentEnd, entry.getWeapon1_IID_name(), DisposEntry.class.getMethod("setWeapon1_pointer", byte[].class));
                    currentEnd = writeItemToFileAndUpdatePointer(disposCmp, disposFile, entry, currentEnd, entry.getWeapon2_IID_name(), DisposEntry.class.getMethod("setWeapon2_pointer", byte[].class));
                    currentEnd = writeItemToFileAndUpdatePointer(disposCmp, disposFile, entry, currentEnd, entry.getWeapon3_IID_name(), DisposEntry.class.getMethod("setWeapon3_pointer", byte[].class));
                    currentEnd = writeItemToFileAndUpdatePointer(disposCmp, disposFile, entry, currentEnd, entry.getWeapon4_IID_name(), DisposEntry.class.getMethod("setWeapon4_pointer", byte[].class));
                    currentEnd = writeItemToFileAndUpdatePointer(disposCmp, disposFile, entry, currentEnd, entry.getItem1_IID_name(), DisposEntry.class.getMethod("setItem1_pointer", byte[].class));
                    currentEnd = writeItemToFileAndUpdatePointer(disposCmp, disposFile, entry, currentEnd, entry.getItem2_IID_name(), DisposEntry.class.getMethod("setItem2_pointer", byte[].class));
                    currentEnd = writeItemToFileAndUpdatePointer(disposCmp, disposFile, entry, currentEnd, entry.getItem3_IID_name(), DisposEntry.class.getMethod("setItem3_pointer", byte[].class));
                    currentEnd = writeItemToFileAndUpdatePointer(disposCmp, disposFile, entry, currentEnd, entry.getItem4_IID_name(), DisposEntry.class.getMethod("setItem4_pointer", byte[].class));
                } catch (NoSuchMethodException e){
                    e.printStackTrace();
                    throw new IOException("Failed to write item to file.");
                }

                disposCmp.seek(entry.getOffset());
                disposCmp.write(entry.getData());
            }

            disposCmp.close();
        }
    }

    // Warning: This list is empty before collectDisposEntries() gets called.
    public List<DisposFile> getDisposFileList() {
        return disposFileList;
    }

    // Returns the new value of currentEnd
    private int writeItemToFileAndUpdatePointer(RandomAccessFile disposCmp, DisposFile disposFile, DisposEntry entry, int currentEnd, String IID_name, Method setItemMethod) throws IOException{

        try{
            // IID_NULL shouldn't be written, just zero out the pointer
            if (IID_name.equals("IID_NULL")){
                setItemMethod.invoke(entry, new byte[]{0,0,0,0});
                return currentEnd;
            }

            disposCmp.seek(currentEnd);
            disposCmp.write(IID_name.getBytes(StandardCharsets.UTF_8));
            disposCmp.write(0x00);

            // Yes Intellij, i know that I'm passing an array as a parameter, thanks for the warning. Now here is a warning to future me that this warning is fine.
            setItemMethod.invoke(entry, ByteMath.intToBytes(currentEnd - disposFile.getHeader()));
        } catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new IOException("Method objects y'know...");
        }

        return currentEnd + IID_name.length() + 1;
    }
}
