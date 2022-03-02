## SML Pacemaker

Tests

```
mlton -const 'Exn.keepHistory true' tests.mlb
./tests
```

Pacemaker

```
RTMLton/build/bin/mlton -debug true -keep g -default-ann 'allowFFI true' pacemaker-threaded.mlb
./pacemaker-threaded @MLton rtthreads true --
```


## Refs

https://physionet.org/content/ecgsyn/1.0.0/

https://www-users.cs.york.ac.uk/~alcc/publications/papers/SWC12.pdf

SML FFT https://github.com/cannam/sml-fft

## RT Kernels

RTLinux

https://stackoverflow.com/questions/51669724/install-rt-linux-patch-for-ubuntu

RTEMS


