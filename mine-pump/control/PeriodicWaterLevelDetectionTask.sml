structure PeriodicWaterLevelDetectionTask =
struct
  open MLton.PrimThread

  fun sleepforPeriod start finish p = 
  let
    val elapsed = finish - start 
  in 
    OS.Process.sleep (Time.fromMilliseconds(p - elapsed))
  end

  fun isCriticalWaterLevel period = 
  let 
    val start = Time.toMilliseconds(Time.now())
    val highSensorReading = Sensor.conductMeasurement "HIGHWATER"
    val lowSensorReading = Sensor.conductMeasurement "LOWWATER"
  in 
    case (highSensorReading,lowSensorReading) of
     (SOME v, _ ) => (pspawn(fn ()=> WaterPumpActuator.start (), 3);
                      (*print "High water level : Start motor .. \n";*)
                      sleepforPeriod start (Time.toMilliseconds (Time.now())) period;
                      isCriticalWaterLevel period)
    | (NONE, SOME v) => (pspawn(fn () => WaterPumpActuator.stop (), 4);
                         (*print "Low water level : Stopping motor ..\n";*)
                         sleepforPeriod start (Time.toMilliseconds (Time.now())) period;
                         isCriticalWaterLevel period)
    | _ => (sleepforPeriod start (Time.toMilliseconds (Time.now())) period;
              isCriticalWaterLevel period )
  end
                

end