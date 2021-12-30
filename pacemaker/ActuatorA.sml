structure ActuatorA = 
struct
    open MLton.PrimThread

    fun printit s = print (Int.toString(getMyPriority ())^"] "^s^"\n")
    fun Pace_ON_A () = printit "Pace ON A"
    fun Pace_OFF_A () = printit "Pace OFF A"
    fun gettime () = get_ticks_since_boot ()

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

    val reactionTime = 30000
    val recoveryTime = 300000
    val Slop = 8
    val PVARP = 270
    val MSR = 500
    val PaceInterval = 1000
    val AVI = 150
    val PacingLength = 2

    (* DDDR_Handler_Pace_A.java 
       Aperiodic. High priority.
     *)

    fun handler_pace_a (lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) = 
    let
        val now = get_ticks_since_boot ();
        val interval = now - !lastAtriumActivityTime;
    in
        if interval >= (PaceInterval-AVI) then (
            Pace_ON_A ();
            Posix.Process.sleep (Time.fromMilliseconds PacingLength);
            Pace_OFF_A ();
            rtlock attActivityOccurred_lock;
            Activity_V_Occurred := false;
            lastAtriumActivityTime := get_ticks_since_boot ();
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
        val interval = ref 0
    in
        interval := (get_ticks_since_boot()) - !lastVentricleActivityTime;
        (*printit ("read sensor a. interval="^Int.toString(!interval));*)

        if !interval > PVARP andalso !interval < (PaceInterval-AVI) andalso !Activity_A_Occurred = false then (
            printit "sensor A check 1";
            if (EcgCalc.ran1 ()) >= 0.3 then (
                printit "Intrinsic activity sensed in A";
                rtlock attActivityOccurred_lock;
                lastAtriumActivityTime := get_ticks_since_boot ();
                Activity_A_Occurred := true;
                Activity_V_Occurred := false;
                rtunlock attActivityOccurred_lock;
                ()
            ) else ()
        ) else if !interval >= (PaceInterval-AVI) andalso !Activity_A_Occurred = false then (
                printit "sensor A check 2";

                rtlock attActivityOccurred_lock;
                Activity_A_Occurred := true;
                rtunlock attActivityOccurred_lock;
                ()
            ) 
        else ()
    end
end