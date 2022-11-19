#!/bin/bash

# Pack maps
./q1k3-master/q1k3-master/pack_map.exe /cygdrive/C/tmp/m1.map /tmp/m1.plb
./q1k3-master/q1k3-master/pack_map.exe /cygdrive/C/tmp/m2.map /tmp/m2.plb

# Concat all maps into one file
cat \
        /tmp/m1.plb \
        /tmp/m2.plb \
        > /tmp/l

cp -v /tmp/l qucore/js/target/scala-2.13/classes/build/levels

