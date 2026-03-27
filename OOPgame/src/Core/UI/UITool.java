package Core.UI;

import javax.swing.*;

public class UITool {
    public static JLabel createLabel(String msg, float size){
        var lebel = new JLabel(msg);
        lebel.setFont(lebel.getFont().deriveFont(size));
        lebel.setHorizontalAlignment(JLabel.CENTER);
        lebel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        return lebel;
    }
}
