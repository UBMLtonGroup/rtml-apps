signature Sensor =
sig
  val NO_BRICK_PRESENT : int
  val WATER_SENSOR_RANGE_BEGIN : int
  val WATER_SENSOR_RANGE_END : int
  val METHANE_SENSOR_RANGE_BEGIN : int
  val METHANE_SENSOR_RANGE_END : int 
  
  val sensorID : int

  val getSensorID : int -> int
  val setSensorID : int -> unit
  val conductMeasurement : int -> int
  val isBrickWater : int -> bool
  val isBrickMethane : int -> bool
  val isSensorReadingEnvironment : int -> bool 
  
end