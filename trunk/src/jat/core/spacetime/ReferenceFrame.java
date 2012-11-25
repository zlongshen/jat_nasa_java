/* JAT: Java Astrodynamics Toolkit
 * 
  Copyright 2012 Tobias Berthold

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package jat.core.spacetime;

import jat.core.ephemeris.DE405Body;
import jat.core.ephemeris.DE405Body.body;
import jat.core.ephemeris.DE405Plus;
import jat.coreNOSA.cm.Constants;
import jat.coreNOSA.cm.cm;
import jat.coreNOSA.math.MatrixVector.data.VectorN;
import jat.coreNOSA.spacetime.Time;

import java.io.IOException;
import java.util.EnumSet;

public class ReferenceFrame {

	public static VectorN ICRF_to_MEOP(VectorN[] posvelICRF, VectorN in, Time t) throws IOException {

		// convert coordinates from ICRF to ECI
		VectorN posvelECI[]=new VectorN[11];
		for (body q : EnumSet.allOf(body.class)) {
			int bodyNumber = q.ordinal();
			//System.out.println(bodyNumber);
//			posvelECI[bodyNumber] = ICRF_to_ECI(posvelICRF, in, t);
		}
		// vector from earth to moon at time t
		int MOON = DE405Body.body.MOON.ordinal();
//		int EARTH = DE405Body.body.EARTH.ordinal();

//		double[] posvel = new double[6];
//		posvel[0] = posvelECI[EARTH].x[0];
//		posvel[1] = posvelECI[EARTH].x[1];
//		posvel[2] = posvelECI[EARTH].x[2];
//		posvel[3] = posvelECI[EARTH].x[3];
//		posvel[4] = posvelECI[EARTH].x[4];
//		posvel[5] = posvelECI[EARTH].x[5];
//		VectorN EarthPosVel = new VectorN(posvel);
//		posvel[0] = posvelECI[MOON].x[0];
//		posvel[1] = posvelECI[MOON].x[1];
//		posvel[2] = posvelECI[MOON].x[2];
//		posvel[3] = posvelECI[MOON].x[3];
//		posvel[4] = posvelECI[MOON].x[4];
//		posvel[5] = posvelECI[MOON].x[5];
		VectorN MoonPos = new VectorN(posvelECI[MOON].x[0],posvelECI[MOON].x[1],posvelECI[MOON].x[2]);
		VectorN MoonVel = new VectorN(posvelECI[MOON].x[3],posvelECI[MOON].x[4],posvelECI[MOON].x[5]);

//		VectorN EarthtoMoonposvel = MoonPosVel.minus(EarthPosVel);
//		VectorN EarthtoMoonpos = new VectorN(EarthtoMoonposvel.x[0], EarthtoMoonposvel.x[1], EarthtoMoonposvel.x[2]);
//		VectorN MoonVel = new VectorN(MoonPosVel.x[3], MoonPosVel.x[4], MoonPosVel.x[5]);
		VectorN PlaneNormal = MoonPos.crossProduct(MoonVel);
		VectorN xAxis = new VectorN(1e8, 0, 0);

		double angle = PlaneNormal.angle(xAxis);

		VectorN returnval; 
	
		/*
		*/
	//	returnval=ICRF_to_ECI(posvelICRF, in, t);
		returnval= ICRFxAxis_rotate(in, cm.Degree(angle));
		return returnval;
	}

	public static VectorN ICRFxAxis_rotate(VectorN rv, double angleDegrees) {
		VectorN returnval = new VectorN(6);
		double x, y, z, vx, vy, vz, eps, c, s;
		x = rv.get(0);
		y = rv.get(1);
		z = rv.get(2);
		eps = cm.Rad(angleDegrees);
		c = Math.cos(eps);
		s = Math.sin(eps);
		returnval.x[0] = x;
		returnval.x[1] = c * y + s * z;
		returnval.x[2] = -s * y + c * z;
		vx = rv.get(3);
		vy = rv.get(4);
		vz = rv.get(5);
		returnval.x[3] = vx;
		returnval.x[4] = c * vy + s * vz;
		returnval.x[5] = -s * vy + c * vz;

		return returnval;
	}

//	public static VectorN ICRF_to_ECI(VectorN[] posvelICRF, VectorN in, Time mytime) throws IOException {
//
//		double[] posvel = new double[6];
//		int bodyNumber = DE405Body.body.EARTH.ordinal();
//		posvel[0] = posvelICRF[bodyNumber].x[0];
//		posvel[1] = posvelICRF[bodyNumber].x[1];
//		posvel[2] = posvelICRF[bodyNumber].x[2];
//		posvel[3] = posvelICRF[bodyNumber].x[3];
//		posvel[4] = posvelICRF[bodyNumber].x[4];
//		posvel[5] = posvelICRF[bodyNumber].x[5];
//
//		VectorN earthPosVel = new VectorN(posvel);
//
//		VectorN returnval = in.minus(earthPosVel);
//		return returnval;
//	}

	public static VectorN ICRF_to_ECIpos(DE405Plus Eph, VectorN bodyposICRF, Time t) {
		int EARTH = body.EARTH.ordinal();

		return bodyposICRF.minus(Eph.posICRF[EARTH]);
	}

	public static VectorN ICRF_to_ECIvel(DE405Plus Eph, VectorN bodyvelICRF, Time t) {
		int EARTH = body.EARTH.ordinal();

		return bodyvelICRF.minus(Eph.velICRF[EARTH]);
	}

	
	
	public static VectorN ecliptic_obliquity_rotate(VectorN rv) {
		//VectorN returnval = new VectorN(6);
		VectorN returnval = new VectorN(3);
		double x, y, z, vx, vy, vz, eps, c, s;
		x = rv.get(0);
		y = rv.get(1);
		z = rv.get(2);
		eps = cm.Rad(Constants.eps);
		c = Math.cos(eps);
		s = Math.sin(eps);
		returnval.x[0] = x;
		returnval.x[1] = c * y + s * z;
		returnval.x[2] = -s * y + c * z;
//		vx = rv.get(3);
//		vy = rv.get(4);
//		vz = rv.get(5);
//		returnval.x[3] = vx;
//		returnval.x[4] = c * vy + s * vz;
//		returnval.x[5] = -s * vy + c * vz;

		return returnval;
	}



}
