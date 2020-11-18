structure Sensor =
sig
  val NO_BRICK_PRESENT : int
  val WATER_SENSOR_RANGE_BEGIN : int
  val WATER_SENSOR_RANGE_END : int
  val METHANE_SENSOR_RANGE_BEGIN : int
  val METHANE_SENSOR_RANGE_END : int 
  
  val sensorID : ref int

  val new : int -> unit

  val getSensorID : unit -> int
  val setSensorID : int -> unit
  val conductMeasurement : unit -> int
  val isBrickWater : int -> bool
  val isBrickMethane : int -> bool
  val isSensorReadingEnvironment : int -> bool 
  
end =
struct

  (*Using currently imaginary lego sensors library that we can use*)
  open Sensors
  
  val NO_BRICK_PRESENT  = 120 
  val WATER_SENSOR_RANGE_BEGIN = 132
  val WATER_SENSOR_RANGE_END = 146
  val METHANE_SENSOR_RANGE_BEGIN = 147
  val METHANE_SENSOR_RANGE_END = 160

  fun new id = sensorID = ref id 

  fun getSensorID = !sensorID
  fun setSensorID id = sensorID := id

  fun conductMeasurement  =
  (*Sensors.synchronizedReadSensors*)
    Sensors.getBuffereSensor !sensorID

  fun isBrickWater color = 
   color >= WATER_SENSOR_RANGE_BEGIN andalso color <= WATER_SENSOR_RANGE_END
  
  fun isBrickMethane color = 
  color >= METHANE_SENSOR_RANGE_BEGIN andalso color <= METHANE_SENSOR_RANGE_END

  fun isSensorReadingEnvironment color = 
    let 
        val methane = not (isBrickMethane color)
        val water = not (isBrickWater color)
    in 
        methane andalso water
    end


  
end