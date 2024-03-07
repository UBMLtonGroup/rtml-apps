package immortal;

public class FrameSynchronizer {

  private static Integer lock;
  
  static {
	  lock = new Integer(0);
  }
  
  private static int producedFrames = 0;
  private static int consumedFrames = 0;

  public static void produceFrame() {
	  synchronized (lock) {
	    producedFrames ++;
		lock.notify();
	  }
  }
  
  public static void consumeFrame() {
	  synchronized (lock) {
	    consumedFrames ++;
		lock.notify();
	  }
  }
  
  public static void waitForConsumer() {
	  synchronized (lock) {
	    while (producedFrames != consumedFrames) {
    	  try { lock.wait(); } catch (Exception e) {};
    	}
	  }
  }

  public static void waitForProducer() {
	  synchronized (lock) {
	    while (producedFrames == consumedFrames) {
    	  try { lock.wait(); } catch (Exception e) {};
	    }
	  }
  }

}
