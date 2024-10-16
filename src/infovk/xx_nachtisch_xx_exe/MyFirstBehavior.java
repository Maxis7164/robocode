package infovk.xx_nachtisch_xx_exe;

import java.awt.*;

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
		drive();
		shoot();
	}

	
	//#region utils
	double getEnemyAngle(double heading) {
		double own = getHeading() - heading;
		double enemy = e.getBearing();

		return normalRelativeAngle((own + enemy)) * 1.1;
	}

	double getFirePower() {
		double dist = Math.abs(e.getDistance());

		return dist == 60 ? 3 : min(100 / ( dist - 60 ), 3);
	}

	double getFuture() {
		Point enemy = Point.fromPolarCoordinates(getEnemyAngle(getGunHeading()), e.getDistance());

		paintDot(enemy, Color.MAGENTA);

		return 0.0;
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

	void escape() {
		double enemyAngle = getEnemyAngle(getHeading());

		if (hasHitWall()) {
			turn(90);
			ahead(-50);
		} else if (getTurnRemaining() == 0) {
			turn(-enemyAngle);
			ahead(50);
		};

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
			escape();
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
		turnGun(gunAngle);

		//calc shoot
		if (getGunHeat() > 0 || e.getDistance() > 300) return;

		if (gunAngle <= MAX_SHOOT_ANGLE) {
			double power = getFirePower();
			fireBullet(power);
		}

	}
}
