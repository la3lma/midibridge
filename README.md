Firebase/REST/UDP interface to MIDI
========


What it does
----
Accept incoming MIDI events either through Firebase, REST POST
or UDP packets, and send them to some MIDI destination as seen from
the server that runs the bridge.

How it's done
----




To send MIDI using curl and HTTP POST


	  curl -X POST -H "Content-Type: application/json" --data '{"chan": 0, "cmd": "NOTE_ON", "note": 127, "velocity": 127 }' http://localhost:8080/midievent

	  sleep 2

	  curl -X POST -H "Content-Type: application/json" --data '{"chan": 0, "cmd": "NOTE_OFF", "note": 127, "velocity": 127 }' http://localhost:8080/midievent


To send midi using firebase

Prerequisites:

   * Enable the firebase command line interface, so that the "firebase" command is available in your
     path.
   * Make sure that  you have access to whatever firebase you are using (in the example below it's called "fbmidibridge")
   * Make sure that the fbmidibridge service is listening on the same path in the same database as you are sending events to, in
     the example below it's "/testchannel"

Then you just use the power of the command line thingy

     firebase --project fbmidibridge  --data '{"chan": 0, "cmd": "NOTE_ON", "note": 60, "velocity": 127 }' database:push /testchannel
     firebase --project fbmidibridge  --data '{"chan": 0, "cmd": "NOTE_ON", "note": 127, "velocity": 127 }' database:push /testchannel

... you could of course also use the firebase intrefaces for javascript, android, iphone  etc.   Look at the firebase documentation for details.

To send midi using UDP, you can use a combination of netcat and echo to send MIDI messages.  The first byte transmitted is the channel (x00 means channel 1).

     # https://ccrma.stanford.edu/~craig/articles/linuxmidi/misc/essenmidi.html
     # Command	Meaning	# parameters	param 1	param 2
     # 0x80	Note-off	2	        key	velocity
     # 0x90	Note-on	        2	        key	velocity

     # Key on
     echo -n -e '\x00\x90\x44\x45' | nc -w 1 -u localhost 6565
     sleep 2

     # Key off
     echo -n -e '\x00\x80\x44\x45' | nc -w 1 -u localhost 6565



Todo
---

* Add proper documentation of how to use this, do it in this file.

* Figure out how to let j. random user post messages into firebase from
  various services using curl, and little or no authentication.  THis is
  essential in order to let loosely coupled components produce music.

* Do some dropwizification:
  * Healthchecks & metrics for MIDI, firebase etc.
  * Read configs via configfile, as you do
  * Make the midi-sending queues and the firease-reader into services with
    normal dropwizard lifecycle
