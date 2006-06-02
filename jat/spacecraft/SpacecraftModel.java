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

import jat.alg.integrators.Derivatives;
import jat.matvec.data.Matrix;
import jat.matvec.data.Quaternion;
import jat.matvec.data.VectorN;
import jat.spacetime.UniverseModel;

/**
 * This class models the entire system of a spacecraft including any software or 
 * opperational protocols associated with it.  It contains a Spacecraft object
 * which represents the physical parameters and state of the spacecraft, a ControlLaw
 * object which models all of the control functions, and a StateEstimation object
 * which models all of the estimated states and filters associated with the spacecraft.
 * 
 * Use this object in simulations that require spacecraft navigation and control.  
 * 
 * @author Richard C. Page III
 *
 */
public class SpacecraftModel implements Derivatives,PrimarySpacecraft, MemberSpacecraft {

    /**
     * Physical characteristics, dynamics, and state.
     */
    protected Spacecraft sc;
    /**
     * World model installed in spacecraft avionics.
     */
    protected UniverseModel spacetime;
    /**
     * Model for all of the control functions, both linear and angular.
     */
    protected ControlLaw controller;
    /**
     * Model for the state estimation algorithms and filters.
     */
    protected StateEstimation estimator;
    /**
     * Control thrust.
     */
    protected VectorN thrust;
    /**
     * Control torque.
     */
    protected VectorN torque;
//    /**
//     * Onboard computer timestep
//     */
//    protected double sc_dt;
    
    /**
     * Constructor.
     * @param r Position.
     * @param v Velocity.
     * @param cr Coefficient of Reflectivity.
     * @param cd Coefficient of Drag.
     * @param area Cross-sectional area.
     * @param mass Mass.
     */
    public SpacecraftModel(VectorN r, VectorN v, double cr, double cd, double area, double mass){
        sc = new Spacecraft(r,v,cr,cd,area,mass);
        controller = new ControlLaw();
        estimator = new StateEstimation();
        thrust = new VectorN(0,0,0);
        torque = new VectorN(0,0,0);
    }
    
    /**
     * Constructor - creates a generic model from an existing Spacecraft object.
     * @param s Spacecraft object.
     */
    public SpacecraftModel(Spacecraft s){
        sc = s;
        controller = new ControlLaw();
        estimator = new StateEstimation();
        thrust = new VectorN(0,0,0);
        torque = new VectorN(0,0,0);
    }
    
    public SpacecraftModel(Spacecraft s, UniverseModel u){
    	sc = s;
        controller = new ControlLaw();
        estimator = new StateEstimation();
        thrust = new VectorN(0,0,0);
        torque = new VectorN(0,0,0);
    	spacetime = u;
    }
    
    /**
     * Constructor - creates a model from other existing elements.
     * @param s Spacecraft object.
     * @param c Controller.
     * @param e Estimator.
     */
    public SpacecraftModel(Spacecraft s, ControlLaw c, StateEstimation e){
        sc = s;
        controller = c;
        estimator = e;
        thrust = new VectorN(0,0,0);
        torque = new VectorN(0,0,0);
    }
    
    /**
     * Updates the spacecraft state based on a time and state vector.
     * @param t Time
     * @param x State
     */
    public void update(double t, double[] x){
        //TODO - call estimator
        sc.updateState(x);
        spacetime.update(t);
    }
    
    /**
     * Update the spacecraft's computer models of forces and time.
     * @param t seconds since epoch
     */
    public void update(double t){
    	spacetime.update(t);
    }

    /**
     * Update the estimated state based upon the propagated state vector.
     * @param t Time
     * @param x State
     */
    public void update_estimation(double t, double[] x){
        estimator.update(t,x);
    }
    /**
     * Set the controller to use in the spacecraft model.
     * @param c Controller
     */
    public void set_controller(ControlLaw c){
        controller = c;
    }    
    /**
     * Set the estimator to use in the spacecraft model.
     * @param e Estimator.
     */
    public void set_estimator(StateEstimation e){
        estimator = e;
    }
    /**
     * Return the spacecraft object representing the physical parameters and state.
     * @return The physical spacecraft.
     */
    public Spacecraft get_spacecraft(){
        return sc;
    }
    /**
     * Return the spacecraft id string.
     * @return String ID
     */
    public String get_id(){
        return sc.id;
    }
    /**
     * Return a numerical representation of the (String) ID
     * @return Integer ID
     */
    public int get_numeric_id(){
        return Integer.parseInt(sc.id);
    }
    
    /**
     * Compute the thrust generated by the spacecraft controller.
     * @param t Time in seconds since epoch.
     * @param x State
     * @param xdot Derivative of the State from other sources.
     * @return The controller derivatives (not including other sources).
     */
    public void compute_control(double t, double[] x, double[] xdot) {
        double[] out = controller.compute_control(t,x,xdot);
        thrust.x = out;
    }
    
    /**
     * Compute the thrust generated by the spacecraft controller.
     * @param t Time in seconds since epoch.
     * @param x State
     */
    public void compute_control(double t, double[] x){
        double[] out = controller.compute_control(t,x);
        thrust.x = out;
    }
    
    /**
     * Return the current control thrust.
     * @return Control thrust acceleration
     */
    public double[] control_thrust_derivs(double t, double[] dX){
        double[] out = new double[dX.length];
        out[0] = 0;
        out[1] = 0;
        out[2] = 0;
        out[3] = thrust.x[0];
        out[4] = thrust.x[1];
        out[5] = thrust.x[2];
        for(int i=6; i<dX.length; i++){
            out[i] = dX[i];
        }
        return out;        
    }
    
    /**
     * Get the absolute (inertial) position of the spacecraft.
     * @see PrimarySpacecraft#get_abs_pos()
     */
    public VectorN get_abs_pos() {
        return sc.r();
    }

    /**
     * Get the absolute (inertial) velocity of the spacecraft.
     * @see jat.spacecraft.PrimarySpacecraft#get_abs_vel()
     */
    public VectorN get_abs_vel() {
        return sc.v();
    }

    /**
     * Get the spacecraft attitude quaternion.
     * @see jat.spacecraft.PrimarySpacecraft#get_attitude()
     */
    public Quaternion get_attitude() {
        return sc.q();
    }

    /**
     * Get the transformation matrix from inertial to Radial-Intrack-Crosstrack frame.
     * @see jat.spacecraft.PrimarySpacecraft#get_inertial2RIC()
     */
    public Matrix get_inertial2RIC() {
        return sc.get_inertial2RIC();
    }

    /**
     * Get the rotation vector of the Radial-Intrack-Crosstrack frame.
     * @see jat.spacecraft.PrimarySpacecraft#get_omegaRIC()
     */
    //* this is true for circular orbits, for elliptical orbits
    //* it is the instantaneous rate
    public VectorN get_omegaRIC() {
        double r = sc.r().mag();
        double vdotr = sc.v().dotProduct(sc.r().unitVector());
        VectorN vr = sc.r().unitVector().times(vdotr);
        VectorN vtheta = sc.v().minus(vr);
        double vintrack = vtheta.mag();
        VectorN omega = (sc.r().unitVector().crossProduct(sc.v()).unitVector());
        return omega.times(vintrack/r);
    }

    /**
     * Get the position of the spacecraft relative to the primary spacecraft in the 
     * formation.
     * @see jat.spacecraft.MemberSpacecraft#get_rel_pos(jat.spacecraft.PrimarySpacecraft)
     */
    public VectorN get_rel_pos(PrimarySpacecraft ps) {
        VectorN rel = sc.r().minus(ps.get_abs_pos());
        return rel;
    }

    /**
     * Get the velocity of the spacecraft relative to the primary spacecraft in the
     * formation. 
     * @see jat.spacecraft.MemberSpacecraft#get_rel_vel(jat.spacecraft.PrimarySpacecraft)
     */
    public VectorN get_rel_vel(PrimarySpacecraft ps) {
        VectorN rel = sc.v().minus(ps.get_abs_vel());	//* assuming inertial frame
        return rel;
    }

    /**
     * Get the relative position between this and another member spacecraft in the
     * formation.
     * @see jat.spacecraft.MemberSpacecraft#get_rel_pos(jat.spacecraft.MemberSpacecraft, jat.spacecraft.PrimarySpacecraft)
     */
    //* assuming relative positions measured inertially
    public VectorN get_rel_pos(MemberSpacecraft ms, PrimarySpacecraft ps) {
        VectorN rel = ps.get_abs_pos().plus(ms.get_rel_pos(ps)).minus(sc.r());
        return rel;
    }

    /**
     * Get the relative velocity between this and another member spacecraft in the
     * formation.
     * @see jat.spacecraft.MemberSpacecraft#get_rel_vel(jat.spacecraft.MemberSpacecraft, jat.spacecraft.PrimarySpacecraft)
     */
    //* assuming relative velocities measured inertially
    public VectorN get_rel_vel(MemberSpacecraft ms, PrimarySpacecraft ps) {
        VectorN rel = ps.get_abs_vel().plus(ms.get_rel_vel(ps)).minus(sc.v());
        return rel;
    }

//    /**
//     * Sets the onboard computer's timestep for online integration.
//     * @param dt Integration timestep
//     */
//    public void set_sc_dt(double dt){
//    	this.sc_dt=dt;
//    }
//    /**
//     * Returns the current timestep used by the onboard computer for integration.
//     * @return Computer timestep
//     */
//    public double get_sc_dt(){
//    	return this.sc_dt;
//    }
    
    /**
     * Send a command to the indicated member spacecraft.
     * @see jat.spacecraft.MemberSpacecraft#send_control(double, jat.spacecraft.MemberSpacecraft)
     */
    public void send_control(double distance, MemberSpacecraft s) {
        // TODO Auto-generated method stub
        //* Do stuff
    }

    /**
     * Send a command to the indicated member spacecraft.
     * @see jat.spacecraft.MemberSpacecraft#send_control(jat.matvec.data.VectorN, jat.spacecraft.PrimarySpacecraft)
     */
    public void send_control(VectorN pos, PrimarySpacecraft s) {
        // TODO Auto-generated method stub
        //* Do stuff        
    }

    /**
     * Return the state vector containing the relative state.
     * @param ps Primary Spacecraft
     * @return Relative State
     * @see jat.spacecraft.MemberSpacecraft#get_rel_state(jat.spacecraft.PrimarySpacecraft)
     */
    public double[] get_rel_state(PrimarySpacecraft ps) {
        VectorN pos = this.get_rel_pos(ps);
        VectorN vel = this.get_rel_vel(ps);
        sc.set_RIC_frame(ps.get_abs_pos(),ps.get_abs_vel());
        VectorN state = sc.RIC.transform(pos,vel);
        return state.x;
    }

    /**
     * Apply an (instantaneous) delta-v to the spacecraft motion.
     * @param dv Delta-v 3-vector [m/s]
     */
    public void applyDeltaV(VectorN dv){
    	sc.v = (sc.v).plus(dv);
    }

    public double[] derivs(double t, double[] x) {
//		* Update spacecraft
        this.update(t,x);
        spacetime.update(t);
        //* Get non-control derivatives
        double[] xdot = spacetime.derivs(t,this.get_spacecraft());
        //* Get control derivatives
        //double[] xdot2 = this.control_thrust_derivs(t,xdot);
       
        //NOTE:  as of last revision, this was not correct.  
        //A more thorough was of adding controls may be required
        //* Compile derivatives
        //double[] out = MathUtils.plus(xdot,xdot2);
        double[] out = xdot;
        return out;
	}

    /**
     * Returns the (estimated) number of seconds since epoch time.
     * @return seconds since epoch
     */
	public double get_sc_t() {
		return spacetime.time.get_sim_time();
	}
}
