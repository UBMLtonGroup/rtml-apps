package realtime;

public abstract class MemoryAreaWrapper {

  abstract boolean isReal();
  abstract Object getRealArea();
  
  abstract public void executeInArea( Runnable logic );

}