package infovk.xx_nachtisch_xx_exe;

import static infovk.xx_nachtisch_xx_exe.Utils.*;

public class MyFirstBehavior extends SimpleRobotBehavior {
	double MIN_TARGET_DISTANCE = 300.0;
	double DANGER_DISTANCE = 125.0;
	double MAX_SHOOT_ANGLE = 1.33;

	double STEP = 10;

	int escapeTimeLeft = 0;

	double circleDir = 1;

	ScannedRobotEvent e = null;

	public MyFirstBehavior(SimpleRobot robot) {
		super(robot);
	}

	@Override
	public void start() {
		turnRadar(720);
	}

	@Override
	void execute() {
		scan();
//		drive();
		shoot();
	}

	
	//#region utils
	double toRadians(double angle) {
		return angle / 180 * Math.PI;
	}

	double getEnemyAngle(double heading) {
		double own = getHeading() - heading;
		double enemy = e.getBearing();

		return normalRelativeAngle((own + enemy)) * 1.1;
	}

	double getFirePower() {
		double dist = Math.abs(e.getDistance());

		return dist == 60 ? 3 : min(100 / ( dist - 60 ), 3);
	}

	double getBulletVelocity(double power) {
		return 20 - 3 * power;
	}

	double calcAngleOfPoints(Point p1, Point p2) {
		double diff = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());

		return atan(diff);
	}

	double getNextEnemyPos(double power) {
		double heading = getHeading();
		double bearing = e.getBearing();
		double enemy = Math.toRadians(e.getHeading());
		double v = e.getVelocity();

		Point enemyPos = Point.fromPolarCoordinates(heading + bearing, e.getDistance()).add(getPoint());

		Point next = new Point(
			-Math.sin(enemy) * v * getBulletVelocity(power),
			Math.cos(enemy) * v * getBulletVelocity(power)
		);

		double bearingToGun = bearing - heading + getRadarHeading();

		int dir = (int)( bearingToGun / Math.abs(bearingToGun) );

		System.out.println(bearingToGun);

		return v == 0 ? 0 : dir * calcAngleOfPoints(next, enemyPos);
	}
	//#endregion

	//#region diving behaviors
	void targetEnemy() {
		double targetAngle = getEnemyAngle(getHeading());
		turn(targetAngle);

		double power = STEP;

		ahead(power);
	}

	void circleAround() {

		if (hasHitWall()) circleDir = circleDir * -1;
		ahead(circleDir * 20);

		if (e != null) {
			double to = normalRelativeAngle(e.getBearing() - 90);
			turn(to);
		}

	}
	//#endregion

	void scan() {
		if (!hasScannedRobot()) return;

		e = getScannedRobotEvent();

		double lastEnemyAngle = getEnemyAngle(getRadarHeading());

		turnRadar(lastEnemyAngle);
	}

	void drive() {
		if (!hasScannedRobot()) return;

		double dist = e.getDistance();

		if (escapeTimeLeft > 0.0) {
			escapeTimeLeft--;
		} else {
			if (dist >= MIN_TARGET_DISTANCE) targetEnemy();
			else if (dist <= DANGER_DISTANCE) escapeTimeLeft = randomInteger(5, 15);
			else circleAround();
		}
	}


	void shoot() {
		if (!hasScannedRobot()) return;

		//get angle & rotate to enemy's pos
		double gunAngle = getEnemyAngle(getGunHeading());
		double power = getFirePower();
		turnGun(gunAngle + getNextEnemyPos(power));

		//calc shoot
		if (getGunHeat() > 0 || e.getDistance() > 300) return;

//		if (gunAngle <= MAX_SHOOT_ANGLE) {
			fireBullet(power);
//		}

	}
}
