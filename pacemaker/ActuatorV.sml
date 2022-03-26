structure ActuatorV = 
struct
    open MLton.PrimThread

    fun printit s = print (Int.toString(getMyPriority ())^"] "^s^"\n")
    fun Pace_ON_V () = printit "Pace ON V"
    fun Pace_OFF_V () = printit "Pace OFF V"
    fun gettime () = get_ticks_since_boot ()
    fun mstosec x = Time.toReal(Time.fromMilliseconds(IntInf.fromInt(x)));

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

    val reactionTime = mstosec(30000)
    val recoveryTime = mstosec(300000)
    val Slop = mstosec(8)
    val PVARP = mstosec(270)
    val MSR = mstosec(500)
    val PaceInterval = mstosec(1000)
    val AVI = mstosec(150)
    val PacingLength = mstosec(2)

    (* DDDR_Handler_Pace_V.java 
       Aperiodic. High priority.
     *)

    fun handler_pace_v (lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = 
    let
        val now = get_ticks_since_boot ();
        val interval = now - !lastVentricleActivityTime;
    in
        if interval >= (PVARP+AVI) then (
            Pace_ON_V ();
            Posix.Process.sleep (Time.fromReal PacingLength);
            Pace_OFF_V ();
            rtlock attActivityOccurred_lock;
            Activity_A_Occurred := false;
            lastVentricleActivityTime := get_ticks_since_boot ();
            rtunlock attActivityOccurred_lock;
            ()
        )
        else ()
    end


    (* DDDR_Read_Sensor_V.java
       Periodic. Interval: 1ms. Normal priority.
     *)

    fun handler_read_sensor_v (lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = 
    let
        val interval = ref 0.0
    in
        interval := Real.-(get_ticks_since_boot(), !lastAtriumActivityTime);
        (*printit ("read sensor v. interval="^Int.toString(!interval));*)

        if !interval <= AVI andalso !Activity_V_Occurred = false then (
            printit "sensor V check 1";

            if Real.>=(EcgCalc.ran1 (), 0.9) then (
                printit "Intrinsic activity sensed in V";
                rtlock attActivityOccurred_lock;
                lastVentricleActivityTime := get_ticks_since_boot ();
                Activity_V_Occurred := true;
                Activity_A_Occurred := false;
                (* mode change? *)
                rtunlock attActivityOccurred_lock;
                ()
            ) else ()
        ) else if !interval > AVI andalso !Activity_V_Occurred = false then (
                printit "sensor V check 2";
                rtlock attActivityOccurred_lock;
                Activity_V_Occurred := true;
                (* mode change? *)
                rtunlock attActivityOccurred_lock;
                ()
            )
        else ()
    end
end