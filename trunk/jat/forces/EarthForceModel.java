/* JAT: Java Astrodynamics Toolkit
*
* Copyright (c) 2005 Emergent Space Technologies Inc. All rights reserved.
*
* This file is part of JAT. JAT is free software; you can 
* redistribute it and/or modify it under the terms of the 
* GNU General Public License as published by the Free Software 
* Foundation; either version 2 of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
* 
*/
package jat.forces;

import jat.matvec.data.VectorN;
import jat.spacecraft.Spacecraft;
import jat.timeRef.EarthRef;

/**
 * This interface allows for uniformity among extensible and newly added
 * force models.
 * 
 * @author Richard C. Page III
 *
 */
public interface EarthForceModel {

	/** Acceleration
     * @param eRef Earth reference class
     * @param sc Spacecraft parameters and state
     * @return the acceleration [m/s^2]
     */
    public VectorN acceleration(EarthRef eRef, Spacecraft sc);	
	
}