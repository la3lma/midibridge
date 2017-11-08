#!/bin/bash

curl -X POST -H "Content-Type: application/json" --data '{"chan": 0, "cmd": "NOTE_ON", "note": 127, "velocity": 127 }' http://localhost:8080/midievent

sleep 2

curl -X POST -H "Content-Type: application/json" --data '{"chan": 0, "cmd": "NOTE_OFF", "note": 127, "velocity": 127 }' http://localhost:8080/midievent
