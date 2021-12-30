structure EcgCalc = EcgCalc


open MLton.PrimThread

fun printit s = print (Int.toString(getMyPriority ())^"] "^s^"\n")
fun gettime () = get_ticks_since_boot ()

val pos = ref 0;
val curposA = ref 0;
val curposV = ref 0;
val locknum = 0;
val Activity_A_Occurred = ref false;
val Activity_V_Occurred = ref false;
val lastAtriumActivityTime = ref 0;
val lastVentricleActivityTime = ref 0;
val buffer = Array.array (128000, 0.0);




fun thread0 (ln, pos, buf) = let in
   while true do ( 
      printit "Ecg: create wave form"; 
      EcgCalc.dorun (ln, pos, buf); 
      printit "Ecg: do it again" 
   ) end

fun thread2 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = let in
   while true do (
      Posix.Process.sleep (Time.fromMilliseconds 100);
      rtlock ln;
      ActuatorA.handler_read_sensor_a (lastVentricleActivityTime, 
                     Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred);
      rtunlock ln
   ) end


fun thread3 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = let in
   while true do (
      Posix.Process.sleep (Time.fromMilliseconds 100);
      rtlock ln;
      ActuatorV.handler_read_sensor_v (lastVentricleActivityTime, 
               Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred);
      rtunlock ln
   ) end


fun thread4 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = let in
   while true do (
      Posix.Process.sleep (Time.fromSeconds 1);
      rtlock ln;
      ActuatorA.handler_pace_a (lastVentricleActivityTime, 
               Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred);
      rtunlock ln
   ) end


fun thread5 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = let in
   while true do (
      Posix.Process.sleep (Time.fromSeconds 1);
      rtlock ln;
      ActuatorV.handler_pace_v (lastVentricleActivityTime, 
               Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred);
      rtunlock ln
   ) end





val _ = printit "create thread 2: read sensor a";
(*
val _ = ActuatorA.handler_read_sensor_a (lastVentricleActivityTime, 
                     Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred)
*)

val _ = pspawn (
   fn () => let in 
            thread2 (locknum, pos, buffer, lastVentricleActivityTime, 
                     Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) 
            end, 2)

val _ = printit "create thread 3: read sensor v";

val _ = pspawn (
   fn () => let in 
            thread3 (locknum, pos, buffer, lastVentricleActivityTime, 
                     Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) 
            end, 3)

val _ = printit "create thread 4: pace a";

val _ = pspawn (
   fn () => let in 
            thread4 (locknum, pos, buffer, lastVentricleActivityTime, 
                     Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) 
            end, 4)

val _ = printit "create thread 5: pace v";

val _ = pspawn (
   fn () => let in 
            thread5 (locknum, pos, buffer, lastVentricleActivityTime, 
                     Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) 
            end, 5)


val _ = printit "thread 0: Main loop (waveform generator)"
val _ = thread0 (locknum, pos, buffer);
val _ = printit "Main end (should not happen)"
