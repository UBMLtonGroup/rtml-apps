package heap;

import immortal.ImmortalEntry;

public abstract class PrecompiledSimulator {

	public static void dumpStats() {}

	protected Object[] positions;
	protected Object[] lengths;
	protected Object[] callsigns;

	// args .. ignored
	public static void generate(final String[] args) {

		(new Simulator()).generate();
	}
	
	public void generate() {
      /*
		synchronized (ImmortalEntry.initMonitor) {

		/*	if (!immortal.Constants.PRESIMULATE) {
				ImmortalEntry.simulatorReady = true;
				ImmortalEntry.initMonitor.notifyAll();
			}

			while (!ImmortalEntry.detectorReady) {
				try {
					ImmortalEntry.initMonitor.wait();
				} catch (InterruptedException e) {
				}
			} */
		/*}


		if (positions.length < immortal.Constants.MAX_FRAMES) {
			throw new RuntimeException("Not enough frames in pre-compiled simulator.");
		}

		for(int frameIndex=0; frameIndex<immortal.Constants.MAX_FRAMES;frameIndex++) {

			immortal.ImmortalEntry.frameBuffer.putFrame( (float[])positions[frameIndex], 
					(int[])lengths[frameIndex], 
					(byte[])callsigns[frameIndex]);
		}
		//System.out.println("Generated "+immortal.Constants.MAX_FRAMES+" frames.");

/*		if (immortal.Constants.PRESIMULATE) {
			synchronized (ImmortalEntry.initMonitor) {
				ImmortalEntry.simulatorReady = true;
				ImmortalEntry.initMonitor.notifyAll();
			}
		} */

	} 
}

