structure PeriodicWaterLevelDetectionTask =
struct

  val run highWaterSensor lowWaterSensor waterPumpActuator  =
  let
    val highlevel = highWaterSensor.criticalWaterLevel
    val lowlevel = lowWaterSensor.criticalWaterLevel
  in
    if highlevel then waterPumpActuator.emergencyStop true
    else if lowlevel then waterPumpActuator.start () else ()  
  end

end