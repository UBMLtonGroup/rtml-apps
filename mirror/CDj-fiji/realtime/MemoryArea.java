package realtime;

public class MemoryArea extends MemoryAreaWrapper {

	protected static MemoryArea fakeArea = new MemoryArea();

	boolean isReal() {
		return false;
	}

	Object getRealArea() {
		return null;
	}

	public static MemoryArea getMemoryArea(Object o) {
		return fakeArea;
	}

	public void executeInArea(Runnable logic) {
		logic.run();
	}

	public void enter(Runnable logic) {
		logic.run();
	}

	public long memoryConsumed() {
		return Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
	}

	public long memoryRemaining() {
		return Runtime.getRuntime().freeMemory();
	}

	public Object newInstance(Class type) throws IllegalAccessException,
			InstantiationException {

		return type.newInstance();
	}

	public String toString() {
		return "fake memory area";
	}

}
