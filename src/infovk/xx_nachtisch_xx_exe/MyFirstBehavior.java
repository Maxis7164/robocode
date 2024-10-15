package infovk.xx_nachtisch_xx_exe;

import java.awt.*;

import static infovk.xx_nachtisch_xx_exe.Utils.*;
import static infovk.xx_nachtisch_xx_exe.Wrappers.*;


public class MyFirstBehavior extends SimpleRobotBehavior {
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

		shoot();
	}

	double getEnemyAngle(ScannedRobotEvent e, double heading) {
		double own = getHeading() - heading;
		double enemy = e.getBearing();

		return normalRelativeAngle((own + enemy)) * 1.1;
	}

	void scan() {
		if (!hasScannedRobot()) return;

		ScannedRobotEvent e = getScannedRobotEvent();

		double lastEnemyAngle = getEnemyAngle(e, getRadarHeading());

		turnRadar(lastEnemyAngle);
	}

	void shoot() {
		if (getGunHeat() > 0 || !hasScannedRobot()) return;

		ScannedRobotEvent e = getScannedRobotEvent();

		double gunAngle = getEnemyAngle(e, getGunHeading());

		turnGun(gunAngle);

		fireBullet(0.1);

	}
}
