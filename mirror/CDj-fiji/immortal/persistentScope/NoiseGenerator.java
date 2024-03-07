package immortal.persistentScope;

/**
 * Noise generator for the detector. The generator lives in the persistent detector scope. 
 * Its constructor runs in the persistent detector scope. Noise is generated in the detector scope 
 * (and the allocated objects live in the detector scope).
 */
public class NoiseGenerator {

	private Object[] noiseRoot;
	private int noisePtr;

	public NoiseGenerator() {
		if (immortal.Constants.DETECTOR_NOISE) {
			noiseRoot = new  Object[immortal.Constants.DETECTOR_NOISE_REACHABLE_POINTERS];
			noisePtr = 0;
		}
	}

	private void generateNoise() {

		for(int i=0 ; i<immortal.Constants.DETECTOR_NOISE_ALLOCATE_POINTERS ; i++) {
			noiseRoot [ ( noisePtr++ ) % noiseRoot.length ] = new byte[immortal.Constants.DETECTOR_NOISE_ALLOCATION_SIZE];
		}
		
	}

	private void generateNoiseWithVariableObjectSize() {

		int currentIncrement = 0;
		int maxIncrement = immortal.Constants.DETECTOR_NOISE_MAX_ALLOCATION_SIZE - immortal.Constants.DETECTOR_NOISE_MIN_ALLOCATION_SIZE;

		for(int i=0 ; i<immortal.Constants.DETECTOR_NOISE_ALLOCATE_POINTERS ; i++) {
			noiseRoot [ ( noisePtr++ ) % noiseRoot.length ] = new byte[ immortal.Constants.DETECTOR_NOISE_MIN_ALLOCATION_SIZE + (currentIncrement % maxIncrement) ];
			currentIncrement += immortal.Constants.DETECTOR_NOISE_ALLOCATION_SIZE_INCREMENT;
		}
	}

	public void generateNoiseIfEnabled() {
		if (immortal.Constants.DETECTOR_NOISE) {

			if (immortal.Constants.DETECTOR_NOISE_VARIABLE_ALLOCATION_SIZE) {
				generateNoiseWithVariableObjectSize();
			} else {
				generateNoise();
			}
		}
	}
}
