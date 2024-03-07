/**
 * 
 * Copyright (c) 2001-2010, Purdue University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the Purdue University nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package heap;

/**
 * The <code>BaseFrameFactory</code> class implements the base functionality
 * of the FrameFactory for the simulator. It simply creates instances of
 * <code>BaseFrame</code> for the simulator.
 * 
 * @author Ben L. Titzer
 */
class FrameFactory {
	/**
	 * The <code>getBuilder</code> method is used to retrieve a builder
	 * capable of building a new frame. The builder is then used to add aircraft
	 * and their positions to the frame until it is ready to be sealed.
	 * 
	 * @param max_craft
	 *            the maximum number of craft in the frame
	 * @param timestamp
	 *            the absolute time in the simulation that this frame will
	 *            represent
	 * @returns a builder capable of building the specified frame
	 */
	public Builder getBuilder(final int max_craft, final double timestamp) {
		return new Builder(max_craft, timestamp);
	}

	/**
	 * The <code>getMutator</code> method is used to get a mutator for a
	 * builder. A <code>FrameFactory.Builder</code> instance can only add:
	 * this <code>FrameFactory.Mutator</code> class is used by the
	 * MotionDirector to modify (but not add or delete) the positions in a
	 * frame.
	 * 
	 * @param builder
	 *            the builder of the not yet completed frame for which a mutator
	 *            is requested
	 * @returns a mutator capable of modifying the positions in the builder
	 * @throws IllegalArgumentException
	 *             if the builder is not of the correct type
	 */
	public Mutator getMutator(final Builder builder) {
		try {
			final Builder b = builder;
			if (b.mutator != null)
				throw new BuilderError(
						"BaseBuilder cannot have more than one BaseMutator");
			final Mutator m = new Mutator(b.frame);
			b.mutator = m;
			return m;
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException(
					"Cannot create BaseMutator for incompatible Builder");
		}
	}

	/**
	 * The <code>BaseBuilder</code> class implements the functionality of a
	 * builder for frames in the base implementation. It controls access to the
	 * creation of a <code>BaseFrame</code> instance and accesses its internal
	 * fields to fill the frame
	 * 
	 * @author Ben L. Titzer
	 */
	class Builder {

		private int cursor;
		private final int max_craft;
		private final SimFrame frame;
		private boolean finished;
		private Mutator mutator;

		private Builder(final int max_craft, final double timestamp) {
			this.frame = new SimFrame(max_craft, timestamp);
			this.finished = false;
			this.max_craft = max_craft;
		}

		public void addPosition(final SimAircraft a, final Vector3d pos) {
			synchronized (frame) {
				if (finished)
					throw new BuilderError("BaseBuilder already finished");
				if (cursor >= max_craft)
					throw new BuilderError("BaseFrame full");
				frame.aircraft[cursor] = a;
				frame.positions[cursor] = new Vector3d(pos);
				cursor++;
				frame.used++;
			}
		}

		public SimFrame makeFrame() {
			synchronized (frame) {
				finished = true;
				if (mutator != null && !mutator.comitted)
					throw new BuilderError(
							"BaseBuilder has uncommitted BaseMutator");
				return frame;
			}
		}
	}

	/**
	 * The <code>BaseMutator</code> class is the base implementation of the
	 * <code>FrameFactory.Mutator</code> that is needed by the MotionDirector
	 * in order to modify the positions of aircraft during the computation of
	 * the next frame within the simulator.
	 */
	class Mutator {
		private int cursor;
		private final SimFrame frame;
		private boolean comitted;

		private Mutator(final SimFrame frame) {
			this.frame = frame;
		}

		public SimAircraft getCurrent(final Vector3d pos) {
			synchronized (frame) {
				if (cursor >= frame.used)
					throw new IndexOutOfBoundsException(
							"BaseMutator cursor out of bounds");
				pos.set(frame.positions[cursor]);
				return frame.aircraft[cursor];
			}
		}

		public void updateCurrent(final Vector3d pos) {
			synchronized (frame) {
				if (comitted)
					throw new BuilderError("BaseMutator already committed");
				if (cursor >= frame.used)
					throw new IndexOutOfBoundsException(
							"BaseMutator cursor out of bounds");
				frame.positions[cursor].set(pos);
			}

		}

		public void rewind() {
			cursor = 0;
		}

		public void advance() {
			cursor++;
		}

		public boolean hasNext() {
			synchronized (frame) {
				return cursor < frame.used;
			}
		}

		public void commit() {
			comitted = true;
		}
	}

	static class BuilderError extends Error {
		BuilderError(final String ignore) {
		}
	}
}
