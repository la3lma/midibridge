package no.rmz.sequencer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiDevice;
import no.rmz.eventgenerators.JitterPreventionFailureException;
import no.rmz.eventgenerators.ParsedEvent;
import no.rmz.eventgenerators.FileReadingEventGenerator;
import no.rmz.eventgenerators.PingEveryHalfSecond;
import no.rmz.eventgenerators.TcpdumpEvent;
import no.rmz.scales.ChordAndScaleDatabase;
import no.rmz.scales.ScaleBean;
import no.rmz.scales.ScaleCsvReader;

/**
 * Create a very simple sequence of pings just to check that the midi signals
 * are passed to their recipient.
 */
public final class EventGenerator {

    private final static String IAC_BUS_NAME = "Bus 1";

    private final static String FILENAME = "/tmp/tcpdump.log";
    
    


    public final static void main(final String[] argv) throws SequencerException, 
            IOException, InterruptedException, JitterPreventionFailureException {
        final ChordAndScaleDatabase chordDb;
        chordDb = ScaleCsvReader.readChordAndScaleDatabaseFromResources();

        final MidiDevice midiDevice
                = IacDeviceUtilities.getMidiReceivingDevice(IAC_BUS_NAME);
        final ScaleBean scale = chordDb.getAllScales().iterator().next();
        final SoundGenerator sg = new RandomScaleToneGenerator(scale);
        final File file = new File(FILENAME);
        final EventParser tcpdumpParser;
        tcpdumpParser = (String str) ->   new TcpdumpEvent(str);
        final PlingPlongSequencer seq;
        seq = new PlingPlongSequencerBuilder()
                .setDevice(midiDevice)
                .setSoundGenerator(sg)
                .setSignalSource(new FileReadingEventGenerator(file,
                        tcpdumpParser))
                .build();
       
        seq.start();
        Thread.currentThread().join();
    }
}
