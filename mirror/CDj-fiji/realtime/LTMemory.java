package realtime;

public class LTMemory extends ScopedMemory {

	protected long initial = 0;
	protected long maximum = 0;

	public LTMemory(long initial, long maximum) {
		this.initial = initial;
		this.maximum = maximum;
	}

	public String toString() {
		return "fake LTMemory with initial size " + initial
				+ " and maximum size " + maximum;
	}
}
