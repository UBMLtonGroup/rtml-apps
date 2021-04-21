structure Sensor =
struct

  val methaneBuffer :int option list ref = ref []
  val highWaterBuffer :int option list ref = ref []
  val lowWaterBuffer :int option list ref = ref []


  (* Read the array of  "inputs" and then return if BRICK is
    Methane/ Water / Nothing*)
  fun conductMeasurement sens = 
    case sens of 
      "METHANE" => (case !methaneBuffer of 
                    [] => NONE
                  | hd :: tl => (methaneBuffer := tl; hd))
    | "HIGHWATER" =>(case !highWaterBuffer of
                      [] => NONE
                    | hd :: tl => (highWaterBuffer := tl; hd))
    | "LOWWATER" => (case !lowWaterBuffer of 
                      [] => NONE
                    | hd :: tl => (lowWaterBuffer := tl; hd))
    | _ => (print "Invalid sensor!! \n"; NONE)

  
end