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


// Original Code:
// Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.


package jat.jat3D.behavior;

import jat.jat3D.plot3D.JatPlot3D;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * MouseZoom is a Java3D behavior object that lets users control the Z axis
 * translation of an object via a mouse drag motion with the second mouse
 * button. See MouseRotate for similar usage info.
 */

public class jat_MouseZoom extends MouseBehavior {

	float z_factor = .004f;
	Vector3d translation = new Vector3d();
	//public ViewingPlatform myvp;
	//public TransformGroup myvpt;
	JatPlot3D jatPlot3D;

	private MouseBehaviorCallback callback = null;

//	public jat_MouseZoom(ViewingPlatform myvp) {
//		super(0);
//		this.myvp = myvp;
//	}

	public jat_MouseZoom(JatPlot3D jatPlot3D) {
		super(0);
		this.jatPlot3D = jatPlot3D;

	}

	
	
	
	/**
	 * Creates a zoom behavior given the transform group.
	 * 
	 * @param transformGroup
	 *            The transformGroup to operate on.
	 */
	public jat_MouseZoom(TransformGroup transformGroup) {
		super(transformGroup);
	}

	/**
	 * Creates a default mouse zoom behavior.
	 **/
	public jat_MouseZoom() {
		super(0);
	}

	/**
	 * Creates a zoom behavior. Note that this behavior still needs a transform
	 * group to work on (use setTransformGroup(tg)) and the transform group must
	 * add this behavior.
	 * 
	 * @param flags
	 */
	public jat_MouseZoom(int flags) {
		super(flags);
	}

	/**
	 * Creates a zoom behavior that uses AWT listeners and behavior posts rather
	 * than WakeupOnAWTEvent. The behavior is added to the specified Component.
	 * A null component can be passed to specify the behavior should use
	 * listeners. Components can then be added to the behavior with the
	 * addListener(Component c) method.
	 * 
	 * @param c
	 *            The Component to add the MouseListener and MouseMotionListener
	 *            to.
	 * @since Java 3D 1.2.1
	 */
	public jat_MouseZoom(Component c) {
		super(c, 0);
	}

	/**
	 * Creates a zoom behavior that uses AWT listeners and behavior posts rather
	 * than WakeupOnAWTEvent. The behaviors is added to the specified Component
	 * and works on the given TransformGroup.
	 * 
	 * @param c
	 *            The Component to add the MouseListener and MouseMotionListener
	 *            to. A null component can be passed to specify the behavior
	 *            should use listeners. Components can then be added to the
	 *            behavior with the addListener(Component c) method.
	 * @param transformGroup
	 *            The TransformGroup to operate on.
	 * @since Java 3D 1.2.1
	 */
	public jat_MouseZoom(Component c, TransformGroup transformGroup) {
		super(c, transformGroup);
	}

	/**
	 * Creates a zoom behavior that uses AWT listeners and behavior posts rather
	 * than WakeupOnAWTEvent. The behavior is added to the specified Component.
	 * A null component can be passed to specify the behavior should use
	 * listeners. Components can then be added to the behavior with the
	 * addListener(Component c) method. Note that this behavior still needs a
	 * transform group to work on (use setTransformGroup(tg)) and the transform
	 * group must add this behavior.
	 * 
	 * @param flags
	 *            interesting flags (wakeup conditions).
	 * @since Java 3D 1.2.1
	 */
	public jat_MouseZoom(Component c, int flags) {
		super(c, flags);
	}

	// public void setViewingPlatform(ViewingPlatform myvp) {
	// this.myvp = myvp;
	// }

	public void initialize() {
		super.initialize();
		if ((flags & INVERT_INPUT) == INVERT_INPUT) {
			z_factor *= -1;
			invert = true;
		}
	}

	/**
	 * Return the y-axis movement multipler.
	 **/
	public double getFactor() {
		return z_factor;
	}

	/**
	 * Set the y-axis movement multipler with factor.
	 **/
	public void setFactor(float factor) {
		z_factor = factor;
	}

	public void processStimulus(Enumeration criteria) {
		WakeupCriterion wakeup;
		AWTEvent[] events;
		MouseEvent evt;
		// int id;
		// int dx, dy;

		while (criteria.hasMoreElements()) {
			wakeup = (WakeupCriterion) criteria.nextElement();
			if (wakeup instanceof WakeupOnAWTEvent) {
				events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
				if (events.length > 0) {
					evt = (MouseEvent) events[events.length - 1];
					doProcess(evt);
				}
			}

			else if (wakeup instanceof WakeupOnBehaviorPost) {
				while (true) {
					synchronized (mouseq) {
						if (mouseq.isEmpty())
							break;
						evt = (MouseEvent) mouseq.remove(0);
						// consolodate MOUSE_DRAG events
						while ((evt.getID() == MouseEvent.MOUSE_DRAGGED) && !mouseq.isEmpty() && (((MouseEvent) mouseq.get(0)).getID() == MouseEvent.MOUSE_DRAGGED)) {
							evt = (MouseEvent) mouseq.remove(0);
						}
					}
					doProcess(evt);
				}
			}

		}
		wakeupOn(mouseCriterion);
	}

	void doProcess(MouseEvent evt) {
		int id;
		int dx, dy;

		processMouseEvent(evt);

		if (((buttonPress) && ((flags & MANUAL_WAKEUP) == 0)) || ((wakeUp) && ((flags & MANUAL_WAKEUP) != 0))) {
			id = evt.getID();
			if ((id == MouseEvent.MOUSE_DRAGGED) && evt.isAltDown() && !evt.isMetaDown()) {

				x = evt.getX();
				y = evt.getY();

				dx = x - x_last;
				dy = y - y_last;

				if (!reset) {
					transformGroup.getTransform(currXform);

					
					
					jatPlot3D.jat_zoom(dy);

					// translation.z = dy*z_factor;
/*
					float zoom;
					if (dy > 0)
						zoom = 0.9f;
					else
						zoom = 1.1f;
					// transformX.set(translation);
					myvpt = myvp.getViewPlatformTransform();
					Transform3D Trans = new Transform3D();
					myvpt.getTransform(Trans);
					Vector3f v = new Vector3f();
					Trans.get(v);
					//util.print("v", v);
					Point3d p = new Point3d();
					p.x = zoom * v.x;
					p.y = zoom * v.y;
					p.z = zoom * v.z;
					//util.print("p", p);
					Transform3D lookAt = new Transform3D();
					lookAt.lookAt(p, new Point3d(0.0, 0.0, 0.0), new Vector3d(0, 0, 1.0));
					lookAt.invert();

					myvpt.setTransform(lookAt);

					if (invert) {
						// currXform.mul(currXform, transformX);
					} else {
						// currXform.mul(transformX, currXform);
					}

					//transformGroup.setTransform(currXform);

					//transformChanged(currXform);
*/
					if (callback != null)
						callback.transformChanged(MouseBehaviorCallback.ZOOM, currXform);

				} else {
					reset = false;
				}

				x_last = x;
				y_last = y;
			} else if (id == MouseEvent.MOUSE_PRESSED) {
				x_last = evt.getX();
				y_last = evt.getY();
			}
		}
	}

	/**
	 * Users can overload this method which is called every time the Behavior
	 * updates the transform
	 * 
	 * Default implementation does nothing
	 */
	public void transformChanged(Transform3D transform) {
	}

	/**
	 * The transformChanged method in the callback class will be called every
	 * time the transform is updated
	 */
	public void setupCallback(MouseBehaviorCallback callback) {
		this.callback = callback;
	}

}