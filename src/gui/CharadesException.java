package gui;

/**
 * It is thrown by the Controller and caught by the GUI.
 */
public class CharadesException extends Exception{
    String message;
    public CharadesException(String s) {
        message=s;
    }
    @Override
    public String toString() {
        return message;
    }
}
