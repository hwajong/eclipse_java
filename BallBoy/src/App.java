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
	
	final int WIN_WIDTH = 660; // 전체 frame의 폭
	final int WIN_HEIGHT = 700; // 전체 frame의 높이

	JFrame mainFrame = new JFrame(); // 전체 GUI를 담을 프레임
	JPanel controlPanel = new JPanel(); // 게임 컨트롤이 들어갈 패널
	
	JPanel coverPanel; // 초기화면이 나타날 패널
	JPanel menualPanel; // 초기화면이 나타날 패널
	
	JLabel lbUserId = new JLabel("User ID");
	JTextField tfUserId = new JTextField(5);   // user id
	JButton btMenual = new JButton("메뉴얼"); // 시작버튼
	JButton btStart = new JButton("시작");   // 시작버튼
	
	int score = 0;
	int level = 0;
	
	// 버튼 토글을 위한 비트 연산에 사용될 상수들
	private final int START = 1;
	private final int MANUAL = 2;

	
	JLayeredPane lp = new JLayeredPane(); // 화면을 여러장 겹치기 위한 PaneL 레이어
	
	GamePanel gamePanel; // 게임이 이루질 패널
	int gamePanelWidth, gamePanelHeight; // 실제 게임이 이루어질 영역의 크기
	
	Timer timerAnim;  // 그래픽 객체의 움직임을 관장하기 위한 타이머
	Timer timerClock; // 초단위 타이머 
	
	StringShape levelShape = new StringShape("LV: 0", 10, 20);
	StringShape scoreShape = new StringShape("Score: 0", 300, 20);
	List<Shape> ballList = new ArrayList<Shape>(); // 공객체 
	Shape player; // 키보드로 움직이는 Player 객체
	
	private AudioClip backgroundSound; // 게임 배경 음악
	private AudioClip boomSound; // 충돌음향

	
	public static void main(String argv[]) {
		new App().init();
	}

	public void init() {
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		controlPanel.add(lbUserId);
		controlPanel.add(tfUserId);
		controlPanel.add(btMenual);
		controlPanel.add(btStart);
		
		// 게임의 진행이 디스플레이 될 패널
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
		timerAnim = new Timer(10, new AnimeListener()); // 그림의 이동을 처리하기 위한 리스너
		
		
		player = new Shape(getClass().getResource("player.png"), gamePanelWidth, gamePanelHeight);		
		
		// 버튼 리스너의 설치
		btMenual.addActionListener(new MenualListener());
		btStart.addActionListener(new StartListener());
		gamePanel.addKeyListener(new DirectionListener()); // 키보드 리스너 설치
		tfUserId.addKeyListener(new UserInputListener());
		
		// 게임을 위한 음향 파일 설치
		try {
			backgroundSound = JApplet.newAudioClip(getClass().getResource("start.wav"));
			boomSound = JApplet
					.newAudioClip(getClass().getResource("boom.wav"));

		} catch (Exception e) {
			System.out.println("음향 파일 로딩 실패");
		}

		buttonOff(MANUAL+START); // 활성화된 버튼의 조정
		buttonOn(MANUAL); // 활성화된 버튼의 조정

		// 전체 프레임에 배치
		//todo -------------- mainFrame.add(lp);
		mainFrame.add(BorderLayout.CENTER, lp);
		mainFrame.add(BorderLayout.SOUTH, controlPanel);
		
		mainFrame.setSize(WIN_WIDTH, WIN_HEIGHT);
		mainFrame.setVisible(true);
		
		// 게임이 이루어질 패널의 실제 폭과 넓이 계산
		gamePanelWidth = gamePanel.getWidth();
		gamePanelHeight = gamePanel.getHeight();
		gamePanelHeight -= controlPanel.getHeight();
		gamePanelHeight -= 20; 

		player.setY(gamePanelHeight-38);
		player.setX(gamePanelWidth/2 - 25);
	}
	
	// 게임이 진행되는 메인 패널
	@SuppressWarnings("serial")
	class GamePanel extends JPanel {
		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight()); // 화면 지우기

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
	
	// 버튼의 활성 비활성화를 위한 루틴
	private void buttonOn(int flags) {
		if ((flags & START) == START) btStart.setEnabled(true);
		if ((flags & MANUAL) == MANUAL) btMenual.setEnabled(true);
	}

	
	private void buttonOff(int flags) {
		if ((flags & START) == START) btStart.setEnabled(false);
		if ((flags & MANUAL) == MANUAL) btMenual.setEnabled(false);
	}
	
	
	// 게임의 종료시 처리해야 될 내용
	private void finishGame() {
		backgroundSound.stop(); // 음향 종료
		timerClock.stop(); // 시간 디스플에이 멈춤
		timerAnim.stop(); // 그림객체 움직임 멈춤
		gamePanel.setFocusable(false); // 포커싱 안되게 함(즉 키 안먹음)
	}


	// 시작 버튼의 감청자
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			lp.moveToFront(gamePanel);
			gamePanel.setDoubleBuffered(true);
			gamePanel.setFocusable(true); // gamePanel이 포커싱될 수 있게 함
			gamePanel.requestFocus(); // 포커싱을 맞춰줌(이것 반드시 필요)

			//backgroundSound.play(); // 배경음악 시작
			backgroundSound.loop();
			timerClock.start();
			timerAnim.start(); // 그림객체 움직임을 위한 시작

			buttonOff(MANUAL+START); // 활성화된 버튼의 조정
			tfUserId.setEnabled(false);
			
			mainFrame.repaint(); // 화면을 다시 디스플레이 되게 함
		}
	}
	
	// 메뉴얼 버튼의 감청자
	class MenualListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			lp.moveToFront(menualPanel);
		}
	}
	
	class UserInputListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			if(tfUserId.getText().isEmpty()) {
				buttonOff(START); // 활성화된 버튼의 조정	
			}
			else {
				buttonOn(START); // 활성화된 버튼의 조정
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}
	
	// goAnime 타이머에 의해 주기적으로 실행될 내용
	// 객체의 움직임, 충돌의 논리를 구현
	private class AnimeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			// 만약 충돌하였으면 충돌의 효과음 나타내고 타이머를 중단시킴
			for (Shape s : ballList) {
				if (s.collide(new Point(player.x, (int)player.y))) {
					boomSound.play(); // 충돌의 음향
					finishGame(); // 게임 중단
					return;
				}
			}

			// 그림 객체들을 이동시킴
			for (Shape s : ballList) {
				s.move();
			}
			
			// 점수 계산
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
			
			// 시간이 일정시간 지나면 추가 공 생성 
			if(times % 2 == 0) {
				makeBall();
				levelShape.setString("LV: " + ++level);
			}
			
			//System.out.println("times : " + times);
		}
	}
	
	// 키보드 움직임을 감청하는 감청자
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
	
	// 초기화면을 나타내는 패널
	@SuppressWarnings("serial")
	class coverPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource("init.png"))
					.getImage();
			g.drawImage(image, 0, 0, this);
		}
	}

	// 메뉴얼을 나타내는 패널
	@SuppressWarnings("serial")
	class manualPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource("manual.png"))
					.getImage();
			g.drawImage(image, 0, 0, this);
		}
	}

}


