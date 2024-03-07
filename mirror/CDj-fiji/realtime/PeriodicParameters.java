package realtime;

public class PeriodicParameters extends ReleaseParameters {

	RelativeTime period = null;

	public PeriodicParameters(HighResolutionTime start, RelativeTime period,
			RelativeTime cost, RelativeTime deadline,
			AsyncEventHandler overrunHandler, AsyncEventHandler missHandler) {

		this.period = period;
	}

	public RelativeTime getPeriod() {
		return period;
	}
}
