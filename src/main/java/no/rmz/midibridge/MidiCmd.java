package no.rmz.midibridge;

import javax.sound.midi.ShortMessage;
/*

Command	Meaning	#               parameters	param 1         param 2
0x80	Note-off	        2               key             velocity
0x90	Note-on	                2               key             veolcity
0xA0	Aftertouch	        2               key             touch
0xB0	Continuous controller	2               controller      controller value
0xC0	Patch change	        2               instrument      #
0xD0	Channel Pressure	1               pressure
0xE0	Pitch bend	        2               lsb (7 bits)    msb (7 bits)
0xF0	(non-musical commands)
 */
public enum MidiCmd {
    NOTE_ON(ShortMessage.NOTE_ON, 2),
    NOTE_OFF(ShortMessage.NOTE_OFF, 2),
    AFTERTOUCH(ShortMessage.POLY_PRESSURE, 2),
    CONTINOUS_CONTROLLER(ShortMessage.CONTROL_CHANGE, 2),
    PATCH_CHANGE(ShortMessage.PROGRAM_CHANGE, 2),
    CHANNEL_PRESSURE(ShortMessage.CHANNEL_PRESSURE, 1),
    PITCH_BEND(ShortMessage.PITCH_BEND, 2);

    // XXX The F* SYSTEM/Non musical commands are not
    //     included yet.

    private final int cmd;
    private final int noOfArgs;

    public int getCmd() {
        return cmd;
    }

    public int getNoOfArgs() {
        return noOfArgs;
    }

    @SuppressWarnings("unused")
    MidiCmd(int cmd, int noOfArgs) {
        this.cmd = cmd;
        this.noOfArgs = noOfArgs;
    }

    public static MidiCmd findByMidiCmd(final int cmd) {
        for (final MidiCmd mc : MidiCmd.values()) {
            if (mc.getCmd() == cmd) {
                return mc;
            }
        }
        return null;
    }
}
