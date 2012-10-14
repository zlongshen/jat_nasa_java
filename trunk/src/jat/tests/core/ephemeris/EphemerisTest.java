package jat.tests.core.ephemeris;

import jat.core.ephemeris.DE405APL;
import jat.core.util.messageConsole.MessageConsole;
import jat.coreNOSA.math.MatrixVector.data.VectorN;
import jat.coreNOSA.spacetime.Time;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class EphemerisTest extends JApplet {

	private static final long serialVersionUID = 4507683576803709168L;

	public void init() {

		// Create a text pane.
		JTextPane textPane = new JTextPane();
		JScrollPane paneScrollPane = new JScrollPane(textPane);
		paneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		paneScrollPane.setPreferredSize(new Dimension(250, 155));
		paneScrollPane.setMinimumSize(new Dimension(10, 10));
		getContentPane().add(paneScrollPane, BorderLayout.CENTER);

		// Redirect stdout and stderr to the text pane
		MessageConsole mc = new MessageConsole(textPane);
		mc.redirectOut();
		mc.redirectErr(Color.RED, null);
		mc.setMessageLines(100);
		System.out.println("Ephemeris Test");

	}

	public void start() {
		System.out.println("Creating Ephemeris Test Applet");
		EphemerisTestConsole E = new EphemerisTestConsole();
		JFrame jf = new JFrame();
		jf.setSize(500, 400);
		jf.getContentPane().add(E);
		jf.setVisible(true);
		E.init();
		System.out.println("Ephemeris Console created");

		
		
		Time mytime = new Time(2002, 2, 17, 12, 0, 0);
		System.out.println("Loading DE405 Ephemeris File");
		DE405APL ephem = new DE405APL();
		VectorN rv;
		try {
			rv = ephem.get_planet_posvel(DE405APL.body.MARS, mytime.jd_tt());
			System.out.println("The position of Mars on 10-17-2002 at 12:00pm was ");
			System.out.println("x= " + rv.get(0) + " km");
			System.out.println("y= " + rv.get(1) + " km");
			System.out.println("z= " + rv.get(2) + " km");
			System.out.println("The velocity of Mars on 10-17-2002 at 12:00pm was ");
			System.out.println("vx= " + rv.get(3) + " km/s");
			System.out.println("vy= " + rv.get(4) + " km/s");
			System.out.println("vz= " + rv.get(5) + " km/s");
		} catch (IOException e) {
			System.out.println("Failed to get planet position velocity in get_planet_posvel()");
			e.printStackTrace();
		}



	}

}
