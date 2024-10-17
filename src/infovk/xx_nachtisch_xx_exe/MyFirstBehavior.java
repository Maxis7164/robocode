package infovk.xx_nachtisch_xx_exe;

import static infovk.xx_nachtisch_xx_exe.Utils.*;

public class MyFirstBehavior extends SimpleRobotBehavior {
	double MIN_TARGET_DISTANCE = 200.0;
	double DANGER_DISTANCE = 100.0;
	double MAX_SHOOT_ANGLE = 1.33;


	double wall_distance = 60;
	double wall_distance_safe = 100;
	boolean safe = true;
	double wall_time = 0;
	// boolean change = true;

	int z = 0;



	double STEP = 10;

	int escapeTimeLeft = 0;
//	boolean canescape = true;

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
	//#endregion

	//#region diving behaviors
	void targetEnemy() {
		double targetAngle = getEnemyAngle(getHeading());
		turn(targetAngle);

		double power = STEP;

		ahead(power);
	}

	void circleAround() {


		if (checkifsurpassing())
		{
			turn(90);
			ahead(-40);

			//circleDir = circleDir * -1;

			//ahead(circleDir * 20);

		}
		else if (e != null)
		{
			double to = normalRelativeAngle(e.getBearing() - 90);
			turn(to);
		}


	}

	void escape() {

		double enemyAngle = getEnemyAngle(getHeading());

		if (checkifsurpassing())
		{
			turn(90);
			ahead(-50);
			System.out.println("über Grenze");
		}
		else
		{
			turn(-enemyAngle);
			ahead(40);
		}


	}

	void checkifsafe() {


		if ((((getY() >= (getBattleFieldHeight() - wall_distance))
				|| (getY() <= wall_distance)
				|| (getX() <= wall_distance)
				|| (getX() >= (getBattleFieldWidth() - wall_distance)))) && safe) safe = false;
		else
		if ((getY() <= (getBattleFieldHeight() - wall_distance_safe))
				&& (getY() >= wall_distance_safe)
				&& (getX() >= wall_distance_safe)
				&& (getX() <= (getBattleFieldWidth() - wall_distance_safe)) && !safe){
			safe = true;
			wall_time = 0;
		}

	}

	boolean checkifsurpassing () {

		if (((getY() >= (getBattleFieldHeight() - wall_distance))
				|| (getY() <= wall_distance)
				|| (getX() <= wall_distance)
				|| (getX() >= (getBattleFieldWidth() - wall_distance)))) return true;

		else return false;
	}

	void escapebond(){

		if(getY() > (getBattleFieldHeight()-wall_distance) && getHeading() > 270 && getHeading() < 90 )
		{
			turn(180);
			ahead(80);
		}
		else if (getY() < wall_distance && getHeading() > 90 && getHeading() <270)
		{
			turn(180);
			ahead(80);
		}
		else if (getX() < wall_distance && getHeading() > 180 && getHeading() <360)
		{
			turn(180);
			ahead(80);
		}
		if(getX() > (getBattleFieldWidth()-wall_distance) && getHeading() > 0 && getHeading() < 180 )
		{
			turn(180);
			ahead(80);
		}
		else
		{
			turn(180);
			ahead(80);
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

		if (dist <= DANGER_DISTANCE && escapeTimeLeft == 0)
		{
			escapeTimeLeft = randomInteger(3, 8);
			// System.out.println(escapeTimeLeft);
		}

		else if (escapeTimeLeft > 0 && safe)
		{
			escape();
			escapeTimeLeft--;
		}

		else if(dist >= MIN_TARGET_DISTANCE)
		{
			targetEnemy();
		}
		else if(dist <= MIN_TARGET_DISTANCE && safe)
		{
			circleAround();
		}
		else if(!safe && wall_time != 0)
		{
			wall_time = 50;
			escapebond();
		}
		else
			circleAround();
	}


	void shoot() {
		if (!hasScannedRobot()) return;

		//get angle & rotate to enemy's pos
		double gunAngle = getEnemyAngle(getGunHeading());
		double power = getFirePower();
		turnGun(gunAngle);

		//calc shoot
		if (getGunHeat() > 0 || e.getDistance() > 300) return;

		if (gunAngle <= MAX_SHOOT_ANGLE) {
			fireBullet(power);
		}

	}
}
