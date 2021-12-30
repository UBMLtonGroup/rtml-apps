signature ActuatorA = 
sig
    fun handler_pace_a : (lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) -> ()
    fun handler_read_sensor_a : (lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) -> ()
end