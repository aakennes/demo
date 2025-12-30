package demo.Apps;

/*
 * Color class includes color constants used in the GUI.
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ColorDefine {
    public static final Color BUTTON_COLOR = new Color(24, 144, 255);
    public static final Color BUTTON_TEXT_COLOR = new Color(240, 255, 254);
    public static final Color FRAME_BACK_COLOR = new Color(250, 250, 250);
    
    public static final Color BLUE_COLOR = new Color(51, 255, 255);
    public static final Color GREEN_COLOR = new Color(51, 255, 51);
    public static final Color YELLOW_COLOR = new Color(255, 255, 51);
    public static final Color RED_COLOR = new Color(255, 51, 51);
    public static final Color GREY_COLOR = new Color(192, 192, 192);
    public static final Color BLACK_COLOR = new Color(0, 0, 0);
    public static final Color PURPLE_COLOR = new Color(127, 0, 255);
    public static final Color UNDEFINED_COLOR = new Color(255, 255, 255);

    public static final int BLUE    = 0;
    public static final int GREEN   = 1;
    public static final int YELLOW  = 2;
    public static final int RED     = 3;
    public static final int GREY    = 4;
    public static final int BLACK   = 5;
    public static final int PURPLE  = 6;
    public static final int UNDEFINED  = 7;

    public static final String BLUE_STR     = "Blue";
    public static final String GREEN_STR    = "Green";
    public static final String YELLOW_STR   = "Yellow";
    public static final String RED_STR      = "Red";
    public static final String GREY_STR     = "Grey";
    public static final String BLACK_STR    = "Black";
    public static final String PURPLE_STR   = "Purple";
    public static final String UNDEFINED_STR = "Undefined";

    public static int colorStrToNo(String str){
        switch (str) {
            // {"Blue", "Green", "Yellow", "Red", "Grey", "Black", "Purple"}
            case "Blue":
                return BLUE;
            case "Green":
                return GREEN;
            case "Yellow":
                return YELLOW;
            case "Red":
                return RED;
            case "Grey":
                return GREY;
            case "Black":
                return BLACK;
            case "Purple":
                return PURPLE;
            default:
                break;
        }
        return UNDEFINED;
    }

    public static String colorNoToStr(int no_){
        switch (no_) {
            // {"Blue", "Green", "Yellow", "Red", "Grey", "Black", "Purple"}
            case 0:
                return BLUE_STR;
            case 1:
                return GREEN_STR;
            case 2:
                return YELLOW_STR;
            case 3:
                return RED_STR;
            case 4:
                return GREY_STR;
            case 5:
                return BLACK_STR;
            case 6:
                return PURPLE_STR;
            default:
                break;
        }
        return UNDEFINED_STR;
    }
}
