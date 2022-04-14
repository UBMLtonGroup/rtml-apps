RTML=/home/jcmurphy/RTMLton/build/bin/mlton

run-stage0:
	./pacemaker-threaded @MLton rtthreads true --

dbg-stage1-taskset:
	sudo taskset -c 0 gdb --args ./pacemaker-threaded @MLton rtthreads true packingstage1 true --

dbg-stage1:
	sudo gdb --args ./pacemaker-threaded @MLton rtthreads true packingstage1 true --

run-stage1:
	sudo taskset -c 0 ./pacemaker-threaded @MLton rtthreads true packingstage1 true --

build-stage1:
	$(RTML) -debug true -keep g -default-ann 'allowFFI true' pacemaker-threaded.mlb

tests-rtmlton:
	$(RTML) tests.mlb

mlton:
	mlton -const 'Exn.keepHistory true' tests.mlb

collect:
	LOWER=40 UPPER=200 sudo ./pacemaker-threaded @MLton hpercent .7 rtthreads true max-heap 256M packingstage1 false packingstage2 false -- 2> run10-256m-pack0.txt
	LOWER=40 UPPER=200 sudo ./pacemaker-threaded @MLton hpercent .7 rtthreads true max-heap 256M packingstage1 true packingstage2 false -- 2> run10-256m-pack1.txt
	LOWER=40 UPPER=200 sudo ./pacemaker-threaded @MLton hpercent .7 rtthreads true max-heap 256M packingstage1 true packingstage2 true -- 2> run10-256m-pack2.txt

