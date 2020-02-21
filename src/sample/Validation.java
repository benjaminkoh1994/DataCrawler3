package sample;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class Validation {
    public static boolean textFieldNotEmpty(TextArea i){
        boolean r = false;
        if (i.getText() != null && !i.getText().isEmpty()){
            r = true;
        }
        return r;
    }

    public static boolean textFieldNotEmpty(TextArea i, Label l, String validationText){
        boolean r = true;
        String c = null;
        if (!textFieldNotEmpty(i)) {
            r = false;
            c = validationText;
        }
        l.setText(c);
        return r;
    }
}
