structure WaterPumpActuator =
struct

  open MLton.PrimThread
  val emergencyState : bool ref = ref false

  fun readState () = 
    let in 
      rtlock 0;
      case !emergencyState of
        true => (rtunlock 0; true)
      | false => (rtunlock 0; false)
    end 
  
  fun writeState v = 
    let in 
      rtlock 0;
      emergencyState := v;
      rtunlock 0
    end

  fun start () = 
      if readState () then
      print "WaterPumpActuator: Cannot start motor --> methane detected\n" 
      else print "WaterPumpActuator : Starting motor\n"
    
  
  fun stop () = print "WaterPumpActuator : Stopping motor\n"


  fun emergencyStop performEmergencyStop = 
    let in 
      writeState performEmergencyStop ;
      (if performEmergencyStop 
       then (print "WaterPumpActuator : Methane detected\n"; stop ())
       else ()) 
    end 

end