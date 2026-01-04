package demo.Apps;

/*
 * Utility to apply the shared application icon to any Swing window.
*/

import java.awt.*;
import javax.swing.*;

public final class IconSupport {
    private static final String ICON_PATH = "data/icon.png";

    private IconSupport() {
    }

    public static void apply(Window window) {
        if (window == null) {
            return;
        }
        ImageIcon icon = new ImageIcon(ICON_PATH);
        Image image = icon.getImage();
        if (image != null && icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            window.setIconImage(image);
        }
    }
}
