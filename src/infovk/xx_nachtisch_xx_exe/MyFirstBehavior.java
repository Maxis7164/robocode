package infovk.xx_nachtisch_xx_exe;

import java.awt.*;

import static infovk.xx_nachtisch_xx_exe.Utils.*;
import static infovk.xx_nachtisch_xx_exe.Wrappers.*;


public class MyFirstBehavior extends SimpleRobotBehavior {
	double lastEnemyAngle = 0;

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

	void scan() {
		if (!hasScannedRobot()) return;

		ScannedRobotEvent e = getScannedRobotEvent();

		double ownAngle = this.getHeading() - this.getRadarHeading();
		double enemyAngle = e.getBearing();

		lastEnemyAngle = normalRelativeAngle((ownAngle + enemyAngle)) * 1.1;

		turnRadar(lastEnemyAngle);
	}

	void shoot() {
		if (this.getGunHeat() > 0) return;


		turnGun(lastEnemyAngle);
		fireBullet(1);
	}
}
