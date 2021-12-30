signature ActuatorV = 
sig
    fun handler_pace_v : (lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) -> ()
    fun handler_read_sensor_v : (lastVentricleActivityTime, Activity_V_Occurred, lastAtriumActivityTime, Activity_A_Occurred) -> ()
end