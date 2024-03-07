#!/bin/bash
echo "load ./o-optimize/cdc.exe
go
q" > batch.txt
tsim-leon3 < batch.txt
rm batch.txt
echo "Simulation done. Check results..."

