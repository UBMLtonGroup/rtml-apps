signature MethaneSensor =
sig
  include Sensor

  structure MeasurementHistory :
  sig
    val INSERT_POINT : int 
    val history : Bricks.t array
    val maxSize : int

    val newMeasurementHistory : int -> unit
    val addMeasurement : Bricks.t -> unit
    val getMethaneLevel : unit -> float

  end

  val criticalMethaneLevel :float
  val mHistory : MeasurementHistory
  val detectBrick : bool

  val newMethaneSensor

  val isCriticalMethaneLevelReached

end