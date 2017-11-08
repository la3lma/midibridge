package no.rmz.midibridge;

public final class MidibridgeException extends Exception {

    MidibridgeException(String string) {
        super(string);
    }

    MidibridgeException(String msg, Throwable ex) {
        super(msg,ex);
    }

    MidibridgeException(Throwable ex) {
        super(ex);
    }
}
