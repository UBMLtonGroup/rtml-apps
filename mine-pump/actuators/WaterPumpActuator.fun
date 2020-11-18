functor WaterPumpActuator (GA : GenericActuator) =
struct

  open GA
  
  val emergencyState : bool SyncVar.mVar = SyncVar.mVarInit(false)


  val start () = 
    let
      val eState = SyncVar.mTake(emergencyState)
    in
      if eState then () else start ();
      SyncVar.mPut(emergencyState,true)
    end


  val emergencyStop performEmergencyStop = 
  let
    val eState = SyncVar.mTake emergencyState
  in
    if performEmergencyStop then stop () else () ;
    SyncVar.mPut(emergencyState,performEmergencyStop)

  end



end