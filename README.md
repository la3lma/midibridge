Firebase Midi Bridge
========


TODO:

Make this into an all singing and dancing bridge with a web interface etc.

* Figre out how to let j. random user post messages into firebase from
  various services using curl, and little or no authentication.  THis is
  essential in order to let loosely coupled components produce music.

* Add dropwizard to service, refactor the midi message bean to be used
  as input through POSTs.

* Set up healthchecks/metrics etc. for both MIDI and Firebase, it's all good.

* Use json file for configuration, will permit it to grow into
  high complexit without getting very messy (avoiding command line).

* Set up a Netty service that listens on some random port for UDP
  messages that are very simply formatted MIDI messages that are
  essentially forwarded as-is (after a format check) into the
  local MIDI buses.  This will make it simple to add thumbnail-wifi
  devies as input devices.
