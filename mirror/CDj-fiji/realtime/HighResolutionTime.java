package realtime;

public class HighResolutionTime {

	int nanoseconds;
	long milliseconds;

	protected HighResolutionTime(long milliseconds, int nanoseconds) {
		this.nanoseconds = nanoseconds;
		this.milliseconds = milliseconds;
	}

	public int getNanoseconds() {
		return nanoseconds;
	}

	public long getMilliseconds() {
		return milliseconds;
	}
}
