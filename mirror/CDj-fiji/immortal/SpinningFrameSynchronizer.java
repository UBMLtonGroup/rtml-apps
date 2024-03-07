package immortal;

public class SpinningFrameSynchronizer {

  static int producedFrames = 0;
  static int consumedFrames = 0;

  public static void produceFrame() {
    producedFrames ++;
  }
  
  public static void consumeFrame() {
    consumedFrames ++;
  }
  
  public static void waitForConsumer() {
    while (producedFrames != consumedFrames) {
      try { Thread.sleep(5); } catch (Exception e) {};
    }
  }

  public static void waitForProducer() {
    while (producedFrames == consumedFrames) {
      try { Thread.sleep(5); } catch (Exception e) {};
    }
  }

}