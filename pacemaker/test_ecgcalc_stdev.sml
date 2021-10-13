structure Calcs = EcgCalc
exception AssertionFailed

local
    fun assert cond msg = if cond = false then (print (msg^"\n"); raise AssertionFailed) else ()
    val printr = print o (Real.fmt (StringCvt.SCI(SOME 15)))
in

    val expect = 5.237229365663817;
    val _ = print "stdevl: ";
    val s = Calcs.stdevl ([10.0, 12.0, 23.0, 23.0, 16.0, 23.0, 21.0, 16.0], 8.0);
    val _ = printr s;
    val _ = print "\n";
    val _ = assert (Real.== (expect, s)) "stdev calc failed";
    
    val a = Array.fromList([10.0, 12.0, 23.0, 23.0, 16.0, 23.0, 21.0, 16.0])
    val s = Calcs.stdeva (a, 8.0)
    val _ = print "stdeva: "
    val _ = printr s;
    val _ = print "\n";
    val _ = assert (Real.== (expect, s)) "stdev calc failed";


end