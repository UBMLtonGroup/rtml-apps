open PeriodicThread

structure Mtask = PeriodicMethaneDetectionTask
structure Wtask = PeriodicWaterDetectionTask


val periodic_gas_period = 56
val periodic_water_period = 40

val _ = spawnPeriod 
        (fn () =>  Mtask.run methaneSensor waterPumpActuator)
        periodic_gas_period

val _ = spawnPeriod 
        (fn () => Wtask.run highWaterSensor lowWaterSensor waterPumpActuator)
        periodic_water_period