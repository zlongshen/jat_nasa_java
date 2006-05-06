package jat.measurements;


import java.util.HashMap;

import jat.sim.*;
import jat.gps.Visible;
import jat.matvec.data.*;
import java.util.Random;
import jat.alg.estimators.*;

public class stateMeasurementModel implements MeasurementModel{
	
	public static VectorN R;
	public static int numStates;
	HashMap hm = closedLoopSim.hm;
	Random generator;
	
	public stateMeasurementModel() {
		/*Add a sleep in here to insure that the Random Number
		 * Seeds don't allign with any other random number generator
		 */
		try {
			Thread.sleep(20);
			generator = new Random();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	public VectorN getMeasurement()
	{
		String tmp;
		VectorN range = new VectorN(12);
		double[] truth0 = closedLoopSim.truth[0].sc.get_spacecraft().toStateVector();
		double[] truth1 = closedLoopSim.truth[1].sc.get_spacecraft().toStateVector();
		VectorN sat0 = new VectorN(truth0);
		VectorN sat1 = new VectorN(truth1);
		range.set(0,sat0);
		range.set(6,sat1);
		
		//Add in the measurement noise read out of the file
		for(int j = 0; j < 12; j++)
		{
			tmp = "MEAS."+0+".R."+j;
			double R = initializer.parseDouble(hm,tmp);
			
			/*Scale the error as Gaussian noise times the 
			 square of the measurement noise*/
			range.x[j] += generator.nextGaussian()*R*R; 
		}	
		
		return range;
	}
	
	public VectorN predictMeasurement(VectorN state){
		
		VectorN range = new VectorN(12);
		range = state.get(0,12);
		
		
		return range;
		
	}
	
	public double  zPred(int i, double time, VectorN state){
		VectorN oMinusC;
		VectorN pred = predictMeasurement(state);
		VectorN obs  = getMeasurement();
		oMinusC      = obs.minus(pred);
		return oMinusC.get(i);
	}
	
	public double R()
	{
		int whichState = EKF.stateNum;
		int satNum     = EKF.satNum;
		String tmp = "MEAS."+satNum+".R."+whichState;
		double R = initializer.parseDouble(hm,tmp);
		return R;
	}
	
	public VectorN H(VectorN state)
	{
		/*Determine the number of states*/
		int whichState = EKF.stateNum;
		int numStates = initializer.parseInt(hm,"FILTER.states");
	
		/*for a Range measurement, the current state has H = 1, all other states H = 0 */
		VectorN H = new VectorN(numStates);
		H.set(0.0);
		H.set(whichState,1.0);
		return H;
	}

}