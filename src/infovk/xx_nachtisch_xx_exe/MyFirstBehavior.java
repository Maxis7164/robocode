package infovk.xx_nachtisch_xx_exe;

import java.awt.*;
import java.lang.reflect.Array;

import static infovk.xx_nachtisch_xx_exe.Utils.*;
import static infovk.xx_nachtisch_xx_exe.Wrappers.*;

public class MyFirstBehavior extends SimpleRobotBehavior {
	double DIST_TO_ENEMY = 50.0;
	double SIZE = 36;

	double targetAngle = 0.0;

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
	//#endregion

	void scan() {
		if (!hasScannedRobot()) return;

		ScannedRobotEvent e = getScannedRobotEvent();

		double lastEnemyAngle = getEnemyAngle(e, getRadarHeading());

		turnRadar(lastEnemyAngle);
	}

	void drive() {
		if (!hasScannedRobot()) return;

		ScannedRobotEvent e = getScannedRobotEvent();

		targetAngle = getEnemyAngle(e, getHeading());
		System.out.println(targetAngle);
		turn(targetAngle);

		if (e.getDistance() > SIZE + DIST_TO_ENEMY) ahead(10);
		else {
			ahead(-10);
		}
	}


	void shoot() {
		if (getGunHeat() > 0 || !hasScannedRobot()) return;

		ScannedRobotEvent e = getScannedRobotEvent();

		double gunAngle = getEnemyAngle(e, getGunHeading());

		turnGun(gunAngle);

		fireBullet(0.1);

	}
}
