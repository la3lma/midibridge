#!/bin/bash

firebase --project fbmidibridge  --data '{"chan": 0, "cmd": "NOTE_ON", "note": 60, "velocity": 127 }' database:push /testchannel

firebase --project fbmidibridge  --data '{"chan": 0, "cmd": "NOTE_ON", "note": 127, "velocity": 127 }' database:push /testchannel


sleep 3

firebase --project fbmidibridge  --data '{"chan": 0, "cmd": "NOTE_OFF", "note": 60, "velocity": 127 }' database:push /testchannel

firebase --project fbmidibridge  --data '{"chan": 0, "cmd": "NOTE_OFF", "note": 127, "velocity": 127 }' database:push /testchannel
