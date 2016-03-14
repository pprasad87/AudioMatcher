//This JAVA class has methods that take complex numbers as input and
//perform mathematical calculations on them like addition, subtraction and
//multiplication.
public class Complex {
    //The real part in the complex number
    private final double real;
    //The imaginary part in the complex number
    private final double imaginary;
    
    //Creates a new object of Complex with the given real and imaginary values
    public Complex(double re, double im){
        real = re;
        imaginary = im;
    }
    
    //Calculates the product of two complex numbers and returns an Complex
    //object
    public Complex product(Complex number){
        Complex x = this;
        double re = x.real * number.real - x.imaginary * number.imaginary;
        double im = x.real * number.imaginary + x.imaginary * number.real;
        return new Complex(re, im);
    }
    
    //Calculates the scalar product of the Complex numbers.
    public Complex product(double number){
        return new Complex(number * real, number * imaginary);
    }
    
    //Calculates the difference between two Complex numbers
    public Complex subtract(Complex number){
        Complex x = this;
        double re = x.real - number.real;
        double im = x.imaginary - number.imaginary;
        return new Complex(re, im);
    }
    
    //Calculates the sum of two complex numbers.
    public Complex add(Complex number){
        Complex x = this;
        double re = x.real + number.real;
        double im = x.imaginary + number.imaginary;
        return new Complex(re, im);
    }
    
    //Returns the real part of the Complex number.
    public double real() {
        return real;
    }
    
    //Returns the imaginary part of the Complex number.
    public double imaginary(){
        return imaginary;
    }
}