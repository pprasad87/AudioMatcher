import java.io.BufferedWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HelperFunctions {

    public static final int CHUNK_SIZE = 4096/2;//*4;
    public static final int SAMPLES_IN_CHUNK = CHUNK_SIZE/2;/// 4;
    public static final Double FREQUENCY_LOW_LIMIT = 20.0;
    public static final Double FREQUENCY_HIGH_LIMIT = 22050.0;
    public static final int SAMPLING_RATE = 11025; //= 44100;
    public static final int HASH_SIZE_THRESHOLD = 4; 
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    static int little4(int i, int j, int k, int l) {
        return ((l << 24) + (k << 16) + (j << 8) + i);
    }

    static int little2(int i, int j) {
        return ((j & 0xff) << 8) + i;
    }

    static boolean withInRange(int x, int y) {
        if ((x > (0.9 * y) && x < (1.1 * y))
                || (y > (0.9 * x) && y < (1.1 * x)))
            return true;
        else
            return false;
    }

    static boolean compareArray(int[] a, int[] b, int value) {
        int i;
        for (i = 0; i < 10; i++) {
            if (!((a[i] > (b[i] - value) && a[i] < (b[i] + value)) 
                    || (b[i] > (a[i] - value) && b[i] < (a[i] + value))))
                return false;
        }
        return true;
    }
    
    public static HashMap sortByComparator(Map unsortMap) {

        List list = new LinkedList(unsortMap.entrySet());

        
        // sort list based on comparator
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                                       .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
        HashMap sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
