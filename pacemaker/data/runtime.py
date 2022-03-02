#!/usr/bin/env python3

import sys
with open(sys.argv[1], "r") as f:
    prevts = {}
    for l in f:
        thread, timestamp, codenum = list(map(str.strip, l.split(',')))
        # process codenums in pairs
        if codenum not in prevts:
            prevts[codenum] = timestamp
        else:
            deltat = float(timestamp) - float(prevts[codenum])
            print(f"{codenum}, {deltat}")
            del prevts[codenum]
