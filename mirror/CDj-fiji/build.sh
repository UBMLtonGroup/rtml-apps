#!/bin/bash
echo "Cleaning class files..."
find . -name "*.class" | xargs rm -f
echo "Compiling with javac..."
javac heap/Main.java -classpath "./:../fivm/runtimej/build:../fivm/lib/fijicore.jar:../fivm/lib/fivmcommon.jar:../fivm/lib/fijirt.jar:../fivm/lib/fivmr.jar"
echo "Creating jar package"
find . -name "*.class" | xargs jar cf cdj.jar

echo "Compiling cdj_cmr"
../fivm_sparc/bin/fivmc -o cdj_cmr cdj.jar --main heap/Main --target=sparc-rtems4.9 --reflect cdj.reflectlog --g-def-max-mem=50M --internal-inst instrumentation.h -G cmr --pollcheck-mode portable --g-def-trigger=30M --more-opt

echo "Compiling cdj_hf_c"
../fivm_sparc/bin/fivmc -o cdj_hf_c cdj.jar --main heap/Main --target=sparc-rtems4.9 --reflect cdj.reflectlog --g-def-max-mem=50M --internal-inst instrumentation.h -G hf --pollcheck-mode portable --g-def-trigger=30M --more-opt --g-pred-level=c

echo "Compiling cdj_hf_a"
../fivm_sparc/bin/fivmc -o cdj_hf_a cdj.jar --main heap/Main --target=sparc-rtems4.9 --reflect cdj.reflectlog --g-def-max-mem=50M --internal-inst instrumentation.h -G hf --pollcheck-mode portable --g-def-trigger=30M --more-opt --g-pred-level=a

echo "Compiling cdj_hf_cw"
../fivm_sparc/bin/fivmc -o cdj_hf_cw cdj.jar --main heap/Main --target=sparc-rtems4.9 --reflect cdj.reflectlog --g-def-max-mem=50M --internal-inst instrumentation.h -G hf --pollcheck-mode portable --g-def-trigger=30M --more-opt --g-pred-level=cw

echo "All done."
