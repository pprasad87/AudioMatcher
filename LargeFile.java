import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


public class LargeFile {

    private static final int DATA_START = 44;
    String fileName;
    int fileSize;
    int dataSize;
    int amountPossible;
    int subChunkSize;
    int channels;
    int sampleRate;
    int[] audio;
    Chunk ch[];
    String check;
    //contains the highest freq for all the chunks(chunks here is equal to the
    //number of chunks in the smaller file 
    HashMap<Double, Integer> totalFreq = new HashMap<Double, Integer>();     
    
    public LargeFile(String filename, int []audio, int size) throws IOException {
        super();
        File song = new File(filename);
        this.fileName = filename;
        this.fileSize = size; //(int) song.length();        
        this.audio = audio;

        this.subChunkSize = HelperFunctions.little4(audio[16], audio[17],
                audio[18], audio[19]);
        this.dataSize = this.fileSize - DATA_START; 
        this.channels = HelperFunctions.little2(audio[22], audio[23]);
        this.sampleRate = HelperFunctions.SAMPLING_RATE; //HelperFunctions.little4(audio[24], audio[25],
                //audio[26], audio[27]);
        this.amountPossible = this.dataSize / HelperFunctions.CHUNK_SIZE;

        this.check = "left";
    }


    //this method populates all the chunks in the Larger file
    public void populateChunks(int offset){ 
        this.amountPossible = (this.dataSize)
                / HelperFunctions.CHUNK_SIZE;
        this.ch = new Chunk[this.amountPossible];
        Complex[][] resultsLeft = new Complex[amountPossible][];
        
        //THis loop calculates the FFt for all the Chunks
        for (int times = 0; times < amountPossible; times++) {
            Complex[] complexLeft = 
                    new Complex[HelperFunctions.SAMPLES_IN_CHUNK];
            
            int j = 0;
            for (int i = 0; i < HelperFunctions.CHUNK_SIZE; i = i + 2) {
                // Put time domain data into a complex number with imaginary
                // part as 0:

                int left = HelperFunctions.little2(audio[DATA_START
                        + (times * HelperFunctions.CHUNK_SIZE) + i],
                        audio[DATA_START + (times * HelperFunctions.CHUNK_SIZE)
                                + i + 1]);
                
                complexLeft[j] = new Complex(left, 0);
                
                j = j + 1;
            }
            // Perform FFT analysis on the chunk:
            resultsLeft[times] = FourierTransform.calculate_fft(complexLeft);
            
        }
        
        //This loop gets the frequency range 
        Double freq[] = new Double[HelperFunctions.SAMPLES_IN_CHUNK / 2];
        for (int k = 0; k < HelperFunctions.SAMPLES_IN_CHUNK / 2; k++)
            freq[k] = (double) 
            (k * this.sampleRate / HelperFunctions.SAMPLES_IN_CHUNK);
        
        int start = offset / HelperFunctions.CHUNK_SIZE;

        for (int i = 0; i < amountPossible ; i++) {
            //ch[i] = new Chunk(i); 
            Double templeft = 0.0;
           // Double tempRight = 0.0;
            Double leftFreq = 0.0;
            //Double rightFreq = 0.0;          
            for (int j = 0; j < resultsLeft[i].length / 2; j++) {
                if (freq[j] >= HelperFunctions.FREQUENCY_LOW_LIMIT
                        && freq[j] <= HelperFunctions.FREQUENCY_HIGH_LIMIT){
                    if(templeft < getAmplitude(resultsLeft[i][j])){
                        templeft = getAmplitude(resultsLeft[i][j]);
                        leftFreq = freq[j];
                    }
            
                }               
            }
            String content = "Smaller Chunk->"+ i+" Left freq->"+ leftFreq+ 
                   // " Right Freq->"+rightFreq+
                    " The offSet is->"+i * HelperFunctions.CHUNK_SIZE;
            putInHash(leftFreq);
            ch[i] = new Chunk(i, leftFreq, i);
        }  
        Iterator<Entry<Double, Integer>> iterator = 
                totalFreq.entrySet().iterator();
        int count =0;
        while(iterator.hasNext()){
            Entry<Double, Integer> key = iterator.next();
            Double left = key.getKey();
            count = count + key.getValue();
            //System.out.println(left+" count = "+key.getValue());
        }           
        
    }
    
    public void putInHash(Double leftFreq){
        int value;
        if(!totalFreq.containsKey(leftFreq)){
            totalFreq.put(leftFreq, 1);
        }else{
            value = totalFreq.get(leftFreq) + 1;
            totalFreq.put(leftFreq, value);
            
        }        
            
    }
    
    public Double getAmplitude(Complex c){
        Double amp = Math.sqrt( (c.real()*c.real()) + 
                (c.imaginary()*c.imaginary()) );
        return amp;
    }

    public HashMap<Double, Double> getDetailedFrequencies(int byteNo)
    {
    	Complex[] complexLeft = 
                new Complex[HelperFunctions.SAMPLES_IN_CHUNK];
    	Complex[] result = 
                new Complex[HelperFunctions.SAMPLES_IN_CHUNK];
    	int j = 0;
        for (int i = 0; i < HelperFunctions.CHUNK_SIZE; i = i + 2) {
            // Put time domain data into a complex number with imaginary
            // part as 0:

            int left = HelperFunctions.little2(this.audio[DATA_START
                    + byteNo//(chunkNo * HelperFunctions.CHUNK_SIZE) 
                    + i],
                    this.audio[DATA_START + byteNo //(chunkNo * HelperFunctions.CHUNK_SIZE)
                            + i + 1]);            
            complexLeft[j] = new Complex(left, 0);
            
            j = j + 1;
        }
        // Perform FFT analysis on the chunk:
        result = FourierTransform.calculate_fft(complexLeft);
        
        HashMap<Double, Double> detail = new HashMap<Double, Double>();
        Double freq, amp;//[] = new Double[HelperFunctions.SAMPLES_IN_CHUNK / 2];
        for (int k = 0; k < HelperFunctions.SAMPLES_IN_CHUNK / 2; k++)
        {
        	freq = (double)(k * this.sampleRate / HelperFunctions.SAMPLES_IN_CHUNK);
        	amp = getAmplitude(result[k]);
        	if (freq >= HelperFunctions.FREQUENCY_LOW_LIMIT
                    && freq <= HelperFunctions.FREQUENCY_HIGH_LIMIT)
        		detail.put(freq, amp);
        }
                
        int count = 0;
        HashMap<Double, Double> returnDetail = new HashMap<Double, Double>();
        Iterator<Entry<Double, Double>> iterator = detail.entrySet().iterator();
        while(iterator.hasNext()){
            Entry<Double, Double> key = iterator.next();
            Double left = key.getKey();
            //System.out.println(left+" count="+key.getValue());
            returnDetail.put(left,key.getValue());
            count++;
            if (count>=30)
            	break;
        }  
        return HelperFunctions.sortByComparator(returnDetail);
    }

}
