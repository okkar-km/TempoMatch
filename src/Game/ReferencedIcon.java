package Game;

import javax.swing.*;
import java.awt.*;

public class ReferencedIcon extends ImageIcon {
    final String reference;
    public ReferencedIcon(Image image, String reference) {
        super(image);
        this.reference = reference;
    }
    public String getReference() {
        return reference;
    }
}
