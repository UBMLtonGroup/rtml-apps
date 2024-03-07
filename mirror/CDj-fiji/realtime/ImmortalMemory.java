package realtime;

public class ImmortalMemory extends MemoryArea {

	protected static ImmortalMemory singleton = new ImmortalMemory();

	public static ImmortalMemory instance() {
		return singleton;
	}

	public String toString() {
		return "fake immortal memory";
	}
}