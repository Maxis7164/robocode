package infovk.xx_nachtisch_xx_exe;

import static infovk.xx_nachtisch_xx_exe.Utils.*;

public class MyFirstBehavior extends SimpleRobotBehavior {
	double MIN_TARGET_DISTANCE = 300.0;
	double MAX_SHOOT_ANGLE = 1.33;

	double STEP = 10;

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

	double getFirePower(ScannedRobotEvent e) {
		double dist = Math.abs(e.getDistance());

		System.out.println(dist);

		return dist == 60 ? 3 : min(100 / ( dist - 60 ), 3);
	}
	//#endregion

	//#region diving behaviors
	void targetEnemy(ScannedRobotEvent e) {
		double targetAngle = getEnemyAngle(e, getHeading());
		turn(targetAngle);

		double power = STEP;

		ahead(power);
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
		double dist = e.getDistance();

		if (dist >= MIN_TARGET_DISTANCE) targetEnemy(e);
	}


	void shoot() {
		if (!hasScannedRobot()) return;

		ScannedRobotEvent e = getScannedRobotEvent();

		//get angle & rotate to enemy's pos
		double gunAngle = getEnemyAngle(e, getGunHeading());
		turnGun(gunAngle);

		//calc shoot
		if (getGunHeat() > 0 || e.getDistance() > 300) return;

		if (gunAngle <= MAX_SHOOT_ANGLE) {
			double power = getFirePower(e);
			fireBullet(power);
		}

	}
}
