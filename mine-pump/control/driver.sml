

structure Mtask = PeriodicMethaneDetectionTask
structure Wtask = PeriodicWaterLevelDetectionTask

val _ = Sensor.methaneBuffer := (NONE:: NONE:: SOME 1:: SOME 2:: NONE :: SOME 5::[])
val _ = Sensor.lowWaterBuffer := (NONE :: NONE :: NONE :: NONE ::NONE ::SOME 1:: [])
val _ = Sensor.highWaterBuffer := (NONE :: SOME 1 :: NONE :: SOME 1:: NONE :: NONE::[]) 

local
  open MLton.PrimThread
in
  val _ = pspawn(fn () => Mtask.isCriticalMethaneLevelReached 0 3 56, 2)
  val _ = pspawn(fn () => Wtask.isCriticalWaterLevel 40, 5)
end
