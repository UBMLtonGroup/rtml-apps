structure ActuatorA = 
struct
    open MLton.PrimThread

    fun printit s = ()
    fun printit2 s = print (Int.toString(getMyPriority ())^"] "^s^"\n")
    fun Pace_ON_A () = printit "Pace ON A"
    fun Pace_OFF_A () = printit "Pace OFF A"
    fun gettime () = get_ticks_since_boot ()
    fun mstosec x = Time.toReal(Time.fromMilliseconds(IntInf.fromInt(x))); (* seconds *)

(*
    val Activity_A_Occurred = ref false
    val Activity_V_Occurred = ref false
    val lastAtriumActivityTime = ref 0
    val lastVentricleActivityTime = ref 0
*)
    val attActivityOccurred_lock = 1
    val venActivityOccurred_lock = 2
    val lastVActivity_lock = 3
    val lastAActivity_lock = 4

    (* rtmlton intinf buggy so avoid *)
    val reactionTime = 30.0 (* mstosec(30000) *)
    val recoveryTime = 300.0 (* mstosec(300000) *)
    val Slop = 0.8 (* mstosec(8) *)
    val PVARP = 0.270 (* mstosec(270) *)
    val MSR = 0.5 (* mstosec(500) *)
    val PaceInterval = 1.0 (* mstosec(1000) *)
    val AVI = 0.150 (* mstosec(150) *)
    val PacingLength = 0.015 (* mstosec(15)*)

    (* DDDR_Handler_Pace_A.java 
       Aperiodic. High priority.
     *)

    fun handler_pace_a (lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = 
    let
        val now = get_ticks_since_boot ();
        val interval = now - !lastAtriumActivityTime;
    in
        if Real.>=(interval, Real.-(PaceInterval,AVI)) then (
            Pace_ON_A ();
            (* rtlinux sleep is unreliable so avoid *)
            (*Posix.Process.sleep (Time.fromReal PacingLength);*)
            ssleep(0, 15000);
            Pace_OFF_A ();
            rtlock attActivityOccurred_lock;
            Activity_V_Occurred := false;
            lastAtriumActivityTime := get_ticks_since_boot (); (* rtlinux seconds *)
            rtunlock attActivityOccurred_lock;
            ()
        )
        else ()
    end


    (* DDDR_Read_Sensor_A.java
       Periodic. Interval: 1ms. Normal priority.
     *)

    fun handler_read_sensor_a (lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = 
    let
        val interval = ref 0.0
    in
        interval := Real.-(get_ticks_since_boot(), !lastVentricleActivityTime);
        (*printit ("read sensor a. interval="^Real.toString(!interval));*)

        if Real.>(!interval , PVARP) andalso Real.<( !interval , Real.-(PaceInterval,AVI)) andalso !Activity_A_Occurred = false then (
            printit "sensor A check 1";
            if Real.>=(EcgCalc.ran1 (), 0.3) then (
                printit "Intrinsic activity sensed in A";
                rtlock attActivityOccurred_lock;
                printit "Intrinsic A after lock";
                lastAtriumActivityTime := get_ticks_since_boot ();
                Activity_A_Occurred := true;
                Activity_V_Occurred := false;
                rtunlock attActivityOccurred_lock;
                printit "Intrinsic A after unlock";
                ()
            ) else ()
        ) else if Real.>=(!interval , Real.-(PaceInterval,AVI)) andalso !Activity_A_Occurred = false then (
                printit "sensor A check 2";

                rtlock attActivityOccurred_lock;
                printit "sensor A check 2 after lock";
                Activity_A_Occurred := true;
                rtunlock attActivityOccurred_lock;
                printit "sensor A check 2 after unlock";

                ()
            ) 
        else ()
    end
end