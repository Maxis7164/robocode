package infovk.xx_nachtisch_xx.exe;

public class MyFirstBehavior extends SimpleRobotBehavior {
	//Gedächtnissektion
	
	public MyFirstBehavior(SimpleRobot  robot) {
		super(robot);
	}

	@Override
	public void start() {
		turnRadar(720);
	}

	@Override
	void execute() {
		turn(20);
	}


}
