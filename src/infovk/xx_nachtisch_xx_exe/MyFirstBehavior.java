package infovk.xx_nachtisch_xx_exe;

import java.awt.*;
import java.lang.reflect.Array;

import static infovk.xx_nachtisch_xx_exe.Utils.*;
import static infovk.xx_nachtisch_xx_exe.Wrappers.*;

public class MyFirstBehavior extends SimpleRobotBehavior {
	double STEP = 0.05;
	double SIN_MOD = 40;

	int GAP = 36;

	int SIZE_X = 800;
	int SIZE_Y = 600;
	
	double t = -1; // t-value [-1; 1]
	double dir = 1; // determines turning direction
	
	public MyFirstBehavior(SimpleRobot  robot) {
		super(robot);
	}

	@Override
	public void start() {
		turnRadar(720);
	}

	@Override
	void execute() {
		scan();
		drive();
		shoot();
	}

	
	//#region utils
	double getEnemyAngle(ScannedRobotEvent e, double heading) {
		double own = getHeading() - heading;
		double enemy = e.getBearing();

		return normalRelativeAngle((own + enemy)) * 1.1;
	}

	boolean heading(int dir) {
		if (dir < 0 || dir > 3) return false;
		double head = getHeading();

		if (dir == 0) return head > 315 || head < 45;
		else if (dir == 1) return head > 45 && head < 135;
		else if (dir == 2) return head > 135 && head < 225;
		else return head > 225 && head < 315;
	}

	void continueT() {
		t += dir * STEP;
		if (t >= 1) dir = -1;
		else if (t <= -1) dir = 1;
	}

	int avoidWalls() {
		double x = getX();
		double y = getY();

		return heading(0) && y >= 559 ||
		heading(2) && y <= 41 ||
		heading(1) && x >= 759 ||
		heading(3) && x <= 41 ? -1 : 1;
	}
	//#endregion

	void scan() {
		if (!hasScannedRobot()) return;

		ScannedRobotEvent e = getScannedRobotEvent();

		double lastEnemyAngle = getEnemyAngle(e, getRadarHeading());

		turnRadar(lastEnemyAngle);
	}

	void drive() {
		turn(Math.sin(t) * SIN_MOD);

		int dir = avoidWalls();

		ahead(dir * 50);
		continueT();
	}


	void shoot() {
		if (getGunHeat() > 0 || !hasScannedRobot()) return;

		ScannedRobotEvent e = getScannedRobotEvent();

		double gunAngle = getEnemyAngle(e, getGunHeading());

		turnGun(gunAngle);

		fireBullet(0.1);

	}
}
