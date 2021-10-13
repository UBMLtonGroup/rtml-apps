structure Calcs = EcgCalc
exception AssertionFailed

local
    fun assert cond msg = if cond = false then (print (msg^"\n"); raise AssertionFailed) else ()
in
    val _ = print "ran1: ";
    val r1 = Calcs.ran1 ();
    val _ = print (Real.toString(r1));
    val _ = print "\n";
    val _ = assert (r1 >= 0.0 andalso r1 < 1.0) "r1 out of range";

    val _ = print "ran1: ";
    val r2 = Calcs.ran1 ();
    val _ = print (Real.toString(r2));
    val _ = print "\n";
    val _ = assert (r2 >= 0.0 andalso r2 < 1.0) "r2 out of range";
    val _ = assert (Real.!= (r1, r2)) "r1 and r2 the same (possible but improbable)";
end