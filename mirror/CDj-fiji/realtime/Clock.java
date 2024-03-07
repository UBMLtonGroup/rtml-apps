
package realtime;

public class Clock {

  static Clock singleton = new Clock();

  public static Clock getRealtimeClock() {
    return singleton;
  }

  public AbsoluteTime getTime() {
    long nanos = System.nanoTime();
    return new AbsoluteTime(  nanos / 1000000L, (int) (nanos % 1000000L) );
  }
}