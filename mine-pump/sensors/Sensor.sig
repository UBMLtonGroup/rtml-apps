signature Sensor =
sig
  val methaneBuffer : list option int ref 
  val highWaterBuffer :list option int ref
  val lowWaterBuffer : list option int ref

  val conductMeasurement : string -> SOME int
    
end