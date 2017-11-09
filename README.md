Firebase/REST/UDP interface to MIDI
========


What it does
----
Accept incoming MIDI events either through Firebase, REST POST
or UDP packets, and send them to some MIDI destination as seen from
the server that runs the bridge.

How it's done
----

(tbd, add examples)


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
