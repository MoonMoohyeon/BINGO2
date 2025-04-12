package Project;

public class ProjectMain {

	public static void main(String[] args) {
		System.out.println("201910162 문무현");

		GamePlay game = new GamePlay("quiz.txt");
//		game.rateFileOut();
//		game.rateFileIn();
//		System.out.println(game.wincount);
//		System.out.println(game.losecount);
//		System.out.println(game.drawcount);
		game.play();
	}

}
