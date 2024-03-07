package realtime;

public class NoHeapRealtimeThread extends RealtimeThread implements Runnable {

	private class AreaAwareRunner implements Runnable {

		protected MemoryAreaWrapper area;
		protected Runnable logic;

		protected AreaAwareRunner(MemoryAreaWrapper area, Runnable logic) {
			this.area = area;
			this.logic = logic;
		}

		public void run() {
			area.executeInArea(logic);
		}
	}

	public NoHeapRealtimeThread(SchedulingParameters scheduling,
			ReleaseParameters release, MemoryParameters memory,
			MemoryAreaWrapper area, ProcessingGroupParameters group,
			Runnable logic) {

		super(release, (Runnable) null);

		if (logic == null) {
			logic = this;
		}

		if (area == null) {
			this.logic = logic;
		} else {
			this.logic = new AreaAwareRunner(area, logic);
		}

		this.thread = new Thread(this.logic);
		if (scheduling != null && scheduling instanceof PriorityParameters) {
			this.thread.setPriority(((PriorityParameters) scheduling)
					.getPriority());
		}
	}
}
