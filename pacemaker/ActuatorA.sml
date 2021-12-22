
fun Pace_ON_A () = print "Pace ON A"
fun Pace_OFF_A () = print "Pace OFF A"

val attActivityOccurred = ref 0
val venActivityOccurred = ref 0
val lastVActivity = ref 0
val lastAActivity = ref 0

val attActivityOccurred_lock = 1
val venActivityOccurred_lock = 2
val lastVActivity_lock = 3
val lastAActivity_lock = 4

