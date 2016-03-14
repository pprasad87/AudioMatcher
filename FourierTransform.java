//This JAVA class performs the Fast Fourier Transform on complex numbers 
public class FourierTransform {
    
    public static Complex[] calculate_fft(Complex[] sample){
        
        int length = sample.length;
        
        if (length == 1){
            return new Complex[] {sample[0]};
        }
        
        //FIXME : required?
        if (length % 2 != 0){
            throw new RuntimeException("The length of the sample is not a" +
                    " power of 2");
        }
        
        Complex[] evennumbered_terms = new Complex[length/2];
        Complex[] oddnumbered_terms = new Complex[length/2];
        Complex[] combined_fft = new Complex[length];
        //Method to find the FFT of even terms.
        for (int i= 0; i < length/2; i++){
            evennumbered_terms[i] = sample[2 * i];
        }
        
        //Method to find the FFT of odd terms.
        for (int i=0; i < length/2; i++){
            oddnumbered_terms[i] = sample[2 * i +1];
        }
        
        Complex[] fft_eventerms = calculate_fft(evennumbered_terms);
        Complex[] fft_oddterms = calculate_fft(oddnumbered_terms);
        
        //Combine both FFTs of odd and even numbered terms.
        for (int i = 0; i<length/2; i++){
            double x = - 2 * i * Math.PI / length;
            Complex wx = new Complex(Math.cos(x), Math.sin(x));
            combined_fft[i] = fft_eventerms[i].add
                    (wx.product(fft_oddterms[i]));
            combined_fft[i + length/2] = fft_eventerms[i].subtract
                    (wx.product(fft_oddterms[i]));
        }
        
        return combined_fft;
        
    }

    //Method to display Complex numbers
    public static void display_complex_numbers(Complex[] value, String name){
        System.out.println(name);
        for (int i = 0; i < value.length; i++){
            System.out.println(value[i]);
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
    
        
        
        
    }

}