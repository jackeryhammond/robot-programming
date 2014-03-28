package ex3;
/**
 *  optical light sensor stopping robot before a blockage
 */
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class Grid {

	private static RegulatedMotor m_left;
	private static RegulatedMotor m_right;
	private static LightSensor light_l;
	private static LightSensor light_r;
	private static DifferentialPilot pilot;
	private static OpticalDistanceSensor dist;
	private boolean go;

	public Grid(RegulatedMotor _left, RegulatedMotor _right, SensorPort s1,
			SensorPort s4, SensorPort s2) {
		super();
		m_left = _left;
		m_right = _right;
		light_l = new LightSensor(s1);
		light_r = new LightSensor(s4);
		dist = new OpticalDistanceSensor(s2);
		pilot = new DifferentialPilot(56, 143, m_left, m_right);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("grod");
		Grid robot = new Grid(Motor.B, Motor.C, SensorPort.S1, SensorPort.S4,
				SensorPort.S2);

		Button.waitForAnyPress();
		pilot.setTravelSpeed(80);
		robot.run();

		Button.waitForAnyPress();

	}

	
	private void go() { // helper
		go = true;
	}

	public void run() {
		while (dist.getDistance() > 200) {
			go();
			runF();
			Delay.msDelay(200);
		}
		pilot.stop();
	}

	public void runL() { // turns left at junction
		/*
		 * while(true){ System.out.println("left: " + light_l.readValue());
		 * System.out.peckrintln("right: " + light_r.readValue());
		 * Delay.msDelay(3000); }
		 */

		while (go) {
			pilot.forward();
			Delay.msDelay(1);
			if (checkGrid()) { // checks to see if both sensors have been
								// activated (crossed a grid)
				left(); // turns left
			} else if (checkLeft()) { // line
				fixLeft(); // following
			} else if (checkRight()) { // code
				fixRight();
			}
		}

	}

	public void runR() { // turns right at junction

		while (go) {
			pilot.forward();
			Delay.msDelay(1);
			if (checkGrid()) {
				right();
			} else if (checkLeft()) {
				if (light_l.readValue() < 40) {
					while (light_l.readValue() < 43) {
						m_right.forward();
						Delay.msDelay(1);
						m_right.stop();
					}
					fixLeft();
				} else if (checkRight()) {
					fixRight();
				}
			}
		}
	}

	public void runF() { // goes over junction

		while (go) {
			pilot.forward();
			Delay.msDelay(1);
			if (checkGrid()) {
				forward();
			} else if (checkLeft()) {
				fixLeft();
			} else if (checkRight()) {
				fixRight();
			}
		}
	}

	private boolean checkLeft() { // checks for line
		return light_l.readValue() < 40;
	}

	private boolean checkRight() { // checks for line
		return light_r.readValue() < 35;
	}

	private boolean checkGrid() { // checks for junction
		return (light_l.readValue() < 40) && (light_r.readValue() < 35);
	}

	private void left() { // left turn
		pilot.forward();
		Delay.msDelay(750);
		pilot.rotate(100);
		go = false;
	}

	private void right() { // right turn
		pilot.forward();
		Delay.msDelay(750);
		pilot.rotate(-100);
		go = false;
	}

	private void forward() { // ignore junction
		pilot.forward();
		Delay.msDelay(750);
		go = false;
	}

	private void fixLeft() { // line following WHILE not at junction
		while (light_l.readValue() < 43 && !(light_r.readValue() < 35)) {
			m_right.forward();
			Delay.msDelay(1);
			m_right.stop();
		}
	}

	private void fixRight() { // line following WHILE not at junction
		while (light_r.readValue() < 40 && !(light_l.readValue() < 40)) {
			m_left.forward();
			Delay.msDelay(1);
			m_left.stop();
		}
	}
}
