Firebase Midi Bridge
========

TODO:

Make this into an all singing and dancing bridge with a web interface etc.

* Figure out how to let j. random user post messages into firebase from
  various services using curl, and little or no authentication.  THis is
  essential in order to let loosely coupled components produce music.

* Do some dropwizification:
  * Healthchecks & metrics for MIDI, firebase etc.
  * Read configs via configfile, as you do
  * Make the midi-sending queues and the firease-reader into services with
    normal dropwizard lifecycle

* Set up a Netty service that listens on some random port for UDP
  messages that are very simply formatted MIDI messages that are
  essentially forwarded as-is (after a format check) into the
  local MIDI buses.  This will make it simple to add thumbnail-wifi
  devices as input devices.
