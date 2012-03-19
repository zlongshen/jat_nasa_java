/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2002 National Aeronautics and Space Administration and the Center for Space Research (CSR),
 * The University of Texas at Austin. All rights reserved.
 *
 * This file is part of JAT. JAT is free software; you can
 * redistribute it and/or modify it under the terms of the
 * NASA Open Source Agreement
 * 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * NASA Open Source Agreement for more details.
 *
 * You should have received a copy of the NASA Open Source Agreement
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
/*
*  Description :  Animation code for attitude
*/

package jat.demo.vr.attitude;

import jat.vr.*;
import java.util.Enumeration;
import javax.vecmath.*;
import javax.media.j3d.*;

public class SimulationClock extends Behavior
{
	WakeupCriterion yawn;
	long SimTime;
	int DeltaT;
	private Transform3D T_3D;
	Vector3d V_view = new Vector3d(0.f, 0.f, 0.0f);
	Point3d origin = new Point3d(0.0, 0.0, 0.0);
	Vector3d away = new Vector3d(100000.0, 0.0, 0.0);
	ControlPanel panel;
	int i;
	AttitudeSimClock b;
	attitude a;

	//	Temporarily added (by Noriko)
	private float time_values[]; // time values
	private float quat_values[][]; // quarternion values
	int numberOfPts; // Number of data points generated by the numerical simulation
	Transform3D satTrans = new Transform3D();
	int maneuver = 0;

	public SimulationClock(AttitudeSimClock a, int ts, ControlPanel panel, int pts, float tvars[][])
	{
		maneuver = 1;
		/* ************** Added by Noriko ************************** */
		numberOfPts = pts;
		time_values = new float[numberOfPts + 1]; // Time Array
		quat_values = new float[4][numberOfPts + 1]; // Quaternion Array

		//Assign the time and quaternion values
		for (int index = 0; index <= numberOfPts; index++)
		{
			time_values[index] = tvars[0][index];
			quat_values[0][index] = tvars[1][index]; // e1
			quat_values[1][index] = tvars[2][index]; // e2
			quat_values[2][index] = tvars[3][index]; // e3
			quat_values[3][index] = tvars[4][index]; // e4
		}
		/* ********************************************************** */

		int DeltaT = ts;
		this.panel = panel;
		this.b = a;
		T_3D = new Transform3D();
		yawn = new WakeupOnElapsedTime(DeltaT);
	}

	public SimulationClock(attitude a, int ts,  ControlPanel panel)
	{

		DeltaT = ts;
		this.panel = panel;
		this.a = a;
		T_3D = new Transform3D();
		yawn = new WakeupOnElapsedTime(DeltaT); // DeltaT = # of milliseconds to 
		// the wake up
	}

	public void initialize()
	{
		wakeupOn(yawn);
		SimTime = 0;
	}

	public void processStimulus(Enumeration e)
	{

		// Record frame
		if (i > 1 && i < 10)
		{
			System.out.println("" + i);
			a.c.writeJPEG_ = true;
			//a.c.repaint();
		}

		i++;
		int curr_pt = i;
		// general animation
		SimTime += DeltaT;
		wakeupOn(yawn);

		// set new spacecraft attitude
		if (maneuver == 1) // if a quaternion array is passed
		{
			if (i < numberOfPts)
			{

				satTrans.setRotation(
					new Quat4f(
						quat_values[0][curr_pt],
						quat_values[1][curr_pt],
						quat_values[2][curr_pt],
						quat_values[3][curr_pt]));
				a.spacecraft.set_attitude(satTrans);
			}
		}
		if (maneuver == 0) // if a quaternion array is not passed
		{
			a.spacecraft.set_attitude(SimTime * .0002, SimTime * .0002, SimTime * .0002);
		}

		// Update text in panel
		jat_view.TG_vp.getTransform(T_3D);
		T_3D.get(V_view);
		panel.label.setText(
			"Time " + SimTime + "  x " + (long)V_view.x + "  y " + (long)V_view.y + "  z " + (long)V_view.z);

		// set new viewing platform position
		//V_view.x+=100;
		//jat_view.set_view_position(V_view.x,V_view.y,V_view.z);

		// set a new viewing direction
		//jat_view.set_view_direction(V_view,origin);

	} //  end method

} //  end class