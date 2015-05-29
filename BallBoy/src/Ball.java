import java.net.URL;


@SuppressWarnings("serial")
public class Ball extends Shape {
	
	double steps_x; // x��ǥ ��ȭ�� 
	double h = 0;   // �ִ� ���� 
	double vy = 0;  // y��ǥ ��ȭ��   
	double g;       // �߷� ���ӵ� 
	
	public Ball(URL imgURL, int margin, int steps, int xBoundary, int yBoundary) {
		super(imgURL, margin, steps, xBoundary, yBoundary);
		
		// ���� ���� ������ ���� ��ġ ���� 
		x= (int) (0.5 * xBoundary);
		y= (int) (0.3 * yBoundary);
		
		// ���� ������ ���� ��ŭ�� Ƣ�� ���� �� �ְ� h ���� 
		h = y;
		
		// �ʱ� y��ǥ ��ȭ�� ���� 
		//vy = ((Math.random() * 100) % 10);
		
		// �߷°��ӵ��� �����ϰ� ������ ���� �������� �پ��ϰ� �Ѵ�.  
		g = Math.random();
		if(g > 0.2) g = 0.2;
		if(g < 0.02) g = 0.02;
		
		// x��ǥ ��ȭ���� �����ϰ� ����.
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
		
		// x��ǥ ��� 
		x += (xDirection * steps_x);

		// ���� �پ��� ��� ���� �ö󰡰� ���� ���� 
		if(y > yBoundary-20) {
			vy*=-1;
			y = yBoundary-20;
		}
		
		// y��ǥ ��ȭ���� �߷°��ӵ��� ���Ѵ�. ( ������ ������ )
		vy += g; 
		
		// y��ǥ ��� 
		y+=vy;
	}	
}
