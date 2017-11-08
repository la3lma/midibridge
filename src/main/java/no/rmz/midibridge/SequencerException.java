package no.rmz.midibridge;


public final class SequencerException extends Exception {

    SequencerException(String string) {
        super(string);
    }

    SequencerException(String msg, Throwable ex) {
        super(msg,ex);
    }
}
