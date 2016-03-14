import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileConvertor {
	
	public static void main(String args[]) throws IOException, InterruptedException
	{
		FileConvertor f = new FileConvertor();
		f.convert(args[0]);
	}

	public String convert(String sourceFileName) throws IOException,
			InterruptedException {
		
		if(checkFileformat(sourceFileName)){
			return sourceFileName;
		}
		
		String temp = "";
		String shortName = getShortName(sourceFileName);
		for (int i = 0; i < shortName.length(); i++) {
			temp = temp + shortName.charAt(i);
			if (shortName.charAt(i) == '.')
				break;
		}
		String wavFilename = "/tmp/" + temp + "wav";

		File source_file = new File(sourceFileName);
		File tmp_mp3 = new File("/tmp/temp.mp3");
		String tempFileName = "/tmp/temp"+temp+"mp3";
		String cmd_mp3tmp = "./lame -a --resample 11025 " + sourceFileName + " " + tempFileName;
		//System.out.println("Command 1: "+cmd_mp3tmp);
		convert_to_tempmp3(cmd_mp3tmp, "");

		//File tmp_mp3 = new File("/tmp/temp.mp3");		
		//File canonical_file = new File(wavFilename);

		String cmd_decode = "./lame --decode " + tempFileName + " " + wavFilename;
		//System.out.println("Command 2: "+cmd_decode);
		decode_to_wav(cmd_decode, wavFilename);
		//System.out.println("done");
		
		// String cmd_samplerate = "./lame --resample 44100 "+source_file
				// +" "+tmp_mp3;
				
		return wavFilename;
	}
/*
	private static void convert_samplingrate(String cmd_samplerate)
			throws IOException, InterruptedException {
		Runtime r = Runtime.getRuntime();
		r.exec(cmd_samplerate);
	}*/

	private static void decode_to_wav(String cmd_decode, String wavFilename)
			throws IOException, InterruptedException {
		
		//ProcessBuilder processBuilder = new ProcessBuilder(cmd_decode);
		Process process = Runtime.getRuntime().exec(cmd_decode);
		//Process process = //processBuilder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));
		while (reader.readLine() != null)
			; // do nothing process.waitFor();
		process.waitFor();
		process.destroy();
		/*Runtime r = Runtime.getRuntime();
		r.exec(cmd_decode);
		Process p = r.exec(cmd_decode);
		p.waitFor();
		p.destroy();*/
	}

	private static void convert_to_tempmp3(String cmd_decode, String wavFilename)
			throws IOException, InterruptedException {
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(cmd_decode);
		p.waitFor();
		p.destroy();
	}

	public void deleteFileFromTemp(String wavFilename) throws IOException,
			InterruptedException {
		Runtime r = Runtime.getRuntime();
		Process p = r.exec("rm " + wavFilename);
		p.waitFor();
		p.destroy();
	}

	public static String getShortName(String pathName) {
		String str = "";
		for (int i = pathName.length() - 1; i > 0; i--) {
			if (pathName.charAt(i) == '/')
				break;
			str = str + pathName.charAt(i);
		}
		String result = new StringBuffer(str).reverse().toString();
		return result;
	}
	
	//This function takes in the file name and checks if the 
	//file is already in the required format.
	//if file already in required format then return true 
	//else return false
	public static boolean checkFileformat(String fileName) throws IOException{
		String fileCmd = "file" + " "+fileName;
		Process p = Runtime.getRuntime().exec(fileCmd);
		BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
		String line = reader.readLine();
		if(line.contains("mono") && line.contains("WAVE") && line.contains("11025")){
			return true;
		}
		return false;
	}
}
