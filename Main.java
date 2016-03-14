import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Main {

	public static HashMap<String, Double> fileSizes = new HashMap<String, Double>();
	public static HashMap<Integer, ArrayList<Integer>> rank = new HashMap<Integer, ArrayList<Integer>>();
	public static HashMap<Integer, ArrayList<String>> rank1 = new HashMap<Integer, ArrayList<String>>();

	// hash map for all small file objects
	public static HashMap<String, SmallFile> smallFiles = new HashMap<String, SmallFile>();

	// hash map for large file objects
	public static HashMap<String, LargeFile> largeFiles = new HashMap<String, LargeFile>();

	// The class starts executing from here. The arguments are in the run
	// configurations.

	public static void main(String[] args) throws IOException,
			InterruptedException {
		Map<String, String> alreadyCompared = new HashMap<String, String>();

		Main m = new Main();
		CheckAndConvertFiles check = new CheckAndConvertFiles();
		FileConvertor conv = new FileConvertor();
		FileChecker fc = AudioFileChecker.empty();
		boolean status = fc.check(args);
		if (status) {
			String[] files1 = new String[100];
			String[] files2 = new String[100];
			files1 = m.getFileNames(args[0], args[1]);
			files2 = m.getFileNames(args[2], args[3]);
			SmallFile s;
			LargeFile l;
			FileData fd = new FileData();
			for (int i = 0; i < files1.length; i++) {
				String tempPath;
				tempPath = check.checkFileExtension(files1[i]);
				tempPath = conv.convert(tempPath);
				if (!fd.checkFileData(files1[i])) {
					fd.setFileData(files1[i], tempPath);
					fileSizes.put(files1[i],
							(double) new File(tempPath).length());
					if (tempPath.contains("/tmp")) // ------- FIXME (UNcomment
													// this code to delete temp
													// file in the /tmp folder)
						conv.deleteFileFromTemp(tempPath);
				}
			}
			for (int i = 0; i < files2.length; i++) {
				String tempPath;
				tempPath = check.checkFileExtension(files2[i]);
				tempPath = conv.convert(tempPath);
				if (!fd.checkFileData(files2[i])) {
					fd.setFileData(files2[i], tempPath);
					fileSizes.put(files2[i],
							(double) new File(tempPath).length());
					if (tempPath.contains("/tmp")) // ------- FIXME (UNcomment
													// this code to delete temp
													// file in the /tmp folder)
						conv.deleteFileFromTemp(tempPath);
				}
			}

			// populate all small files
			for (int i = 0; i < files1.length; i++) {
				SmallFile small = new SmallFile(files1[i],
						fd.getFileData(files1[i]), fileSizes.get(files1[i])
								.intValue());
				small.populateChunks();
				if (!smallFiles.containsKey(files1[i]))
					smallFiles.put(files1[i], small);
			}
			for (int i = 0; i < files2.length; i++) {
				SmallFile small = new SmallFile(files2[i],
						fd.getFileData(files2[i]), fileSizes.get(files2[i])
								.intValue());
				small.populateChunks();
				if (!smallFiles.containsKey(files2[i]))
					smallFiles.put(files2[i], small);
			}

			// populate all large files
			for (int i = 0; i < files1.length; i++) {
				LargeFile large = new LargeFile(files1[i],
						fd.getFileData(files1[i]), fileSizes.get(files1[i])
								.intValue());
				large.populateChunks(0);
				if (!largeFiles.containsKey(files1[i]))
					largeFiles.put(files1[i], large);
			}
			for (int i = 0; i < files2.length; i++) {
				LargeFile large = new LargeFile(files2[i],
						fd.getFileData(files2[i]), fileSizes.get(files2[i])
								.intValue());
				large.populateChunks(0);
				if (!largeFiles.containsKey(files2[i]))
					largeFiles.put(files2[i], large);
			}

			for (int i = 0; i < files1.length; i++) {
				for (int j = 0; j < files2.length; j++) {
					int size1 = fileSizes.get(files1[i]).intValue(); // (int)
																		// new
																		// File(files1[i]).length();
					int size2 = fileSizes.get(files2[j]).intValue(); // (int)
																		// new
																		// File(files2[j]).length();
					if (size1 == size2) {
						s = smallFiles.get(files1[i]); // new
														// SmallFile(files1[i],fd.getFileData(files1[i]),size1);
						SmallFile sf = smallFiles.get(files2[j]); // new
																	// SmallFile(files2[j],fd.getFileData(files2[j]),size2);
						if (alreadyCompared
								.containsKey(getShortName(s.fileName))
								&& alreadyCompared
										.get(getShortName(s.fileName))
										.contains((getShortName(sf.fileName)))) {
							continue;
						}
						// s.populateChunks();
						// sf.populateChunks();

						secCompare(s, sf);

						// System.out.println(s.getDetailedFrequencies(0));
						// System.out.println("--------");
						// System.out.println(sf.getDetailedFrequencies(0));

						rank.clear();
						rank1.clear();
						if (alreadyCompared
								.containsKey(getShortName(s.fileName))) {
							String str = alreadyCompared
									.get(getShortName(s.fileName))
									+ "-"
									+ getShortName(sf.fileName);
							alreadyCompared.put(getShortName(s.fileName), str);
						} else {
							alreadyCompared.put(getShortName(s.fileName),
									getShortName(sf.fileName));
						}
						continue;
					}
					if (size1 <= size2) {
						s = smallFiles.get(files1[i]); // new
														// SmallFile(files1[i],fd.getFileData(files1[i]),
														// size1);
						l = largeFiles.get(files2[j]); // new
														// LargeFile(files2[j],
														// fd.getFileData(files2[j]),size2);
					} else {
						s = smallFiles.get(files2[j]); // new
														// SmallFile(files2[j],fd.getFileData(files2[j]),size2);
						l = largeFiles.get(files1[i]); // new
														// LargeFile(files1[i],
														// fd.getFileData(files1[i]),size1);
					}
					if (alreadyCompared.containsKey(getShortName(s.fileName))
							&& alreadyCompared.get(getShortName(s.fileName))
									.contains((getShortName(l.fileName)))) {
						continue;
					}
					int noOfChunks = s.amountPossible;
					// s.populateChunks();
					// l.populateChunks(0);
					secCompare(s, l);

					// slower algo
					// List l1 = new
					// ArrayList(s.getDetailedFrequencies(0).keySet());
					// Collections.sort(l1);
					// System.out.println(l1);
					// System.out.println(s.getDetailedFrequencies(0));
					// Double[] d1 =
					// getArrayFromHashMap(s.getDetailedFrequencies(0));
					// System.out.println();
					// System.out.println("--------");
					// List l2 = new
					// ArrayList(l.getDetailedFrequencies(7*1024*2).keySet());
					// Collections.sort(l2);
					// System.out.println(l.getDetailedFrequencies(7*1024*2));
					// Double[] d2 =
					// getArrayFromHashMap(l.getDetailedFrequencies(585*1024*2));
					// List l2 = new
					// ArrayList(l.getDetailedFrequencies(585*1024*2).keySet());
					// System.out.println();
					// System.out.println(l2);
					// slow algo ends

					rank.clear();
					rank1.clear();
					if (alreadyCompared.containsKey(getShortName(s.fileName))) {
						String str = alreadyCompared
								.get(getShortName(s.fileName))
								+ "-"
								+ getShortName(l.fileName);
						alreadyCompared.put(getShortName(s.fileName), str);
					} else {
						alreadyCompared.put(getShortName(s.fileName),
								getShortName(l.fileName));
					}
				}
			}
			System.exit(0);
		} else {
			System.exit(10);
		}
	}

	// this method compares the hashvalues from the smaller file
	// to the hashvalues in the longer file.
	private static void secCompare(SmallFile s, SmallFile l) throws IOException {
		File file = new File("largerfile.txt");
		// if (!file.exists()) {
		// file.createNewFile();
		// }
		// FileWriter fw = new FileWriter(file.getAbsoluteFile());
		// BufferedWriter bw = new BufferedWriter(fw);
		int largeFileSize = l.dataSize;
		int smallFileSize = s.dataSize;
		float largeFileSizeInSec = largeFileSize
				/ (HelperFunctions.SAMPLING_RATE * 4);
		float smallFileSizeInSec = smallFileSize
				/ (HelperFunctions.SAMPLING_RATE * 4);
		String content;
		int start = 0;
		boolean status = false;
		int count = 1;

		// We need to change threshold for incrementing chunks also!!
		// while(!status){
		// content = "the start is->"+start;
		// bw.write(content);
		// bw.write("\n");
		for (int i = start; i + s.amountPossible <= l.amountPossible; i = i + 5) {
			content = "the compare is from Chunk-> " + i + " to->"
					+ (i + s.amountPossible);
			// bw.write(content);
			// bw.write("\n");
			status = hashCompare(i, s, l // new BufferedWriter(null)
			);

		}
		// bw.close();
		Iterator<Entry<Integer, ArrayList<Integer>>> iterator = rank.entrySet()
				.iterator();
		int key = 10;
		int temp;
		while (iterator.hasNext()) {
			Entry<Integer, ArrayList<Integer>> e = iterator.next();
			temp = e.getKey();
			if (key > temp) {
				key = temp;
			}
		}
		// System.out.println("key is "+key);
		if (key != 10 && key != -1) {
			// System.out.println("the rank is "+rank);
		}
		if (key != 10 && key != -1) {
			float time = ((rank.get(key).get(Math
					.round(rank.get(key).size() / 2))) * HelperFunctions.CHUNK_SIZE)
					/ (HelperFunctions.SAMPLING_RATE * 2);
			System.out
					.println("MATCH: " + time + " " + getShortName(l.fileName)
							+ " " + getShortName(s.fileName));
		} else {

		}
	}

	// this method compares the hashvalues from the smaller file
	// to the hashvalues in the longer file.
	private static void secCompare(SmallFile s, LargeFile l) throws IOException {
		// File file = new File("largerfile.txt");
		// if (!file.exists()) {
		// file.createNewFile();
		// }
		// FileWriter fw = new FileWriter(file.getAbsoluteFile());
		// BufferedWriter bw = new BufferedWriter(fw);
		int largeFileSize = l.dataSize;
		int smallFileSize = s.dataSize;
		// float largeFileSizeInSec = largeFileSize
		// / (HelperFunctions.SAMPLING_RATE * 4);
		// float smallFileSizeInSec = smallFileSize
		// / (HelperFunctions.SAMPLING_RATE * 4);
		String content;
		int start = 0;
		boolean status = false;
		int count = 1;

		// We need to change threshold for incrementing chunks also!!
		// while(!status){
		// content = "the start is->"+start;
		// bw.write(content);
		// bw.write("\n");
		for (int i = start; i + s.amountPossible < l.amountPossible; i = i + 5) {
			content = "the compare is from Chunk-> " + i + " to->"
					+ (i + s.amountPossible);
			// bw.write(content);
			// bw.write("\n");
			status = hashCompare(i, s, l// , new BufferedWriter(null)
			);

		}
		// bw.close();
		Iterator<Entry<Integer, ArrayList<Integer>>> iterator = rank.entrySet()
				.iterator();
		int key = 10;
		int temp;
		while (iterator.hasNext()) {
			Entry<Integer, ArrayList<Integer>> e = iterator.next();
			temp = e.getKey();
			if (key > temp) {
				key = temp;
			}
		}
		if (key != 10 && key != -1) {
			// System.out.println("the rank is "+rank);
		}
		if (key != 10 && key != -1) {
			int slowResult = -1;
			if (rank.size() == 0) {
				// System.out.println("No match");
			} else
				slowResult = slowAlgorithm(rank, s, l);
			if (slowResult>=0) {
				//int length = rank.get(key).size();
				//int mid = (int) Math.round(length / 2);
				//int Chunk = rank.get(key).get(mid);
				float time = (slowResult * HelperFunctions.CHUNK_SIZE)
						/ (HelperFunctions.SAMPLING_RATE * 2);
				System.out.println("MATCH: " + time + " "
						+ getShortName(l.fileName) + " "
						+ getShortName(s.fileName));
			}
		} else {

		}

	}

	private static boolean hashCompare(int i, SmallFile s, SmallFile l// ,
	// BufferedWriter bw
	) throws IOException {

		HashMap<Double, Integer> totalFreq = new HashMap<Double, Integer>();

		int chunknumber = i;
		int value;
		boolean status = false;
		if (chunknumber + s.amountPossible != l.amountPossible) {
			return false;
		}

		//
		for (int j = chunknumber; j < chunknumber + s.amountPossible
				&& j < l.amountPossible; j++) {
			if (!totalFreq.containsKey(l.ch[j].highFrequency)) {
				totalFreq.put(l.ch[j].highFrequency, 1);
			} else {
				value = totalFreq.get(l.ch[j].highFrequency) + 1;
				totalFreq.put(l.ch[j].highFrequency, value);

			}
		}
		Iterator<Entry<Double, Integer>> iterator = totalFreq.entrySet()
				.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			Entry<Double, Integer> key = iterator.next();
			Double left = key.getKey();
			count = count + key.getValue();
			// bw.write("The key is->" + left + " and value is->" +
			// key.getValue());
			// bw.write("\n");
		}
		// bw.write("the count is ------->" + count);
		// bw.write("\n");
		// bw.write("right channel high frequency");
		// bw.write("\n");
		/*
		 * iterator = totalFreq.entrySet().iterator(); while
		 * (iterator.hasNext()) { Entry<Double, Integer> key = iterator.next();
		 * Double right = key.getKey(); //bw.write("The key is->" + right +
		 * " and value is->" // + key.getValue()); //bw.write("\n"); }
		 */

		status = compareHash(i, s, totalFreq// , bw
		);
		return status;
	}

	private static boolean hashCompare(int i, SmallFile s, LargeFile l// ,
	// BufferedWriter bw
	) throws IOException {
		HashMap<Double, Integer> totalFreq = new HashMap<Double, Integer>();

		int chunknumber = i;
		int value;
		boolean status = false;
		if (chunknumber + s.amountPossible >= l.amountPossible - 1) {
			return false;
		}
		for (int j = chunknumber; j < chunknumber + s.amountPossible
				&& j < l.amountPossible - 1; j++) {
			if (!totalFreq.containsKey(l.ch[j].highFrequency)) {
				totalFreq.put(l.ch[j].highFrequency, 1);
			} else {
				value = totalFreq.get(l.ch[j].highFrequency) + 1;
				totalFreq.put(l.ch[j].highFrequency, value);

			}
			/*
			 * if (!totalRightFreq.containsKey(l.ch[j].highRightFrequency)) {
			 * totalRightFreq.put(l.ch[j].highRightFrequency, 1); } else { value
			 * = totalRightFreq.get(l.ch[j].highRightFrequency) + 1;
			 * totalRightFreq.put(l.ch[j].highRightFrequency, value);
			 * 
			 * }
			 */
		}

		Iterator<Entry<Double, Integer>> iterator = totalFreq.entrySet()
				.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			Entry<Double, Integer> key = iterator.next();
			Double left = key.getKey();
			count = count + key.getValue();
			// bw.write("The key is->" + left + " and value is->" +
			// key.getValue());
			// bw.write("\n");
		}
		// bw.write("the count is ------->" + count);
		// bw.write("\n");
		// bw.write("right channel high frequency");
		// bw.write("\n");
		iterator = totalFreq.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Double, Integer> key = iterator.next();
			Double right = key.getKey();
			// bw.write("The key is->" + right + " and value is->"
			// + key.getValue());
			// bw.write("\n");
		}

		status = compareHash(i, s, totalFreq// , bw
		);
		if (status) {
			// System.out.println("Chunk Number "+chunknumber);
			Iterator<Entry<Double, Integer>> iterator1 = totalFreq.entrySet()
					.iterator();
			while (iterator1.hasNext()) {
				Entry<Double, Integer> key = iterator1.next();
				Double left = key.getKey();
				// System.out.println(left+" count="+key.getValue());
			}
		}
		return status;
	}

	// this function return true or false by comparing the hashvalues for
	// both left and right of the smaller file to the hashvalues for hashvalues
	// for
	// both left and right of the larger file
	private static boolean compareHash(int chunkNumber, SmallFile s,
			HashMap<Double, Integer> totalFreqLarge// , BufferedWriter bw
	) throws IOException {

		if (compareHashSize(chunkNumber, s.totalFreq.size(),
				totalFreqLarge.size())) {
			if (compareHighestFrequencies(chunkNumber, s.totalFreq,
					totalFreqLarge, // bw,
					"MONO-MONO")) {
				// bw.write("---------Probable Match---------");
				// bw.write("\n");
				return true;
			}

		}

		return false;
	}

	private static boolean compareHighestFrequencies(int chunkNumber,
			HashMap<Double, Integer> h1, HashMap<Double, Integer> h2,
			// BufferedWriter bw,
			String reference) throws IOException {
		Iterator<Entry<Double, Integer>> iterator;
		iterator = h1.entrySet().iterator();
		Double temp = 0.0;
		int tempRank;
		int rankValue = -1;
		boolean flag = true;
		while (iterator.hasNext()) {
			Entry<Double, Integer> key = iterator.next();
			temp = key.getKey();
			int val1 = h1.get(temp);

			if (key.getValue() > 4) {
				if (!h2.containsKey(temp)) {
					flag = false;
					break;
				}
			}

			if (key.getValue() > 300) {
				// System.out.println("freq: "+key);
				int val2 = h2.get(temp);
				tempRank = getRank(val1, val2, 5, 5);
				rankValue = initializeRank(tempRank, rankValue);
				if (rankValue >= 5) {
					flag = false;
					break;
				}

			}
			if (key.getValue() > 200 && key.getValue() <= 300) {
				int val2 = h2.get(temp);
				// System.out.println("freq: "+key);
				tempRank = getRank(val1, val2, 5, 5);
				rankValue = initializeRank(tempRank, rankValue);
				if (rankValue >= 5) {
					flag = false;
					break;
				}

			}
			if (key.getValue() > 100 && key.getValue() <= 200) {
				int val2 = h2.get(temp);
				tempRank = getRank(val1, val2, 10, 5);
				rankValue = initializeRank(tempRank, rankValue);
				if (rankValue >= 5) {
					flag = false;
					break;
				}

			}
			if (key.getValue() > 20 && key.getValue() <= 100
					&& h2.containsKey(key.getKey())) {
				int val2 = h2.get(temp);
				tempRank = getRank(val1, val2, 5, 2);
				rankValue = initializeRank(tempRank, rankValue);
				if (rankValue >= 5) {
					flag = false;
					break;
				}

			}
			if (key.getValue() > 1 && key.getValue() <= 20
					&& h2.containsKey(key.getKey())) {
				// System.out.println("freq: "+key);
				int val2 = h2.get(temp);
				tempRank = getRank(val1, val2, 3, 2);
				rankValue = initializeRank(tempRank, rankValue);
				if (rankValue >= 5) {
					flag = false;
					break;
				}

			}
		}
		if (flag) {
			if (rank.containsKey(rankValue)) {
				ArrayList<Integer> a = rank.get(rankValue);
				a.add(chunkNumber);
				rank.put(rankValue, a);
			} else {
				ArrayList<Integer> a = new ArrayList<Integer>();
				a.add(chunkNumber);
				rank.put(rankValue, a);
			}
			// bw.write("the rank is->" + rankValue + " and the reference is->"
			// + reference);
			// bw.write("\n");
			if (rank1.containsKey(rankValue)) {
				ArrayList<String> a = rank1.get(rankValue);
				a.add(reference);
				rank1.put(rankValue, a);
			} else {
				ArrayList<String> a = new ArrayList<String>();
				a.add(reference);
				rank1.put(rankValue, a);
			}
		}

		return flag;
	}

	private static int getRank(int value1, int value2, int deviation,
			int increaseDeviation) {
		// System.out.println("params: "+value1+" "+value2+" "+deviation+" "+increaseDeviation);
		int i = 0;
		for (i = 0; i < 4; i++) {
			if (Math.abs(value1 - value2) <= (deviation + (i * increaseDeviation))) {
				// System.out.println(i+" "+Math.abs(value1 - value2)
				// +" "+(deviation + (i * increaseDeviation)) );
				break;
			}
		}
		// System.out.println("value1 "+value1 +" value2 "+value2
		// +" rank is "+(i+1));
		return i + 1;
	}

	public static int initializeRank(int temp, int rank) {

		if (rank < temp) {
			rank = temp;

		}
		return rank;
	}

	public static boolean compareHashSize(int ChunkNumber, int small, int large) {

		if (Math.abs(small - large) <= 4) {
			return true;
		}
		return false;

	}

	// this function extracts information like the file names from a directory
	// or just a file name from the run time argument list.
	public String[] getFileNames(String option, String path) {
		String[] files = null;
		if (option.equals("-f") || option.equals("--file")) {
			files = new String[1];
			files[0] = path;
		} else {
			try {
				String[] temp = new String[100];
				String[] lscmd = { "ls", path };
				Process p = Runtime.getRuntime().exec(lscmd);
				p.waitFor();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String line = reader.readLine();
				int i = 0;
				while (line != null) {
					String totalPath;
					if (!path.endsWith("/"))
						totalPath = path + "/" + line;
					else
						totalPath = path + line;
					temp[i] = totalPath;
					i++;
					line = reader.readLine();
				}
				files = new String[i];
				for (int j = 0; j < i; j++)
					files[j] = temp[j];
			} catch (Exception e) {
				System.out.println("the stack trace is " + e);
			}
		}
		return files;
	}

	public static String getShortName(String pathName) {
		String str = "";
		String result;
		if (pathName.contains("/tmp")) {
			result = getMP3FileName(pathName);
			return result;
		}
		for (int i = pathName.length() - 1; i > 0; i--) {
			if (pathName.charAt(i) == '/')
				break;
			str = str + pathName.charAt(i);
		}
		result = new StringBuffer(str).reverse().toString();
		return result;
	}

	public static String getMP3FileName(String pathName) {
		String str = "";
		if (pathName.contains("/tmp")) {
			for (int i = pathName.length() - 1; i > 0; i--) {
				if (pathName.charAt(i) == '/')
					break;
				str = str + pathName.charAt(i);
			}
		}
		String str1 = new StringBuffer(str).reverse().toString();
		String temp = "";
		for (int i = 0; i < str1.length(); i++) {
			temp = temp + str1.charAt(i);
			if (str1.charAt(i) == '.')
				break;
		}
		temp = temp + "mp3";
		return temp;
	}

	public static Double[] getArrayFromHashMap(HashMap<Double, Double> h) {
		Double[] array = new Double[30];
		Iterator<Entry<Double, Double>> iterator = h.entrySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Entry<Double, Double> key = iterator.next();
			Double left = key.getKey();
			//System.out.print(left + " ");
			array[i] = left;
		}
		return array;
	}

	public static int compareFrequencyLists(ArrayList<Double> small,
			ArrayList<Double> large) {
		int sum = 0;
		for (int i = 0; i < small.size(); i++) {
			Double element = small.get(i);
			int index = large.indexOf(element);
			int diff = Math.abs(i - index);
			sum = sum + diff;
		}
		return sum;
	}

	public static int slowAlgorithm(
			HashMap<Integer, ArrayList<Integer>> ranks, SmallFile sm,
			LargeFile lr) {
		// System.out.println("rank "+ranks);
		// Integer[] keys = ranks.keySet().toArray();
		ArrayList<Integer> keys = new ArrayList(ranks.keySet());
		Collections.sort(keys);
		int highestRank = keys.get(0);
		if (highestRank >= 4) {
			//System.out.println("False positive");
			return -1;
		}
		if (highestRank <= 2)
		{
			int mid = (int) Math.round(ranks.get(highestRank).size() / 2);
			int midChunk = ranks.get(highestRank).get(mid);
			return midChunk;
		}
		// highestRank = 2;
		ArrayList<Integer> chunks = ranks.get(highestRank);
		ArrayList<Double> smallDetailFreq = new ArrayList(sm
				.getDetailedFrequencies(0).keySet());
		int tempChunk = 0, tempDiff = 9999999;
		for (int i = 0; i < chunks.size(); i++) {
			int diff = compareFrequencyLists(smallDetailFreq, new ArrayList(lr
					.getDetailedFrequencies(chunks.get(i)).keySet()));
			if (diff < tempDiff) {
				tempDiff = diff;
				tempChunk = chunks.get(i);
			}
			// System.out.println("diff of chunk "+chunks.get(i)+": "+diff);

		}
		if (tempDiff < 200) {
			//System.out.println("True positive " + tempChunk + " " + tempDiff
					//+ " Offset: " + tempChunk * 1024 / 11025);
			return tempChunk;
		} else {

			//System.out.println("False positive " + tempChunk + " " + tempDiff);
			return -1;
		}
		// return true;
	}
}
