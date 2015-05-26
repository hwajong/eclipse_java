import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class App {
	
	final int WIN_WIDTH = 660; // ��ü frame�� ��
	final int WIN_HEIGHT = 700; // ��ü frame�� ����

	JFrame mainFrame = new JFrame(); // ��ü GUI�� ���� ������
	JPanel controlPanel = new JPanel(); // ���� ��Ʈ���� �� �г�
	
	JPanel coverPanel; // �ʱ�ȭ���� ��Ÿ�� �г�
	JPanel menualPanel; // �ʱ�ȭ���� ��Ÿ�� �г�
	
	JLabel lbUserId = new JLabel("User ID");
	JTextField tfUserId = new JTextField(5);   // user id
	JButton btMenual = new JButton("�޴���"); 
	JButton btStart = new JButton("����");   
	JButton btRank = new JButton("��ŷ");    
	
	int score = 0;
	int level = 0;
	
	// ��ư ����� ���� ��Ʈ ���꿡 ���� �����
	private final int START = 1;
	private final int MANUAL = 2;
	private final int RANK = 4;

	
	JLayeredPane lp = new JLayeredPane(); // ȭ���� ������ ��ġ�� ���� PaneL ���̾�
	
	RankPanel rankPanel; // ranking
	GamePanel gamePanel; // ������ �̷��� �г�
	int gamePanelWidth, gamePanelHeight; // ���� ������ �̷���� ������ ũ��
	
	Timer timerAnim;  // �׷��� ��ü�� �������� �����ϱ� ���� Ÿ�̸�
	Timer timerClock; // �ʴ��� Ÿ�̸� 
	
	StringShape levelShape = new StringShape("LV: 0", 10, 20);
	StringShape scoreShape = new StringShape("Score: 0", 300, 20);
	List<Shape> ballList = new ArrayList<Shape>(); // ����ü 
	Shape player; // Ű����� �����̴� Player ��ü
	
	private AudioClip backgroundSound; // ���� ��� ����
	private AudioClip boomSound; // �浹����

	
	public static void main(String argv[]) {
		new App().init();
	}

	public void init() {
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		controlPanel.add(lbUserId);
		controlPanel.add(tfUserId);
		controlPanel.add(btMenual);
		controlPanel.add(btStart);
		controlPanel.add(btRank);
		
		// ������ ������ ���÷��� �� �г�
		gamePanel = new GamePanel();
		gamePanel.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT);
		
		rankPanel = new RankPanel();
		rankPanel.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT);
		
		coverPanel = new coverPanel();
		coverPanel.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT);
		
		menualPanel = new manualPanel();
		menualPanel.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT);
		
		lp.add(coverPanel, new Integer(0));
		lp.add(menualPanel, new Integer(0));
		lp.add(gamePanel, new Integer(0));
		lp.add(rankPanel, new Integer(0));
		
		timerClock = new Timer(1000, new ClockListner());
		timerAnim = new Timer(10, new AnimeListener()); // �׸��� �̵��� ó���ϱ� ���� ������
		
		
		player = new Shape(getClass().getResource("player.png"), gamePanelWidth, gamePanelHeight);		
		
		// ��ư �������� ��ġ
		btMenual.addActionListener(new MenualListener());
		btStart.addActionListener(new StartListener());
		btRank.addActionListener(new RankListener());
		gamePanel.addKeyListener(new DirectionListener()); // Ű���� ������ ��ġ
		tfUserId.addKeyListener(new UserInputListener());
		
		// ������ ���� ���� ���� ��ġ
		try {
			backgroundSound = JApplet.newAudioClip(getClass().getResource("start.wav"));
			boomSound = JApplet
					.newAudioClip(getClass().getResource("boom.wav"));

		} catch (Exception e) {
			System.out.println("���� ���� �ε� ����");
		}

		buttonOff(MANUAL+START); // Ȱ��ȭ�� ��ư�� ����
		buttonOn(MANUAL); // Ȱ��ȭ�� ��ư�� ����

		// ��ü �����ӿ� ��ġ
		//todo -------------- mainFrame.add(lp);
		mainFrame.add(BorderLayout.CENTER, lp);
		mainFrame.add(BorderLayout.SOUTH, controlPanel);
		
		mainFrame.setSize(WIN_WIDTH, WIN_HEIGHT);
		mainFrame.setVisible(true);
		
		// ������ �̷���� �г��� ���� ���� ���� ���
		gamePanelWidth = gamePanel.getWidth();
		gamePanelHeight = gamePanel.getHeight();
		gamePanelHeight -= controlPanel.getHeight();
		gamePanelHeight -= 20; 

		player.setY(gamePanelHeight-38);
		player.setX(gamePanelWidth/2 - 25);
	}
	
	// ������ ����Ǵ� ���� �г�
	@SuppressWarnings("serial")
	class GamePanel extends JPanel {
		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight()); // ȭ�� �����

			player.draw(g, this);
			
			for (Shape s : ballList) {
				s.draw(g, this);
			}
			
			levelShape.draw(g);
			scoreShape.draw(g);
		}
	}
	
	@SuppressWarnings("serial")
	class RankPanel extends JPanel {
		
		TreeMap<Integer, String> rankingMap = new TreeMap<Integer, String>();
		
		public RankPanel() {
			rankingMap.clear();
		}
		
		public void bid(boolean addNew) {
			// read score data from file
			try {
				Scanner file = new Scanner(new File("rank.dat"));
				String line;
				while (file.hasNext()) {
					line = file.nextLine();
					//System.out.println(line);
					String[] strs = line.split("@@@");
					// System.out.println(strs[0]);
					// System.out.println(strs[1]);
					rankingMap.put(Integer.parseInt(strs[1]), strs[0]);
				}
				file.close();
			} catch (FileNotFoundException e1) {
				//e1.printStackTrace();
			}
			
			if(addNew) {
				rankingMap.put(score, tfUserId.getText());	
			}
			
			// save score data to file
			try {
				FileWriter outFile = new FileWriter(new File("rank.dat"));
				int count = 0;
				for(Integer score : rankingMap.descendingKeySet()) {
					String userId = rankingMap.get(score);
					outFile.write(userId + "@@@" + score.toString() + "�n");
					if(++count >= 10) break; // ���� 10���� ���� 
				}
				outFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight()); // ȭ�� �����

			player.draw(g, this);
						
			levelShape.draw(g);
			scoreShape.draw(g);
			
			g.setFont(new Font("TimesRoman", Font.BOLD, 40));
		    g.setColor(Color.black);
		    int x = 100;
		    int y = 80;
		    
		    g.drawString("Rank     Name     Score", x, y);
		    
		    int rank = 1;
		    for(Integer score : rankingMap.descendingKeySet()) {
		    	String userId = rankingMap.get(score);
		    	x = 115;
		    	y += 50;
		    	g.drawString(String.format("%-2d.", rank), x, y);
		    	x = 245;
		    	g.drawString(String.format("%-10s", userId), x, y);
		    	x = 400;
		    	g.drawString(String.format("%-10d", score.intValue()), x, y);
		    	rank++;
		    	if(rank > 10) break; // ��ŷ 10���� ǥ�� 
		    }
		}
		
	}
	
	private void makeBall() {
		URL imgURL = getClass().getResource("ball.png");
		int ballSize = 10;
		ballList.add(
				new Ball(imgURL, 
						(int)(ballSize*1.5), 
						5 + level, 
						gamePanelWidth-ballSize, 
						gamePanelHeight-ballSize));
	}
	
	// ��ư�� Ȱ�� ��Ȱ��ȭ�� ���� ��ƾ
	private void buttonOn(int flags) {
		if ((flags & START) == START) btStart.setEnabled(true);
		if ((flags & MANUAL) == MANUAL) btMenual.setEnabled(true);
		if ((flags & RANK) == RANK) btRank.setEnabled(true);
	}

	
	private void buttonOff(int flags) {
		if ((flags & START) == START) btStart.setEnabled(false);
		if ((flags & MANUAL) == MANUAL) btMenual.setEnabled(false);
		if ((flags & RANK) == RANK) btRank.setEnabled(false);
	}
	
	
	// ������ ����� ó���ؾ� �� ����
	private void finishGame() {
		backgroundSound.stop(); // ���� ����
		timerClock.stop(); // �ð� ���ÿ��� ����
		timerAnim.stop(); // �׸���ü ������ ����
		gamePanel.setFocusable(false); // ��Ŀ�� �ȵǰ� ��(�� Ű �ȸ���)
		buttonOn(START);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		rankPanel.bid(true);
		lp.moveToFront(rankPanel);
		buttonOn(MANUAL);
	}


	// ���� ��ư�� ��û��
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			buttonOff(RANK);
			lp.moveToFront(gamePanel);
			gamePanel.setDoubleBuffered(true);
			gamePanel.setFocusable(true); // gamePanel�� ��Ŀ�̵� �� �ְ� ��
			gamePanel.requestFocus(); // ��Ŀ���� ������(�̰� �ݵ�� �ʿ�)
			
			level = 0;
			levelShape.setString("LV: 0");
			
			score = 0;
			scoreShape.setString("Score: 0");
			
			ballList.clear();

			backgroundSound.loop();
			timerClock.start();
			timerAnim.start(); // �׸���ü �������� ���� ����

			buttonOff(MANUAL+START); // Ȱ��ȭ�� ��ư�� ����
			tfUserId.setEnabled(false);
			
			mainFrame.repaint(); // ȭ���� �ٽ� ���÷��� �ǰ� ��
		}
	}
	
	// �޴��� ��ư�� ��û��
	class MenualListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			lp.moveToFront(menualPanel);
			buttonOn(RANK);
		}
	}
	
	class RankListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			rankPanel.bid(false);
			lp.moveToFront(rankPanel);
		}
	}
	
	class UserInputListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			if(tfUserId.getText().isEmpty()) {
				buttonOff(START); // Ȱ��ȭ�� ��ư�� ����	
			}
			else {
				buttonOn(START); // Ȱ��ȭ�� ��ư�� ����
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}
	
	// goAnime Ÿ�̸ӿ� ���� �ֱ������� ����� ����
	// ��ü�� ������, �浹�� ���� ����
	private class AnimeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			// ���� �浹�Ͽ����� �浹�� ȿ���� ��Ÿ���� Ÿ�̸Ӹ� �ߴܽ�Ŵ
			for (Shape s : ballList) {
				if (s.collide(new Point((int)player.x, (int)player.y))) {
					boomSound.play(); // �浹�� ����
					finishGame(); // ���� �ߴ�
					return;
				}
			}

			// �׸� ��ü���� �̵���Ŵ
			for (Shape s : ballList) {
				s.move();
			}
			
			// ���� ���
			int x_min = (int)(player.getX() - 10);
			if(x_min < 0) x_min = 0;
			int x_max = (int)(player.getX() + 10);
			if(x_max > WIN_WIDTH) x_max = WIN_WIDTH;
			for (Shape s : ballList) {
				if(s.getX() >= x_min && s.getX() <= x_max) {
					score += 10;
					scoreShape.setString("Score: " + score);
				}
			}
						
			// System.out.println("event");
			mainFrame.repaint();
		}
	}
	
	private class ClockListner implements ActionListener {
		int times = 0;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			times++;
			
			// �ð��� �����ð� ������ �߰� �� ���� 
			if(times % 2 == 0) {
				makeBall();
				levelShape.setString("LV: " + ++level);
			}
			
			//System.out.println("times : " + times);
		}
	}
	
	// Ű���� �������� ��û�ϴ� ��û��
	private class DirectionListener implements KeyListener {
		
		int steps = 15;
		
		public void keyPressed(KeyEvent event) {
			switch (event.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (player.x >= 0)
					player.x -= steps;
				break;
			case KeyEvent.VK_RIGHT:
				if (player.x <= gamePanelWidth-30)
					player.x += steps;
				break;
			}
		}
		
		public void keyTyped(KeyEvent event) {
		}

		public void keyReleased(KeyEvent event) {
		}
	}
	
	// �ʱ�ȭ���� ��Ÿ���� �г�
	@SuppressWarnings("serial")
	class coverPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource("init.png"))
					.getImage();
			g.drawImage(image, 0, 0, this);
		}
	}

	// �޴����� ��Ÿ���� �г�
	@SuppressWarnings("serial")
	class manualPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource("manual.png"))
					.getImage();
			g.drawImage(image, 0, 0, this);
		}
	}

}


