package no.rmz.midibridge.service;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Synthesizer;
import no.rmz.midibridge.BufferedMidiReceiver;
import no.rmz.midibridge.IacDeviceUtilities;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.MidiDestination;

public final class MidiDeviceManager {

    public MidiDeviceManager() {
        this.devices = new HashMap<>();
    }

    public static final class Entry {

        private final String id;
        private final MidiDestination dest;
        private final MidiReceiver receiver;

        private Receiver getBuiltinSynthReceiver() throws MidibridgeException {
            try {
                final Synthesizer synth = MidiSystem.getSynthesizer();
                return synth.getReceiver();
            } catch (MidiUnavailableException e) {
                throw new MidibridgeException("Couldn't open internal synth", e);
            }
        }

        private Receiver getReceiverForNamedMidiDevice(final String midiDeviceName) throws MidibridgeException {
            final MidiDevice midiDevice;
            midiDevice = IacDeviceUtilities.getMidiReceivingDevice(midiDeviceName);
            try {
                return midiDevice.getReceiver();
            } catch (MidiUnavailableException ex) {
                throw new MidibridgeException(ex);
            }
        }

        public Entry(final MidiDestination dest) throws MidibridgeException {
            this.dest = Preconditions.checkNotNull(dest);
            this.id = dest.getId();
            final String midiDeviceName = dest.getMidiDeviceName();

            // If the midi device is called "default", then we're assuming
            // that the events should be routed to Java's MIDI system's
            // built in synthesizer.
            final Receiver midiReceiver;
            if ("default".equals(midiDeviceName)) {
                midiReceiver = getBuiltinSynthReceiver();
            } else {
                midiReceiver = getReceiverForNamedMidiDevice(midiDeviceName);
            }
            this.receiver = new BufferedMidiReceiver(midiReceiver);
        }


        public String getId() {
            return id;
        }

        public MidiDestination getDest() {
            return dest;
        }

        public MidiReceiver getReceiver() {
            return receiver;
        }
    }

    private final Map<String, Entry> devices;

    public void addDestination(final MidiDestination dest) throws MidibridgeException {
        Preconditions.checkNotNull(dest);
        final Entry entry = new Entry(dest);
        devices.put(entry.getId(), entry);
    }

    public void addAll(final List<MidiDestination> midiDestinations) throws MidibridgeException {
        Preconditions.checkNotNull(midiDestinations);
        for (MidiDestination d : midiDestinations) {
            addDestination(d);
        }
    }

    public Entry getEntryById(final String id) throws MidibridgeException {
        Preconditions.checkNotNull(id);
        final Entry result = devices.get(id);
        if (result == null) {
            throw new MidibridgeException("Unknown MIDI endpoint: " + id);
        }
        return result;
    }
}
