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

/**
 * 
 */
package immortal.persistentScope.transientScope;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author tomas
 *
 */
public class ReducerTestCase {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link immortal.persistentScope.transientScope.Reducer#isInVoxel(immortal.persistentScope.transientScope.Vector2d, immortal.persistentScope.transientScope.Motion)}.
	 */
	@Test
	public void testIsInVoxel() {
		Reducer reducer = new Reducer(immortal.Constants.GOOD_VOXEL_SIZE);
		
		// default proximity radius is 1
		// default grid element size is 2*default_proximity_radius = 2
		// the motion is thus checked against a square with base 3 (grid_element_size + proximity_radius) -.5 .. 2.5
		
		Vector2d gridElement = new Vector2d(0,0);
		Motion motion = new Motion(null, new Vector3d(0,0,0), new Vector3d(1,1,0));

		// r = 0.5 (half proximity radius)
		// v_s = 2
		// x0 = 0 , y0 = 0 (motion start)
		// xv = 1, yv = 1 (motion delta)
		// low_x = -0.5, low_y = -0.5
		// high_x = 2.5, high_y = 2.5 (both x and y go through)
		// the grid element against which the check is done is

		
		assertTrue(reducer.isInVoxel( gridElement, motion ));
		
		Motion motion2 = new Motion(null, new Vector3d(100,100,0), new Vector3d(111,111,0));
		assertFalse(reducer.isInVoxel( gridElement, motion2 ));
		
		Motion motion3 = new Motion(null, new Vector3d(0.0f,0.75f,0.0f), new Vector3d(0.25f,1.0f,0.0f));
		assertTrue(reducer.isInVoxel( gridElement, motion3 ));
		
		Vector3d start = new Vector3d(-0.6f, 2.0f, 0.0f);
		for (int d = 1; d <= 10 ; d++ ) {
			float shift = (2.5f*d)/10.0f;
			Vector3d end = new Vector3d( -0.5f + shift, 2.6f, 0.0f) ;
			Motion mgen = new Motion(null, start, end);
			assertTrue(reducer.isInVoxel( gridElement, mgen));
			
			end = new Vector3d( 2.6f, 2.5f - shift, 0.0f);
			assertTrue(reducer.isInVoxel(gridElement, new Motion(null, start, end)));
			
			end = new Vector3d( 2.5f - shift, -0.6f, 0.0f);
			assertTrue(reducer.isInVoxel(gridElement, new Motion(null, start, end)));
		}
		
		
	}

}
