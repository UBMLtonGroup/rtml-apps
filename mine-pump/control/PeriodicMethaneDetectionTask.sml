structure PeriodicMethaneDetectionTask  =
struct
  open PT

  val run msensor wpact = 
   if msensor.isCriticalMethaneLevelReached
            then wpact.emergencyStop true
            else wpact.emergencyStop false
   
end