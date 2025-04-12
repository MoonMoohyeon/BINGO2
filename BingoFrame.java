package Project;

import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class BingoFrame extends JFrame {
	
	JLabel labels;
	
	BingoFrame(String title)
	{
		super(title);
		this.setSize(500, 500);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		init();
		this.setVisible(true);
	}

	private void init() {
		// TODO Auto-generated method stub
		
	}
}
