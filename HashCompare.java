import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;


public class HashCompare {

    HashMap<Double, Integer> smallTotalLeftFreq=new HashMap<Double, Integer>(); 
    HashMap<Double, Integer> smallTotalRightFreq=new HashMap<Double,Integer>();
    HashMap<Double, Integer> largeTotalLeftFreq=new HashMap<Double,Integer>(); 
    HashMap<Double, Integer> largeTotalRightFreq=new HashMap<Double,Integer>();
    ArrayList<Double> l = new ArrayList<Double>(); 
    HashMap<Integer, ArrayList<Double>> rank=new 
                       HashMap<Integer,ArrayList<Double>>();
    
    
    public static void main(String[] args) {
        HashCompare hc = new HashCompare();
        hc.populateHash();
        

    }

    private void populateHash() {
        
        
        
    }

}
