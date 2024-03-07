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

import javacp.util.Enumeration;
import javacp.util.Vector;

import command.evaluator.EvaluationContext;

class SimAircraftProducer {

	Vector test_aircrafts = new Vector();
	EvaluationContext e = new EvaluationContext();
	double cur_time;

	/**
	 * Call this to add an aircraft; expression is an array of parametrizations
	 * in x, y, and z.
	 */
	public void addAircraft(String call_sign, String[] expression,
			SimAirport source, SimAirport destination) throws Exception {
		test_aircrafts.addElement(new SimAircraft(call_sign, e, expression,
				source, destination));
	}

	public void addInitialAircraft(SimulationRegion region,
			FrameFactory.Builder builder) {
		cur_time = 0.0;
		e.setVarValue("t", EvaluationContext.TYPE_NUMBER, cur_time);
		Enumeration crafts = test_aircrafts.elements();
		while (crafts.hasMoreElements()) {
			SimAircraft craft = (SimAircraft) crafts.nextElement();
			builder.addPosition(craft, craft.evaluatePosition(e));
		}
	}

	public void addAircraft(SimulationRegion region, SimFrame prev,
			FrameFactory.Builder builder, double delta) {
	}

	public void computeNewPositions(SimulationRegion region,
			FrameFactory.Mutator mutator, double delta) {
		cur_time += delta;
		e.setVarValue("t", EvaluationContext.TYPE_NUMBER, cur_time);

		Vector3d vec = new Vector3d();
		while (mutator.hasNext()) {
			SimAircraft craft = mutator.getCurrent(vec);
			Vector3d new_pos = craft.evaluatePosition(e);
			mutator.updateCurrent(new_pos);
			mutator.advance();
		}

	}

}
