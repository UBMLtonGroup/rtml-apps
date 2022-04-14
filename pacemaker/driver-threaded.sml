structure EcgCalc = EcgCalc


open MLton.PrimThread

fun printit s = ()
fun printit2 s = print (Int.toString(getMyPriority ())^"] "^s^"\n")
fun gettime () = get_ticks_since_boot ()

val pos = ref 0;
val curposA = ref 0;
val curposV = ref 0;
val locknum = 0;
val Activity_A_Occurred = ref false;
val Activity_V_Occurred = ref false;
val lastAtriumActivityTime = ref 0.0;
val lastVentricleActivityTime = ref 0.0;
val buffer = Array.array (128000, 0.0);

val all_stop = ref 0

(* if you are in RTEMS, these settings depend on 
 * the configured clock frequency:
 * CONFIGURE_MICROSECONDS_PER_TICK
 *)
val runtime    =  5000 (* 1 ms *)
val deadline   = 80000 (* 4 ms *)
val period     = 80000 (* 8 ms *)

val collectAtPeriodEnd = false

fun checkDeadline (cur : real, dl : int) = 
   let
      val dl_in_secs = Real./(Real.fromInt(dl), 1000.0)
   in
      if (Real.>(cur, dl_in_secs)) then
         printit "DEADLINE MISSED"
      else (
         if !all_stop = 1 then (
             OS.Process.terminate(OS.Process.success)
         ) else ()
      )
   end

(* wavegen period 13000-14000 ms *)

fun thread0 (ln, pos, buf) = 
let 
   val prev = ref (gettime ())
   val cur = ref (gettime ())
   val rc = ref 0
in
   while true do ( 
      printit "Ecg: create wave form"; 
      instrument 1;
      cur := gettime();
      EcgCalc.dorun (ln, pos, buf); 
      instrument 1;
      prev := gettime();
      printit ("Ecg: runtime "^Real.toString(Real.-(!prev, !cur)));
      schedule_yield false;
      printit "Ecg: do it again"; 
      rc := !rc + 1;
      if (!rc > 5) then (
         dump_instrument_stderr 0;
         dump_instrument_stderr 1;
         dump_instrument_stderr 2;
         dump_instrument_stderr 3;
         dump_instrument_stderr 4;
         dump_instrument_stderr 5;
         dump_instrument_counter_stderr 0;
         dump_instrument_counter_stderr 1;
         dump_instrument_counter_stderr 2;
         dump_instrument_counter_stderr 3;
         dump_instrument_counter_stderr 4;
         dump_instrument_counter_stderr 5;
         all_stop := 1;
         OS.Process.terminate(OS.Process.success)
      ) else ();
      ()
   )
end

fun thread2 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) =
let
   val prev = ref (gettime ())
   val cur = ref (gettime ())
   (* reminder: sched_runtime <= sched_deadline <= sched_period  *)
   val _ = set_schedule (runtime, deadline, period, 2) (* runtime, deadline, period, allowedtopacks *)
in
   while true do (
      (* rtlock ln; *)
      instrument 2;
      cur := gettime();
      ActuatorA.handler_read_sensor_a (lastVentricleActivityTime, 
                     Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred);
      prev := gettime();
      instrument 2;
      (* rtunlock ln; *)
      printit ("handler_read_sensor_a: runtime "^Real.toString(Real.-(!prev, !cur)));
      checkDeadline (Real.-(!cur, !prev), deadline);
      wait_for_next_period collectAtPeriodEnd (* after computation finishes, this must be called *)
   )
end


fun thread3 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = 
let
   val prev = ref (gettime ())
   val cur = ref (gettime ())
   
   val _ = set_schedule (runtime, deadline, period, 2) (* runtime, deadline, period, allowedtopack *)
in
   while true do (
      (* rtlock ln; *)
      cur := gettime();
      instrument 3;
      ActuatorV.handler_read_sensor_v (lastVentricleActivityTime, 
               Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred);
      prev := gettime();
      instrument 3;
      (* rtunlock ln; *)
      printit ("handler_read_sensor_v: runtime "^Real.toString(Real.-(!prev, !cur)));
      checkDeadline (Real.-(!cur, !prev), deadline);
      wait_for_next_period collectAtPeriodEnd (* after computation finishes, this must be called *)
   ) 
end


fun thread4 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) =
let
   val prev = ref (gettime ())
   val cur = ref (gettime ())

   val _ = set_schedule (runtime, deadline, period, 2) (* runtime, deadline, period, allowedtopack *)
in
   while true do (
      (* rtlock ln; *)
      cur := gettime();
      instrument 4;
      ActuatorA.handler_pace_a (lastVentricleActivityTime, 
               Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred);
      prev := gettime();
      instrument 4;
      (* rtunlock ln; *)
      printit ("handler_pace_a: runtime "^Real.toString(Real.-(!prev, !cur)));
      checkDeadline (Real.-(!cur, !prev), deadline);
      wait_for_next_period collectAtPeriodEnd (* after computation finishes, this must be called *)
   )
end


fun thread5 (ln, pos, buf, lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) =
let
   val prev = ref (gettime ())
   val cur = ref (gettime ())

(*  The kernel requires that:
           sched_runtime <= sched_deadline <= sched_period *)

   val _ = set_schedule (runtime, deadline, period, 2) (* runtime, deadline, period, allowedtopack *)
in
   while true do (
      (* rtlock ln; *)
      cur := gettime();
      instrument 5;
      ActuatorV.handler_pace_v (lastVentricleActivityTime, 
               Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred);
      prev := gettime();
      instrument 5;
      (* rtunlock ln; *)
      printit ("handler_pace_v: runtime "^Real.toString(Real.-(!prev, !cur)));
      checkDeadline (Real.-(!cur, !prev), deadline);
      wait_for_next_period collectAtPeriodEnd (* after computation finishes, this must be called *)
   )
end





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
