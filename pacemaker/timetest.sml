val _ = print "time test\n";
val ms = 10;
val t = Time.toReal(Time.fromMilliseconds(IntInf.fromInt(ms)));
val _ = print (Real.toString(t)^" secs\n");
