structure Calcs = EcgCalc
exception AssertionFailed

local
    fun assert cond msg = if cond = false then (print (msg^"\n"); raise AssertionFailed) else ()
in
    val _ = Calcs.dorun ();
end