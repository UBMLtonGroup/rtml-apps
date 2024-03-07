
package realtime;

public class AbsoluteTime extends HighResolutionTime {

  public AbsoluteTime( long millis, int nanos ) {
    super( millis, nanos );
  }

  public AbsoluteTime add( long millis, int nanos ) {
  
    int newNanos = (int) (( getNanoseconds() + nanos ) % 1000000L) ;
    long newMillis = getMilliseconds() + millis + ( getNanoseconds() + nanos ) / 1000000L ;
    
    return new AbsoluteTime( newMillis, newNanos );
  }
}
