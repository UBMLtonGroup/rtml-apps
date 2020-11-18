signature PeriodicThread =
sig
  include Thread 

  val spawnPeriod : (unit ->unit) ->float -> unit
end

signature PeriodicMethaneDetectionThread = 
sig
    include PeriodicThread 

    val new 

    val run 
end