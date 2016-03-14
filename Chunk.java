import java.util.HashMap;


public class Chunk {

    public int startOffset;
    public Double highFrequency;    
    public int chunkNumber;
    //HashMap<Double,Double> hashmap;
    
    
    //this is a constructure
    public Chunk(int startOffset, Double highFrequency, int chunkNumber) {
        super();
        this.startOffset = startOffset * HelperFunctions.CHUNK_SIZE;
        this.highFrequency = highFrequency;
        
        this.chunkNumber = chunkNumber;
    }
    
}
