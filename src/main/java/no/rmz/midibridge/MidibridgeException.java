package no.rmz.midibridge;

public final class MidibridgeException extends Exception {

    public MidibridgeException(String string) {
        super(string);
    }

    public MidibridgeException(String msg, Throwable ex) {
        super(msg,ex);
    }

    public MidibridgeException(Throwable ex) {
        super(ex);
    }
}
