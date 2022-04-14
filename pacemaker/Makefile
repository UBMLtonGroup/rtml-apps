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
	sudo ./pacemaker-threaded @MLton lower-bound 40 upper-bound 200 hpercent $(HPERC) rtthreads true max-heap $(HEAP) packingstage1 false packingstage2 false -- 2> $(RUN)-pack0.txt
	sudo ./pacemaker-threaded @MLton lower-bound 40 upper-bound 200 hpercent $(HPERC) rtthreads true max-heap $(HEAP) packingstage1 true packingstage2 false -- 2> $(RUN)-pack1.txt
	sudo ./pacemaker-threaded @MLton lower-bound 40 upper-bound 200 hpercent $(HPERC) rtthreads true max-heap $(HEAP) packingstage1 true packingstage2 true -- 2> $(RUN)-pack2.txt

