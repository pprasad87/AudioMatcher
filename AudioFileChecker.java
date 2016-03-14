import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

//This class is the implementation of FileChecker Interface.
public abstract class AudioFileChecker {

    public static FileChecker empty() {
        return new AudioFileCheck();
    }

    // Recipe implementation of FileChecker ADT. This class implements
    // FileChecker interface.
    private static abstract class AudioFileChecks implements FileChecker {
        public abstract boolean check(String[] a);
    }

    private static class AudioFileCheck extends AudioFileChecks {
        // Number of arguments to exist in the user given input
        private static final int NO_OF_ARGS = 4;
        // Valid mp3 file content.
        private static final String MP3_FILE = "MPEG ADTS, layer III";
        // Valid mp3 audio file content.
        private static final String MP3AUDIO_FILE =
                "Audio file with ID3 version";
        // Valid wave file content.
        private static final String WAV_FILE = "WAVE audio";
        // Valid OGG Vorbis file content.
        private static final String OGG_FILE = "Ogg data, Vorbis audio";

        // ERROR messages thrown at appropriate error scenarios.
        private static final String ERR_NOT_VALID_FORMAT = 
                "ERROR: File is not actually wav file";
        private static final String ERR_FILE_NOT_PRESENT =
                "ERROR: File not present at given path";
        private static final String ERR_NOT_DIR = 
                "ERROR: Given path is not a directory";
        private static final String ERR_DIR_FILE_NOT_VALID = 
                "ERROR: All files in directory are not valid";
        private static final String ERR_SUBDIR_PRESENT =
                "ERROR: Sub directories present within given directory";
        private static final String ERR_DIR_NOT_PRESENT = 
                "ERROR: No directory path present";
        private static final String ERR_OPTIONS_INVALID = 
                "ERROR: Invalid options given";
        private static final String ERR_NO_OF_ARGS = 
                "ERROR: Number of arguments not correct";

        @Override
        public boolean check(String[] a) {
            if (a.length == NO_OF_ARGS) {
                if (checkOptions(a[0]) && checkPathSpec(a[0], a[1])
                        && checkOptions(a[2]) && checkPathSpec(a[2], a[3]))
                    return true;
                else
                    return false;
            } else {
                // Throw error if the number of arguments entered is not 4.
                printError(ERR_NO_OF_ARGS);
                return false;
            }
        }

        // Check if the options entered is one of -f --file -d or --dir.
        // Else throw ERROR.
        private boolean checkOptions(String option) {
            if (option.equals("-f") || option.equals("--file")
                    || option.equals("-d") || option.equals("--dir"))
                return true;
            else {
                printError(ERR_OPTIONS_INVALID);
                return false;
            }
        }

        // This method calls helper functions that check if the file or the
        // directory entered is valid.
        private boolean checkPathSpec(String option, String pathName) {
            if (option.equals("-f") || option.equals("--file"))
                return checkFilePathValidity(pathName);
            else if (option.equals("-d") || option.equals("--dir"))
                return checkDirectoryPathValidity(pathName);
            else {
                printError(ERR_OPTIONS_INVALID);
                return false;
            }
        }

        // To check if the file is a valid mp3 or OGG or wave, we call a
        // helper function isValidMP3OrWAVEOrOGG which returns a boolean.
        // Based on the boolean returned this method either returns a true
        // or an ERROR message.
        private boolean checkFilePathValidity(String pathName) {
            File dir = new File(pathName);
            if (dir.exists()) {
                if (isValidMP3OrWAVEOrOGG(pathName)) {
                    return true;
                } else {
                    printError(ERR_NOT_VALID_FORMAT + " : " + pathName);
                }
            } else {
                printError(ERR_FILE_NOT_PRESENT);
            }
            return false;
        }

        // Method checks if the file is a valid mp3 or OGG or a wave file.
        // Compares the contents in the file to determine if it a valid file.
        private boolean isValidMP3OrWAVEOrOGG(String pathName) {
            try {
                String[] cmd = { "file", pathName };
                Process p = Runtime.getRuntime().exec(cmd);
                p.waitFor();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                if (line.contains(MP3_FILE) || 
                        line.contains(WAV_FILE)
                        //|| line.contains(OGG_FILE)
                        || line.contains(MP3AUDIO_FILE)
                        )
                    return true;
            } catch (Exception e) {
                System.out.println("the stack trace is " + e);
            }
            return false;
        }

        // This method checks if the entered directory entered is valid or not.
        // The conditions that are checked are: if the directory is valid, if
        // the directory is empty, there are no subdirectories in the directory
        // and that all files in the directory are valid mp3 wave or OGG.
        private boolean checkDirectoryPathValidity(String pathName) {
            File dir = new File(pathName);
            if (dir.exists()) {
                if (!checkIfDirectory(pathName)) {
                    printError(ERR_NOT_DIR);
                    return false;
                }
                if (checkEmptyDirectory(pathName)) {
                    printError(ERR_DIR_FILE_NOT_VALID);
                    return false;
                }
                if (!checkDirectoriesPresent(pathName)) {
                    if (checkAllInDirectory(pathName)) {
                        return true;
                    } else {
                        printError(ERR_DIR_FILE_NOT_VALID);
                    }
                } else {
                    printError(ERR_SUBDIR_PRESENT);
                }
            } else {
                printError(ERR_DIR_NOT_PRESENT);
            }
            return false;
        }

        // Check if the arguments are valid directory arguments.
        private boolean checkIfDirectory(String pathName) {
            try {
                String[] cmd = { "file", pathName };
                Process p = Runtime.getRuntime().exec(cmd);
                p.waitFor();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                if (line.contains("directory"))
                    return true;
            } catch (Exception e) {
                System.out.println("the stack trace is " + e);
            }
            return false;
        }

        // Checks if the given directory has subdirectories.
        private boolean checkDirectoriesPresent(String dir) {
            try {
                String[] lscmd = { "ls", "-l", dir };
                Process p = Runtime.getRuntime().exec(lscmd);
                p.waitFor();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                boolean isDir = false;
                while (line != null) {
                    if (line.charAt(0) == 'd') {
                        isDir = true;
                        return isDir;
                    }
                    line = reader.readLine();

                }
            } catch (IOException e1) {
                System.out.println("Pblm found1." + e1);
            } catch (InterruptedException e2) {
                System.out.println("Pblm found2.");
            }
            return false;
        }

        // Method checks if the directory is empty. Returns a Boolean true
        // if the directory is empty. Else it returns a false.
        private boolean checkEmptyDirectory(String pathName) {
            try {
                String[] lscmd = { "ls", "-l", pathName };
                Process p = Runtime.getRuntime().exec(lscmd);
                p.waitFor();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                if (line.equals("total 0")) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println("the stack trace is " + e);
            }
            return false;
        }

        // Checks if the files in the directory are all valid mp3 wave or ogg.
        private boolean checkAllInDirectory(String pathName) {
            try {
                String[] cmd = { "ls", pathName };
                Process p = Runtime.getRuntime().exec(cmd);
                p.waitFor();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    String totalPath;
                    if (!pathName.endsWith("/"))
                        totalPath = pathName + "/" + line;
                    else
                        totalPath = pathName + line;
                    if (!checkFilePathValidity(totalPath)) {
                        return false;
                    }
                    line = reader.readLine();
                }

            } catch (Exception e) {
                System.out.println("the stack trace is " + e);
            }
            return true;
        }

        // Prints the ERROR message appending a new line after.
        private void printError(String error) {
            System.err.println(error);
        }

    }
}