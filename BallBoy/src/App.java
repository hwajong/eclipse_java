import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
	JButton btMenual = new JButton("�޴���"); // ���۹�ư
	JButton btStart = new JButton("����");   // ���۹�ư
	
	int score = 0;
	int level = 0;
	
	// ��ư ����� ���� ��Ʈ ���꿡 ���� �����
	private final int START = 1;
	private final int MANUAL = 2;

	
	JLayeredPane lp = new JLayeredPane(); // ȭ���� ������ ��ġ�� ���� PaneL ���̾�
	
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
		
		// ������ ������ ���÷��� �� �г�
		gamePanel = new GamePanel();
		gamePanel.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT);
		
		coverPanel = new coverPanel();
		coverPanel.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT);
		
		menualPanel = new manualPanel();
		menualPanel.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT);
		
		lp.add(coverPanel, new Integer(0));
		lp.add(menualPanel, new Integer(0));
		lp.add(gamePanel, new Integer(0));
		
		timerClock = new Timer(1000, new ClockListner());
		timerAnim = new Timer(10, new AnimeListener()); // �׸��� �̵��� ó���ϱ� ���� ������
		
		
		player = new Shape(getClass().getResource("player.png"), gamePanelWidth, gamePanelHeight);		
		
		// ��ư �������� ��ġ
		btMenual.addActionListener(new MenualListener());
		btStart.addActionListener(new StartListener());
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
	}

	
	private void buttonOff(int flags) {
		if ((flags & START) == START) btStart.setEnabled(false);
		if ((flags & MANUAL) == MANUAL) btMenual.setEnabled(false);
	}
	
	
	// ������ ����� ó���ؾ� �� ����
	private void finishGame() {
		backgroundSound.stop(); // ���� ����
		timerClock.stop(); // �ð� ���ÿ��� ����
		timerAnim.stop(); // �׸���ü ������ ����
		gamePanel.setFocusable(false); // ��Ŀ�� �ȵǰ� ��(�� Ű �ȸ���)
	}


	// ���� ��ư�� ��û��
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			lp.moveToFront(gamePanel);
			gamePanel.setDoubleBuffered(true);
			gamePanel.setFocusable(true); // gamePanel�� ��Ŀ�̵� �� �ְ� ��
			gamePanel.requestFocus(); // ��Ŀ���� ������(�̰� �ݵ�� �ʿ�)

			//backgroundSound.play(); // ������� ����
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
				if (s.collide(new Point(player.x, (int)player.y))) {
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
			int x_min = player.getX() - 10;
			if(x_min < 0) x_min = 0;
			int x_max = player.getX() + 10;
			if(x_max > WIN_WIDTH) x_max = WIN_WIDTH;
			for (Shape s : ballList) {
				if(s.getX() >= x_min && s.getX() <= x_max) {
					scoreShape.setString("Score: " + ++score);
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
				if (player.x <= gamePanelWidth)
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


