// FileChecker is an interface designed to provide abstraction and data hiding.
// The FileChecker is implemented by the class AudioFileChecker
// Algebraic Specifications:

// s.check(a) = true    if a satisfies the requirements of the Syntax Checker
//            = false   if a does not satisfy the requirements of the Syntax
//			 Checker

public interface FileChecker
{
    // returns whether the given arguments is accepted as a valid
    // parameters or not
    public boolean check(String a[]);
}
