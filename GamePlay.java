package Project;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class GamePlay extends JFrame{
	Scanner scan = new Scanner(System.in);
	ArrayList<Word> voc = new ArrayList<Word>();
	int N; // 빙고판 사이즈
	Word[][] userWord; // 유저 단어판
	Word[][] comWord; // 컴퓨터 단어판
	int bestX = 0, bestY = 0; // 컴퓨터 알고리즘 좌표
	int ccheck = 0, ucheck = 0; // 컴퓨터, 유저 선택 횟수
	int wincount = 0, losecount = 0, drawcount = 0; // 승률 저장용
	
	Container frame = this.getContentPane();
	JPanel panel1, panel2;
	JLabel[][] labels;
	JLabel text = new JLabel("원하는 단어를 입력하세요.");
	JTextField tb = new JTextField(15);
	JButton button = new JButton("확인");
	
	public GamePlay(String filename) {
		super("201910162 문무현 빙고 프로젝트");
		this.vocInit(filename);
		this.rateFileIn();
		this.setSize(800, 800);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void addWord(Word word)
	{
		voc.add(word);
	}
	
	private void makeVoc(String fileName) // 파일 읽어오기
	{ 
	
		try(Scanner scan = new Scanner(new File(fileName)))
		{
			while(scan.hasNextLine())
			{
				String str = scan.nextLine();
				String[] temp = str.split("\t");
				this.addWord(new Word(temp[0].trim(), temp[1].trim()));
			}
		}catch(FileNotFoundException e) { }
	}
		
	public void vocInit(String filename) // 보드 사이즈 입력받기
	{
		this.makeVoc(filename);
		while(N < 3 || N > 9 || voc.size() < N*N)
		{
			System.out.print("3~9 사이의 숫자를 입력하세요 : ");
			try{ N = scan.nextInt(); }
			catch(InputMismatchException e) { }
			if(N < 3 || N > 9)
			{
				System.out.println("3~9 사이의 숫자를 입력하세요.");
			}
			else if(voc.size() < N*N)
			{
				System.out.println("파일의 단어가 적습니다. 입력한 숫자보다 작은 수를 입력해 보세요.");
			}
			scan.nextLine();
		}
		this.userWord = new Word[N][N];
		this.comWord = new Word[N][N];
		this.boardInit();
		this.swingInit();
	}
	
	public void boardInit() // 보드판 랜덤 생성
	{
		int userArray[] = new int[N*N];
		int comArray[] = new int[N*N];
		Random r = new Random();
		for(int i=0; i<N*N; i++)
		{
			userArray[i] = r.nextInt(voc.size());
			for(int j=0; j<i; j++)
			{
				if(userArray[i] == userArray[j]) i--;

			}
		}
		
		for(int i=0; i<N*N; i++)
		{
			comArray[i] = r.nextInt(voc.size());
			for(int j=0; j<i; j++)
			{
				if(comArray[i] == comArray[j]) i--;

			}
		}
		
		int count = 0;
		for(int i=0; i<N; i++)
		{
			for(int j=0; j<N; j++)
			{
				userWord[i][j] = voc.get(userArray[count]);
				comWord[i][j] = voc.get(comArray[count++]);
			}
		}
	}
	
	void swingInit()
	{
		this.panel1 = new JPanel();
		this.panel2 = new JPanel();
		panel1.setLayout(new GridLayout(N, N, 5, 5));
		this.labels = new JLabel[N][N];
		for(int i=0; i<N; i++)
		{
			for(int j=0; j<N; j++)
			{
				labels[i][j] = new JLabel(userWord[i][j].eng, SwingConstants.CENTER);
				panel1.add(labels[i][j]);
			}
		}
		
		panel2.add(text);
		panel2.add(tb);
		panel2.add(button);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = text.getText();
				if(str.length() > 0)
				{
					System.out.println(str);
					GamePlay.this.userTurn(str);
				}
				panel1.revalidate();
				panel1.repaint();
			}
			
		});
		frame.add(panel2, BorderLayout.NORTH);
		frame.add(panel1);
	}
	
	void play() // 게임 플레이 함수
	{
		this.printBoard();
		while(true)
		{
			
			this.userTurn();
			this.computerTurn();
			int result = this.decision();
			this.printBoard();
			if(result == 0)
			{
				System.out.println("컴퓨터 승리 !");
				losecount++;
				this.printRate();
				this.rateFileOut();
				break;
			}
			else if(result == 1)
			{
				System.out.println("유저 승리 !");
				wincount++;
				this.printRate();
				this.rateFileOut();
				break;
			}
			else if(result == 2)
			{
				System.out.println("빙고 개수 같음 !");
			}
			else if(result == 3)
			{
				System.out.println("무승부 !");
				drawcount++;
				this.printRate();
				this.rateFileOut();
				break;
			}
		}
	}
	
	void printBoard()
	{
		System.out.println("User BingoBoard");
		for(int i=0; i<N; i++)
		{
			for(int j=0; j<N; j++)
			{	
				if(userWord[i][j].check == true)
					System.out.print(userWord[i][j].eng + "(O)" + " | ");
				else
					System.out.print(userWord[i][j].eng + " | ");
			}
			System.out.println();
		}
		
		System.out.println("\nComputer BingoBoard");
		for(int i=0; i<N; i++)
		{
			for(int j=0; j<N; j++)
			{
				if(comWord[i][j].check == true)
					System.out.print(comWord[i][j].eng + "(O)" + " | ");
				else
					System.out.print(comWord[i][j].eng + " | ");
			}
			System.out.println();
		}
	}
	
	void userTurn()
	{
		System.out.print("단어를 입력하세요 : ");
		String input = scan.nextLine();
		for(int i=0; i<N; i++)
		{
			for(int j=0; j<N; j++)
			{
				if(input.equals(userWord[i][j].eng))
				{
					System.out.println(userWord[i][j].eng + "(" + userWord[i][j].kor + ")"
							+ "을/를 선택하였습니다.");
					userWord[i][j].check = true;
					ucheck++;
				}
			}
		}
		
		for(int i=0; i<N; i++)
		{
			for(int j=0; j<N; j++)
			{
				if(input.equals(comWord[i][j].eng))
				{
					comWord[i][j].check = true;
					ccheck++;
				}
			}
		}
	}
	
	void userTurn(String input)
	{
//		System.out.print("단어를 입력하세요 : ");
//		String input = scan.nextLine();
		for(int i=0; i<N; i++)
		{
			for(int j=0; j<N; j++)
			{
				if(input.equals(userWord[i][j].eng))
				{
					System.out.println(userWord[i][j].eng + "(" + userWord[i][j].kor + ")"
							+ "을/를 선택하였습니다.");
					userWord[i][j].check = true;
					ucheck++;
				}
			}
		}
		
		for(int i=0; i<N; i++)
		{
			for(int j=0; j<N; j++)
			{
				if(input.equals(comWord[i][j].eng))
				{
					comWord[i][j].check = true;
					ccheck++;
				}
			}
		}
	}
	
	void computerTurn()
	{
		if(ccheck >= N*N) // 빙고판이 가득 찬 경우 선택하지 않음
			return;

		findBestPlay(); // arrX, arrY에 최적 수의 좌표가 담김.
		comWord[bestX][bestY].check = true;
		ccheck++;
		
		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < N; j++)
			{
				if (comWord[bestX][bestY].eng.equals(userWord[i][j].eng))
				{
					userWord[i][j].check = true;
					ucheck++;
					break;
				}
			}
		}
	}
	
	void findBestPlay()
	{

		int[] horizontal = new int[9];
		int[] vertical = new int[9];
		int[] diagnal = new int[2];

		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < N; j++)
			{
				if (comWord[i][j].check == true)
					horizontal[i]++;
				if (comWord[j][i].check == true)
					vertical[i]++;
			}

			if (comWord[i][i].check == true)
				diagnal[0]++;

			if (comWord[i][N - i - 1].check == true)
				diagnal[1]++;
		}
		
		int maxValue = 0; // 최대 평가치
		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < N; j++)
			{
				int temp = 1; // 평가치
				if (comWord[i][j].check == true)
					continue;

				if (i == j)
				{
					temp += 1 << (diagnal[0] + 1);

					if (diagnal[0] == N - 1)
						temp += 1 << (diagnal[0] + 1);
				}

				if ((N - i - 1) == j)
				{
					temp += 1 << (diagnal[1] + 1);

					if (diagnal[1] == N - 1)
						temp += 1 << (diagnal[1] + 1);
				}

				temp += 1 << horizontal[i];
				temp += 1 << vertical[j];
				if (horizontal[i] == N - 1) // 빙고 완성 직전
					temp += 1 << horizontal[i];
				if (vertical[j] == N - 1)
					temp += 1 << vertical[j];

				if (maxValue < temp)
				{
					bestX = i;
					bestY = j;
					
					maxValue = temp;
				}
			}
		}
	}
	
	int decision()
	{
		int userBingoCount = 0;
		int comBingoCount = 0;

		int rUser = 0;
		int cUser = 0;
		int clUser = 0;
		int crUser = 0;

		for (int i = 0; i < N; i++)
		{
			rUser = 0;
			cUser = 0;

			for (int j = 0; j < N; j++)
			{
				if (userWord[i][j].check == true)
					rUser++;
				if (userWord[j][i].check == true)
					cUser++;
			}
			if (userWord[i][i].check == true)
				clUser++;
			if (userWord[N - 1 - i][i].check == true)
				crUser++;

			if (rUser == N)
				userBingoCount++;
			if (cUser == N)
				userBingoCount++;
		}

		if (clUser == N)
			userBingoCount++;

		if (crUser == N)
			userBingoCount++;

		int rCom = 0;
		int cCom = 0;
		int clCom = 0;
		int crCom = 0;

		for (int i = 0; i < N; i++)
		{
			rCom = 0;
			cCom = 0;

			for (int j = 0; j < N; j++)
			{
				if (comWord[i][j].check == true)
					rCom++;
				if (comWord[j][i].check == true)
					cCom++;
			}
			if (comWord[i][i].check == true)
				clCom++;
			if (comWord[N - 1 - i][i].check == true)
				crCom++;

			if (rCom == N)
				comBingoCount++;
			if (cCom == N)
				comBingoCount++;
		}

		if (clCom == N)
			comBingoCount++;

		if (crCom == N)
			comBingoCount++;
		
		if (ccheck >= N*N && ucheck >= N*N) // 무승부 종료
			return 3;
		else if ((userBingoCount == comBingoCount) && (userBingoCount > 0) && (comBingoCount > 0)) // 동시 빙고, 무승부
			return 2;
		else if (userBingoCount > comBingoCount) // 유저 승
			return 1;
		else if (userBingoCount < comBingoCount) // 컴퓨터 승
			return 0;
		else
			return -1;
	}
	
	void rateFileOut()
	{
		try ( PrintWriter outfile = new PrintWriter(new File("rate.txt"));) {
			outfile.println(wincount);
			outfile.println(losecount);
			outfile.println(drawcount);
		} catch(FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
	void rateFileIn() // 승률 파일 입출력
	{
		try ( Scanner infile = new Scanner(new File("rate.txt"));) {
			wincount = infile.nextInt();
			losecount = infile.nextInt();
			drawcount = infile.nextInt();
		} catch(FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch(java.util.NoSuchElementException e)
		{ System.out.println(e.getMessage()); };
	}
	
	void printRate()
	{
		double rate = wincount / (double)(wincount + losecount + drawcount);
		System.out.println("누적 승률 : " + rate*100 + "%");
	}
}
