import java.awt.Color;
import java.util.*;

public class ColorArray {

    public static List<Color> colorArray =
        new ArrayList<Color>();

    public static void init() {
        for (int i = 0, r = 0; i < 2; i++, r += 255) {

          for (int j = 0, g = 0; j < 2; j++, g += 255) {

            for (int k = 0, b = 0; k < 2; k++, b += 255) {

              colorArray.add(new Color(r, g, b));
            }
          }
        }
    }

}