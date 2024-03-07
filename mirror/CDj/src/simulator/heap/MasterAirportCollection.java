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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javacp.util.Hashtable;
import java.util.Properties;
import javacp.util.StringTokenizer;

/**
 * This here class is meant as a global (inter-VM) method for configuring
 * airports and no-fly zones. The philosophy here is that the airport and no-fly
 * set is immutable at least in the short-term, and therefore a configuration
 * file is sufficient for specifying that set. Each component in the Group 2
 * collision detection system must use this class at start-up in the following
 * way:
 * 
 * <pre>
 * MasterAirportCollection.readAirportAndNoFlyConfig(some_filename);
 * Airport[] airports = MasterAirportCollection.getAllAirports();
 * Region[] nofly = MasterAirportCollection.getAllNoFlyZones();
 * </pre>
 * 
 * Note that some_filename should be preferrably specified on the command-line
 * to the mediator.
 */
class MasterAirportCollection {

	public static class NoSuchRegionTypeException extends Exception {
		String region;

		public NoSuchRegionTypeException(final String region) {
			super("No such region: " + region);
			this.region = region;
		}

		public String getRegion() {
			return region;
		}
	}

	public static class NoSuchNoFlyCodeException extends Exception {
		String region;

		public NoSuchNoFlyCodeException(final String region) {
			super("No such no fly code: " + region);
			this.region = region;
		}

		public String getNoFlyCode() {
			return region;
		}
	}

	static Hashtable airports = new Hashtable();
	static Hashtable nofly = new Hashtable();

	public static void submitAirport(final SimAirport port) {
		airports.put(port.getAirportCode(), port);
	}

	public static SimAirport lookup(final String code) {
		return (SimAirport) airports.get(code);
	}

	// SuppressWarnings("unchecked")
	public static void readAirportAndNoFlyConfig(final String filename) {
		/*
		 * nofly.airport_ord = cylindrical 700 500 10 0 10 nofly.airport_ind =
		 * cylindrical 750 550 10 0 10 airport.ord = 700 500 airport_ord 10
		 * airport.ind = 750 550 airport_ind 10
		 */
		{
			Region region = null;
			String code = "";
			code = "airport_ord";
			{
				final Vector2d vec = new Vector2d(700, 500);
				final float radius = 10;
				final float min_z = 0;
				final float max_z = 10;
				region = new ReadableCylindricalRegion(vec, radius, min_z,
						max_z);
			}
			nofly.put(code, region);
		}
		{
			Region region = null;
			String code = "";
			code = "airport_ind";
			{
				final Vector2d vec = new Vector2d(750, 550);
				final float radius = 10;
				final float min_z = 0;
				final float max_z = 10;
				region = new ReadableCylindricalRegion(vec, radius, min_z,
						max_z);
			}
			nofly.put(code, region);

		}
		{
			final String code = "ord";
			final Vector2d vec = new Vector2d(700, 500);
			final String nofly_code = "airport_ord";
			final Region region = (Region) nofly.get(nofly_code);
			final int cap = 10;
			airports.put(code, new SimAirport(code, vec, region, cap));
		}
		{
			final String code = "ind";
			final Vector2d vec = new Vector2d(750, 550);
			final String nofly_code = "airport_ind";
			final Region region = (Region) nofly.get(nofly_code);
			final int cap = 10;
			airports.put(code, new SimAirport(code, vec, region, cap));
		}

		if (true)
			return; // Properties seem to trigger an Ovm bug.

		final Properties props = new Properties();
		try {
			props.load(new FileInputStream(filename));
		} catch (final FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		java.util.Enumeration e = props.propertyNames();
		while (e.hasMoreElements()) {
			final String name = (String) e.nextElement();
			if (name.startsWith("nofly.")) {
				final String code = name.substring(6);
				Region region = null;
				final String val = props.getProperty(name);
				final StringTokenizer tox = new StringTokenizer(val);
				final String type = tox.nextToken();
				if (type.equals("cylindrical")) {
					final Vector2d vec = new Vector2d(Float.parseFloat(tox
							.nextToken()), Float.parseFloat(tox.nextToken()));
					final float radius = Float.parseFloat(tox.nextToken());
					final float min_z = Float.parseFloat(tox.nextToken());
					final float max_z = Float.parseFloat(tox.nextToken());
					region = new ReadableCylindricalRegion(vec, radius, min_z,
							max_z);
				}
				nofly.put(code, region);
			}
		}

		e = props.propertyNames();
		while (e.hasMoreElements()) {
			final String name = (String) e.nextElement();
			if (name.startsWith("airport.")) {
				final String code = name.substring(8);
				final String val = props.getProperty(name);
				final StringTokenizer tox = new StringTokenizer(val);
				final Vector2d vec = new Vector2d(Float.parseFloat(tox
						.nextToken()), Float.parseFloat(tox.nextToken()));
				final String nofly_code = tox.nextToken();
				final Region region = (Region) nofly.get(nofly_code);
				if (region == null) {
					throw new Error();
				}
				final int cap = Integer.parseInt(tox.nextToken());
				airports.put(code, new SimAirport(code, vec, region, cap));
			}
		}
	}
}
