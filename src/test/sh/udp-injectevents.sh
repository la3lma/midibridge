#!/bin/bash

# https://ccrma.stanford.edu/~craig/articles/linuxmidi/misc/essenmidi.html
# Command	Meaning	# parameters	param 1	param 2
# 0x80	Note-off	2	        key	velocity
# 0x90	Note-on	        2	        key	velocity

# Key on
echo -n -e '\x00\x90\x44\x45' | nc -w 1 -u localhost 6565
sleep 2

# Key off
echo -n -e '\x00\x80\x44\x45' | nc -w 1 -u localhost 6565
