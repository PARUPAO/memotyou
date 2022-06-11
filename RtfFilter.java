import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class RtfFilter extends FileFilter {
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf(".");
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }

        if (ext.equals("rtf") || ext.equals("txt")) {
            return true;
        } else {
            return false;
        }
    }

    public String getDescription() {
        return "RTF Only";
    }
}