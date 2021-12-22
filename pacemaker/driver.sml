structure EcgCalc = EcgCalc

fun printit s = print (s^"\n")
fun forever1 () = while true do ( printit "Ecg: create wave form"; EcgCalc.dorun (); printit "Ecg: do it again" )
fun forever2 () = while true do ( printit "Hi!" )
fun gettime () = Time.toMilliseconds (Time.now ())

local


in
   val s = gettime();
   val _ = EcgCalc.dorun ();
   val d = (gettime ()) - s;
   val _ = printit (IntInf.toString(d) ^ " ms");

   val s = gettime();
   val _ = printit "time";
   val _ = EcgCalc.dorun ();
   val d = (gettime ()) - s;
   val _ = printit (IntInf.toString(d) ^ " ms");

   val s = gettime();
   val _ = EcgCalc.dorun ();
   val d = (gettime ()) - s;
   val _ = printit (IntInf.toString(d) ^ " ms");

(*
  val _ = print (Int.toString(getMyPriority ())^"] "^Real.toString(EcgCalc.ran1 ())^"\n");
  val f = let in print (Int.toString(getMyPriority ())^"] "^Real.toString(EcgCalc.ran1 ())^"\n") end
  val _ = pspawn (fn () => print (Int.toString(getMyPriority ())^"] Called from user program!\n"), 3)
  val _ = pspawn (fn () => let in print "Hi!\n" end, 2)
  val _ = print "Hello World\n";

  val _ = pspawn (fn () => forever1 (), 4);

  val _ = forever1 ();
  val _ = Thread.run ()
  *)
  val _ = printit "Main end\n"
end
