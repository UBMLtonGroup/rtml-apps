structure PeriodicMethaneDetectionTask  =
struct
  open MLton.PrimThread
  fun sleepforPeriod start finish p = 
  let
    val elapsed = finish - start 
  in 
    OS.Process.sleep (Time.fromMilliseconds(p - elapsed))
  end

  fun isCriticalMethaneLevelReached history criticalLevel period =
  let 
    val start = Time.toMilliseconds(Time.now())
    val m = Sensor.conductMeasurement "METHANE"
  in
    case m of
      SOME v => if  history+v > criticalLevel 
                then (pspawn (fn () => print "call water pump emergency stop\n", 3); 
                      sleepforPeriod start (Time.toMilliseconds(Time.now())) period;
                      isCriticalMethaneLevelReached 0 criticalLevel period)
                else (pspawn(fn () => print "Turn of emergency water pump\n",3);
                      sleepforPeriod start (Time.toMilliseconds(Time.now())) period;
                      isCriticalMethaneLevelReached (history+v) criticalLevel period)
    | NONE => (sleepforPeriod start (Time.toMilliseconds(Time.now())) period;
              isCriticalMethaneLevelReached history criticalLevel period)
  end
   
end