/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snakegame;

import java.awt.*; // de phat trien ung dung tren GUI hoac cua so
import java.awt.event.*; //de xu ly cac su kien nhu keo tha chuot , bam nut, ... thao tac nguoi dung 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.*; // de tao cac ung dung window-based
import java.util.Random; // de su dung phep toan ngau nhien
import java.util.Scanner;


@SuppressWarnings("serial")
public class GamePanel extends JPanel implements ActionListener {
	
	static final int SCREEN_WIDTH = 1350;
	static final int SCREEN_HEIGHT = 750;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	static final int DELAY = 150;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int appleX;
	int appleY;
	char direction = 'R';
	boolean running = false;
        int highScore;
        int score;
	Timer timer;
	Random random;
	
	GamePanel(){
		random = new Random(); 
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}
	
	public void startGame() { //Bắt đầu trò chơi
                checkHighScore(); //Kiểm tra điểm số cao nhất để hiển thị
		newApple(); //Tạo táo
		running = true; //Chương trình khởi chạy set giá trị = true
		timer = new Timer(DELAY,this); //Khởi tạo bộ đếm thời gian mới
		timer.start();// Bắt đầu đếm
	}
	
	public void paintComponent(Graphics g) { //tạo đối tượng cho phép vẽ các vật thể lên Panel
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		if(running) { //Nếu chương tình chạy 
			/*
                        Tạo lưới trên cửa sổ 
			for( int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			}
			*/
			g.setColor(Color.red); //Set màu
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE); //Tô kín ô chứa giá trị đầu rắn
			
			for(int i = 0; i < bodyParts; i++) { //Tô thân rắn
				if(i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else {
					g.setColor(new Color(45,180,0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
					
				}				
			}
                                              
                                                
			g.setColor(Color.red); //Set màu phông chữ kích cỡ và căn chỉnh vị trí dòng SCORE HIGHSCORE
			g.setFont( new Font("Monospaced", Font.PLAIN, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("SCORE: "+score, (SCREEN_WIDTH - metrics.stringWidth("SCORE: "+score))/2, SCREEN_HEIGHT - 710);
                        g.drawString("HIGHSCORE: " + highScore, (SCREEN_WIDTH - metrics.stringWidth("HIGHSCORE: " + highScore))/2, SCREEN_HEIGHT - 675);
		} 
		else {
			gameOver(g);
		}
	}
	
	public void newApple() { //Tạo táo ở vị trí ngẫu nhiên 
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
	}
	
	public void move() { //Di chuyển
		for(int i = bodyParts; i > 0; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}
	
	public void checkApple() { //Kiểm tra táo đã bị ăn chưa
		if((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
                        score++;
			newApple();
		}
	}
	
	public void checkCollisions() { //Kiểm tra điều kiện thua
            //Nếu chạm vào cơ thể
		for(int i = bodyParts; i > 0; i--) {
			if((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;                               
			}
		}
            //Nếu chạm vào viền cửa sổ
		if(x[0] < 0) {
			running = false;
		}
		
		if(x[0] > SCREEN_WIDTH) {
			running = false;
		}
		
		if(y[0] < 0) {
			running = false;
		}
		
		if(y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		
		if(!running) {
                        checkScore();
			timer.stop();
                        
		}
	}  
        
        public void checkHighScore() { //Kiểm tra HIGHSCORE
            try {
                File file = new File("HS.txt");
                Scanner sc = new Scanner(file);
                while(sc.hasNextLine()){
                    String data = sc.nextLine();
                    highScore = Integer.parseInt(data);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }            
        }
        
        public void checkScore() { //Kiểm tra SCORE có lớn hơn HIGHSCORE không. Nếu lớn hơn thì ghi đè file
                                    
            if (score > highScore ) {
                JFrame frame = new JFrame("JOptionPane showMessageDialog example");
                JOptionPane.showMessageDialog(frame,"You set a new HighScore!");
                
                File scoreFile = new File("HS.txt");
                if (!scoreFile.exists()) {
                    try{
                        scoreFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                              
                try {
                    PrintWriter writer = new PrintWriter("HS.txt");
                    writer.println(score);
                    writer.close();
                } catch (Exception ex){
                    ex.printStackTrace();
                }                
            }
        }

                        
	public void gameOver(Graphics g){ //Màn hình hiển thị khi kết thúc trò chơi
            
                        
		//Score Over
		g.setColor(Color.red);
		g.setFont( new Font("Monospaced", Font.PLAIN, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("SCORE: "+score, (SCREEN_WIDTH - metrics1.stringWidth("SCORE: "+score))/2, SCREEN_HEIGHT - 710);
                //HighScore
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("HIGHSCORE: "+highScore, (SCREEN_WIDTH - metrics2.stringWidth("HIGHSCORE: "+highScore))/2, SCREEN_HEIGHT - 675);                
                
                
		//Game Over text
		g.setColor(Color.red);
		g.setFont( new Font("Monospaced", Font.PLAIN, 75));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("GAME OVER", (SCREEN_WIDTH - metrics3.stringWidth("Gamve Over"))/2, SCREEN_HEIGHT/2);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) { 
		// TODO Auto-generated method stub
		if(running) {
			move();
			checkApple();
			checkCollisions();
		} 
		repaint();
	}

    private FileInputStream openFileInput(String hStxt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
	
	public class MyKeyAdapter extends KeyAdapter{ //Điều hướng di chuyển của rắn
		
		public void keyPressed (KeyEvent e){
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
				}
				break;
			}
		}
	}			
}

