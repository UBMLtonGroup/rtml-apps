structure EcgCalc = EcgCalc

open MLton.PrimThread

fun printit s = print (Int.toString(getMyPriority ())^"] "^s^"\n")
fun gettime () = get_ticks_since_boot ()

val e = _export "client_thread2": (unit -> unit) -> unit;

val pos = ref 0;

val curposA = ref 0;
val curposV = ref 0;
val locknum = 0;
val Activity_A_Occurred = ref false;
val Activity_V_Occurred = ref false;
val lastAtriumActivityTime = ref 0;
val lastVentricleActivityTime = ref 0;





fun thread0 (ln, pos, buf) = let in
   while true do ( 
      printit "Ecg: create wave form"; 
      EcgCalc.dorun (ln, pos, buf); 
      printit "Ecg: do it again" 
   ) end

fun thread2 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = let in
   while true do (
      Posix.Process.sleep (Time.fromSeconds 1);
      rtlock ln;
      printit ("Hi: "^Int.toString(!pos));
      rtunlock ln
   ) end



   val _ = e (fn () => (
               thread2 (locknum, pos, 1, lastVentricleActivityTime, 
                        Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) 
               ; ()))

val buffer = Array.array (128000, 0.0);


fun thread3 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = let in
   while true do (
      Posix.Process.sleep (Time.fromSeconds 1);
      rtlock ln;
      printit ("Hi: "^Int.toString(!pos));
      rtunlock ln
   ) end


fun thread4 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = let in
   while true do (
      Posix.Process.sleep (Time.fromSeconds 1);
      rtlock ln;
      printit ("Hi: "^Int.toString(!pos));
      rtunlock ln
   ) end


fun thread5 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = let in
   while true do (
      Posix.Process.sleep (Time.fromSeconds 1);
      rtlock ln;
      printit ("Hi: "^Int.toString(!pos));
      rtunlock ln
   ) end





   val _ = printit "create thread 2: read actuator a\n";


(*
   val _ = e (fn () => (
               thread2 (locknum, pos, buffer, lastVentricleActivityTime, 
                        Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) 
               ; ()))
  *)
  
   (*
   val _ = pspawn (
      fn () => let in 
               thread2 (locknum, pos, buffer, lastVentricleActivityTime, 
                        Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) 
               end, 2)
*)
   val _ = printit "create thread 3: read actuator v";
   val _ = printit "create thread 4: pace a";
   val _ = printit "create thread 5: pace v";

   val _ = printit "thread 0: Main loop (waveform generator)"
   val _ = thread0 (locknum, pos, buffer);
   val _ = printit "Main end (should not happen)"
