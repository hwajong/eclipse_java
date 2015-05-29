import java.net.URL;


@SuppressWarnings("serial")
public class Ball extends Shape {
	
	double steps_x; // x좌표 변화량 
	double h = 0;   // 최대 높이 
	double vy = 0;  // y좌표 변화량   
	double g;       // 중력 가속도 
	
	public Ball(URL imgURL, int margin, int steps, int xBoundary, int yBoundary) {
		super(imgURL, margin, steps, xBoundary, yBoundary);
		
		// 최초 공이 나오는 시작 위치 설정 
		x= (int) (0.5 * xBoundary);
		y= (int) (0.3 * yBoundary);
		
		// 최초 설정한 높이 만큼만 튀어 오를 수 있게 h 설정 
		h = y;
		
		// 초기 y좌표 변화량 설정 
		//vy = ((Math.random() * 100) % 10);
		
		// 중력가속도를 랜덤하게 설정해 공의 움직임을 다양하게 한다.  
		g = Math.random();
		if(g > 0.2) g = 0.2;
		if(g < 0.02) g = 0.02;
		
		// x좌표 변화량도 랜덤하게 설정.
		steps_x = Math.random() * 3;
		if(steps_x < 0.3) steps_x = 0.3;
	}
	
	public void move() {
		if (xDirection > 0 && x >= xBoundary) {
			xDirection = -1;
		}
		if (xDirection < 0 && x <= 0) {
			xDirection = 1;
		}
		
		// x좌표 계산 
		x += (xDirection * steps_x);

		// 땅에 다았을 경우 위로 올라가게 방향 변경 
		if(y > yBoundary-20) {
			vy*=-1;
			y = yBoundary-20;
		}
		
		// y좌표 변화량에 중력가속도를 더한다. ( 포물선 움직임 )
		vy += g; 
		
		// y좌표 계산 
		y+=vy;
	}	
}
