RTML=/media/sf_cse/CSE/B/RTMLton/build/bin/mlton

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
