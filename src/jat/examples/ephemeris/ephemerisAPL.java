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

package jat.examples.ephemeris;

import java.io.IOException;

import jat.core.ephemeris.DE405APL;
import jat.core.math.matvec.data.VectorN;
import jat.core.spacetime.Time;

public class ephemerisAPL
{
    public static void main (String argv[])
    {
        Time mytime=new Time(2002, 2, 17, 12, 0, 0);
		DE405APL ephem = new DE405APL();
        VectorN rv;
		try {
			rv = ephem.get_planet_posvel(DE405APL.MARS, mytime.jd_tt());
	        System.out.println("The position of Mars on 10-17-2002 at 12:00pm was ");
	        System.out.println("x= "+rv.get(0)+" km");
	        System.out.println("y= "+rv.get(1)+" km");
	        System.out.println("z= "+rv.get(2)+" km");
	        System.out.println("The velocity of Mars on 10-17-2002 at 12:00pm was ");
	        System.out.println("vx= "+rv.get(3)+" km/s");
	        System.out.println("vy= "+rv.get(4)+" km/s");
	        System.out.println("vz= "+rv.get(5)+" km/s");
		} catch (IOException e) {
			e.printStackTrace();
		}


    }
}
