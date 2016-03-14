import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CheckAndConvertFiles {

	/**
	 * @param args
	 * @throws IOException 
	 */
    // Valid mp3 file content.
    private static final String MP3_FILE = "MPEG ADTS, layer III";
    // Valid mp3 audio file content.
    private static final String MP3AUDIO_FILE =
            "Audio file with ID3 version";
    // Valid wave file content.
    private static final String WAV_FILE = "WAVE audio";
    
	public String checkFileExtension(String pathName) throws IOException, InterruptedException {				
		String[] path = pathName.split("/");
		String directoryPath="";
		for (int i = 1; i< path.length - 1; i++){
			directoryPath = directoryPath+"/"+path[i]; 
		}		
		String FileName = path[path.length-1];		
		String correctPath = checkForFileFormat(directoryPath, FileName);
		return correctPath;
	}
	//This function checks for the actual file extension and file format and
	//creates a file with proper extension in the /tmp directory if the format of the file doesnt match with
	//its extension
	private static String checkForFileFormat(String directoryPath, String FileName) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		String fileCmd = "file"+" "+directoryPath+"/"+FileName;
		//System.out.println(fileCmd);
		Process p= Runtime.getRuntime().exec(fileCmd);
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String fileLine = reader.readLine();
		String fileLocation ="";
		//System.out.println(fileLine);
		if(fileLine.contains(WAV_FILE)){
			fileLocation = checkForFileExtension(FileName, "WAV", directoryPath);
			//System.out.print(" is a wav audio file");
			//System.out.println("final location is "+fileLocation);
		}else if(fileLine.contains(MP3AUDIO_FILE) || fileLine.contains(MP3_FILE)){
			fileLocation = checkForFileExtension(FileName, "MP3", directoryPath);
			//System.out.print(" is a MP3 file");
			//System.out.println("final location is "+fileLocation);
		}
		return fileLocation;
	}

	private static String checkForFileExtension(String fileName, String format, String directoryPath) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		String tmpPath = "/tmp/";
		if(format.equals("WAV")){
			if(fileName.endsWith(".wav")){
				//System.out.print(" correct extension");
			}
			else{
				//System.out.print(" wrong wav extension");
				String FileNameWithoutExt = getFileNameWithOutExtension(fileName);
				String copy = " cp"+" "+directoryPath+"/"+fileName+ " "+tmpPath+FileNameWithoutExt+".wav";
				//System.out.println(copy);
				Process p = Runtime.getRuntime().exec(copy);
				p.waitFor();
				p.destroy();
				return tmpPath+FileNameWithoutExt+".wav";
			}
		}else if(format.equals("MP3")){
			if(fileName.endsWith(".mp3")){
				//System.out.print(" correct extension");
			}
			else{
				//System.out.print(" wrong mp3 extension");
				String FileNameWithoutExt = getFileNameWithOutExtension(fileName);
				String copy = " cp"+" "+directoryPath+"/"+fileName+ " "+tmpPath+FileNameWithoutExt+".mp3";
				//System.out.println(copy);
				Process p = Runtime.getRuntime().exec(copy);
				p.waitFor();
				p.destroy();
				return tmpPath+FileNameWithoutExt+".mp3";
			}
		}
		return directoryPath+"/"+fileName;
	}

	//this function takes in a file name and returns only the filename
	//without file extension
	//eg: xyz.mp3 -> xyz
	private static String getFileNameWithOutExtension(String fileName) {
		// TODO Auto-generated method stub
		String fileNameWithoutExtension="";
		if(fileName.contains(".")){
			fileNameWithoutExtension = fileName.substring(0, fileName.length()-4);
			//System.out.println();
		}else
			fileNameWithoutExtension = fileName;
		return fileNameWithoutExtension;
	}

}
