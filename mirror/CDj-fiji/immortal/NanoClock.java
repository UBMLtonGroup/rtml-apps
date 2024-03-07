package immortal;

import realtime.AbsoluteTime;
import realtime.Clock;

public class NanoClock {

  public static long baseMillis = -1;
  public static int baseNanos = -1;


  public static AbsoluteTime roundUp( AbsoluteTime t ) { // round up to next or second next period

     long tNanos = t.getNanoseconds();
     long tMillis = t.getMilliseconds();

     long periodMillis = Constants.DETECTOR_PERIOD;
     
     if (tNanos > 0) {
       tNanos = 0;
       tMillis ++;
     }
     
     if ( periodMillis > 0 ) {
       tMillis = ((tMillis + periodMillis -1)/periodMillis) * periodMillis;
     }       
  
     return new AbsoluteTime( tMillis, (int)tNanos );
  }
  
  public static void init() {
    if (baseMillis != -1 || baseNanos != -1) {
      throw new RuntimeException("NanoClock already initialized.");
    }
    
    AbsoluteTime rt = roundUp(Clock.getRealtimeClock().getTime());

    baseNanos = rt.getNanoseconds();
    baseMillis = rt.getMilliseconds();
  }

  public static long now() {
  
    AbsoluteTime t = Clock.getRealtimeClock().getTime();  
    
    return convert(t);
  }
  
  public static long convert( AbsoluteTime t ) {
  
    long millis = t.getMilliseconds() - baseMillis;  
    int nanos = t.getNanoseconds();
    
    return millis*1000000 + nanos - baseNanos;
  }
  
  public static int asMicros(long relativeNanos) {
	   if (relativeNanos < 0) {
	      if (relativeNanos == -1) {
	        return 0;
	      }
	    }
		  
	    long millis = baseMillis + relativeNanos / 1000000L ;
	    int nanos = baseNanos + (int) ( relativeNanos % 1000000L );
	    millis += nanos / 1000000L ;
	    nanos = nanos % 1000000;
	    return nanos/1000;
  }
  
  public static String asString( long relativeNanos ) {

    if (relativeNanos < 0) {
      if (relativeNanos == -1) {
        return "NA";
      }
    }
  
    long millis = baseMillis + relativeNanos / 1000000L ;
    int nanos = baseNanos + (int) ( relativeNanos % 1000000L );


    millis += nanos / 1000000L ;
    nanos = nanos % 1000000;  
    
    String ns = Integer.toString(nanos);
    int zeros = 6-ns.length();
    StringBuffer result = new StringBuffer(Long.toString(millis));
    
    while(zeros-- >0) {
      result = result.append("0");
    }
    
    result = result.append(ns);    
    
    return result.toString();
  }

}