import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;


public class FileData {

    static HashMap<String, int[] > fileData= new HashMap<String, int[]>();

    public int[] getFileData(String key){        
        return this.fileData.get(key);
    }

    public void setFileData(String key, String tempPath) throws IOException {        
        int []audio;
        File song = new File(tempPath);
        int fileSize = (int) song.length();
        audio = new int[fileSize + 1];
        FileInputStream file;
        try{
        file = new FileInputStream(song);
        }catch(Exception e){
            file = new FileInputStream(song);
        }
        boolean eof = false;
        int count = 0;        
        while (!eof) {
            int input = file.read();
            audio[count] = input;
            if (input == -1)
                eof = true;            
            count++;
            
        }
        file.close();
        this.fileData.put(key, audio);
    }
    
    public boolean checkFileData(String key){
        return this.fileData.containsKey(key);
    }

}
