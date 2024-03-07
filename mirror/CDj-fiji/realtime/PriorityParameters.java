package realtime;

public class PriorityParameters extends SchedulingParameters {

	protected int priority = -1;

	public PriorityParameters(int priority) {
		this.priority = priority;
	}

	int getPriority() {
		return priority;
	}

}
