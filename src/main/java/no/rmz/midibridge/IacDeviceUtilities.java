package no.rmz.midibridge;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

/**
 * Finds an IAC bus in an IAC de device, and returns an instance with an opened
 * midi device.
 */
public final class IacDeviceUtilities {

    private static MidiDevice getMatchingDevice(final String devicename, MidiDevice.Info info) throws MidibridgeException {
        try {
            final MidiDevice device = MidiSystem.getMidiDevice(info);
            if (device.getMaxReceivers() != 0) {
                final String deviceName = device.getDeviceInfo().getName();
                if (devicename.equals(deviceName)) {
                    device.open();
                    return device;
                }
            }
        } catch (MidiUnavailableException ex) {
            throw new MidibridgeException("Midi unavailable for  : " + devicename, ex);
        }
        return null;
    }

    /**
     * Get a midi device ready to receive input.
     * @param devicename Name of midi device.
     * @return A MidiDevice instance, opened, ready to receive input.
     * @throws MidibridgeException if midi device is unavailable or has no receivers.
     */
    public static MidiDevice getMidiReceivingDevice(final String devicename) throws MidibridgeException {
        final MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (final MidiDevice.Info info : infos) {
            final MidiDevice device;
            device = getMatchingDevice(devicename, info);
            if (device != null) {
                return device;
            }
        }
        throw new MidibridgeException("Couldn't find midi device : " + devicename);
    }

    /**
     * Utility class, no creator.
     */
    private IacDeviceUtilities() {
    }
}
