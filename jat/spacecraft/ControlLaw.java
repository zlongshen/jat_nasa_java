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
package jat.spacecraft;

import jat.alg.integrators.*;
import jat.forces.*;
import jat.matvec.data.VectorN;

/**
 * A template class intended to be extended to implement various control
 * laws.  This class represents a unity feedback control law.
 * 
 * spacecraft[]-->(sum)-->[Dynamics]->[Control Law]-->acceleration[]
 *                  A -                             |
 *                  |_______________________________|
 * 
 * @author Richard C. Page III
 */
public class ControlLaw {
    
    public ControlLaw(){    }

    public double[] compute_control(double t, double[] x){
        return new double[x.length];
    }
    
    public double[] compute_control(double t, double[] x, double[] xrel){
        return new double[3];
    }
    
}