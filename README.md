Firebase/REST/UDP  to MIDI bridge
========
[![codebeat badge](https://codebeat.co/badges/4d8e97f9-6a84-4d33-a36d-c85421290429)](https://codebeat.co/projects/github-com-la3lma-midibridge-master)



What it does
----
Accept incoming MIDI events either through Firebase, REST POST
or UDP packets, and send them to some MIDI destination as seen from
the server that runs the bridge.  It's tested on a mac laptop, and nowhere else.

How it's done
----




To send MIDI using curl and HTTP POST


     curl -X POST -H "Content-Type: application/json" --data '{"chan": 0, "cmd": "NOTE_ON", "note": 127, "velocity": 127 }' http://localhost:8080/MIDIevent
     sleep 2
     curl -X POST -H "Content-Type: application/json" --data '{"chan": 0, "cmd": "NOTE_OFF", "note": 127, "velocity": 127 }' http://localhost:8080/MIDIevent


### To send MIDI using firebase

#### Prerequisites:

   * Enable the firebase command line interface, so that the "firebase" command is available in your
     path.
   * Make sure that  you have access to whatever firebase you are using (in the example below it's called "fbMIDIbridge")
   * Make sure that the fbMIDIbridge service is listening on the same path in the same database as you are sending events to, in
     the example below it's "/testchannel"

#### Doing it

Then you just use the power of the command line thingy

     firebase --project fbMIDIbridge  --data '{"chan": 0, "cmd": "NOTE_ON", "note": 60, "velocity": 127 }' database:push /testchannel
     firebase --project fbMIDIbridge  --data '{"chan": 0, "cmd": "NOTE_ON", "note": 127, "velocity": 127 }' database:push /testchannel

... you could of course also use the firebase interfaces for javascript, android, iphone  etc.   Look at the firebase documentation for details.


### To send MIDI using UDP

To send MIDI using UDP, you can use a combination of netcat and echo to send MIDI messages.  The first byte transmitted is the channel (x00 means channel 1).

     # https://ccrma.stanford.edu/~craig/articles/linuxMIDI/misc/essenMIDI.html
     # Command  Meaning # parameters    param 1 param 2
     # 0x80     Note-off        2               key     velocity
     # 0x90     Note-on         2               key     velocity

     # Key on
     echo -n -e '\x00\x90\x44\x45' | nc -w 1 -u localhost 6565
     sleep 2
     # Key off
     echo -n -e '\x00\x80\x44\x45' | nc -w 1 -u localhost 6565

The UDP option will also be a great choice if you're working on restricted devies, such as arduinos.

To send UDP encoded MIDI from arduino with wifi, this code can be used as a starting point:


     #include <ESP8266WiFi.h>
     #include <WiFiUdp.h>


     const char* ssid = "*******";
     const char* password = "*******";

     WiFiUDP Udp;


     void setup()
     {
       Serial.begin(115200);
       Serial.println();

       Serial.printf("Connecting to %s ", ssid);
       WiFi.begin(ssid, password);
       while (WiFi.status() != WL_CONNECTED)  {
         delay(500);
         Serial.print(".");
       }
       Serial.println(" connected");
       Serial.printf("Connected as  %s\n", WiFi.localIP().toString().c_str());
       pinMode(LED_BUILTIN, OUTPUT);     // Initialize the LED_BUILTIN pin as an output
     }


     // This just happened to be my address when writing this code,
     // obviously it should be something else on your network.
     const IPAddress  ip(10, 0, 0, 62);

     void loop()
     {
       // Switch the LED off
       digitalWrite(LED_BUILTIN, LOW);
       //                    Chan cmd   note  velocity
       byte keyOnBytes[] =  {0,   0x90, 0x44, 0x45};
       byte keyOffBytes[] = {0,   0x80, 0x44, 0x45};

       // Send a key down event
       Udp.beginPacket(ip, 6565);
       Udp.write(keyOnBytes, 4);
       Udp.endPacket();
       Serial.println(" key down");

       // Turn the LED off by making the voltage HIGH
       digitalWrite(LED_BUILTIN, HIGH);

       // Wait half a  second
       delay(500);

       // Send a key up event
       Udp.beginPacket(ip, 6565);
       Udp.write(keyOffBytes, 4);
       Udp.endPacket();
       Serial.println(" key up");
       digitalWrite(LED_BUILTIN, LOW);

       // Wait three seconds, and do it all again.
       delay(3000);
     }





Todo
---

* Add proper documentation of how to use this, do it in this file.

* Figure out how to let j. random user post messages into firebase from
  various services using curl, and little or no authentication.  THis is
  essential in order to let loosely coupled components produce music.

* Do some dropwizification:
  * Healthchecks & metrics for MIDI, firebase etc.
  * Read configs via configfile, as you do
  * Make the MIDI-sending queues and the firease-reader into services with
    normal dropwizard lifecycle

* Add more of the MIDI protocol to the bridge.  For now it only
  works properly for key on / key off events.  There are many more
  possible events in MIDI, and it's relatively straightforward to add more.

* Check out how to modify the sounds of loop running in a step-sequencer
  by twiddling knobs on the synth making the sounds.

* https://www.silabs.com/products/development-tools/software/usb-to-uart-bridge-vcp-drivers
