import java.util.*;
import java.lang.String;
import java.math.*;

class Point{
	int x;
	int y;
	
	Point(int m, int n){
		x = m;
		y = n;
	}	
}

class Ship{
	ArrayList<Point> live = new ArrayList<Point>();
	ArrayList<Point> dead = new ArrayList<Point>();
	
	boolean vertical;
	int length;
	Point start;
	
	Ship(Point origin, boolean isVertical, int length){
		start = origin;
		this.length = length;
		vertical = isVertical;
		for(int i = 0; i < length; i++){
			if(isVertical){
				live.add(new Point(origin.x + i, origin.y));
			}
			else{
				live.add(new Point(origin.x, origin.y + i));
			}
		}
	}
	
	boolean containsPoint(Point P){
		boolean on = false;
		for(int i = 0; i < this.length; i++){
			if(live.get(i).x == P.x && live.get(i).y == P.y){
				on = true;
			}
		}
		return on;
	}
	
	boolean collidesWith(Ship S){
		boolean hits = false;
		for(int i = 0; i < this.live.size(); i++){
			for(int j = 0; j < S.live.size(); j++){
				if(this.live.get(i).x == S.live.get(j).x && this.live.get(i).y == S.live.get(j).y){
					hits = true;
				}
			}
		}
		return hits;
	}
	
	void shotFiredAtPoint(Point p){
		if(isHitAtPoint(p)){
			dead.add(new Point(p.x, p.y));
			int index = 0;
			while(live.get(index).x != p.x && live.get(index).y != p.y){
				index++;
			}
			live.remove(index);
		}
	}
	
	boolean isHitAtPoint(Point p){
		boolean hit = false;
		for(int i = 0; i < live.size(); i++){
			if(live.get(i).x == p.x && live.get(i).y == p.y){
				
				hit = true;
			}
		}
		
		return hit;
	}
	
	int hitCount(){
		return dead.size();
	}
}

class Board{
	int side;
	ArrayList<Point> boardList;
	ArrayList<Ship> userShips = new ArrayList<Ship>();
	ArrayList<Ship> enemyShips = new ArrayList<Ship>();
	
	boolean shootAtEnemy(Point p){
		boolean hit = false;
		int shipIndex = -1;
		for(int i = 0; i < enemyShips.size(); i++){
			if(enemyShips.get(i).isHitAtPoint(p)){
				if(enemyShips.get(i).live.size() == 1){
					shipIndex = i;
				}
				hit = true;
				printBoard();
				enemyShips.get(i).shotFiredAtPoint(p);
				
			}
		}
		printBoard();
		if(hit){
			System.out.println("Hit! @ (" + p.x + "," + p.y + ").");
		}
		else{
			System.out.println("(" + p.x + "," + p.y + ") was a miss.");
		}

		if(shipIndex > -1){
			System.out.println("Ship of length " + enemyShips.get(shipIndex).length + " has been sunk!");
			//enemyShips.remove(shipIndex);
		}
		return true;
	}
	
	Board(int side){
		this.side = side;
		boardList = new ArrayList<Point>(side * side);
		for(int i = 0; i < side; i ++){
			for(int j = 0; j < side; j++){
				boardList.add(new Point(i,j));
			}
		}
	}
	
	
	void printBoard(){
		for(int i = side - 1; i >= 0; i--){
			System.out.print(i +" ");
			for(int j = 0; j < side; j++){
				boolean special = false;
				for(int k = 0; k < userShips.size(); k++){
					if(userShips.get(k).containsPoint(new Point(i,j))){
						System.out.print("F ");
						special = true;
					}
				}
				for(int k = 0; k < enemyShips.size(); k++){
					for(int l = 0; l < enemyShips.get(k).dead.size(); l++){
						if(enemyShips.get(k).dead.get(l).x == j && enemyShips.get(k).dead.get(l).y == i){
							System.out.print("X ");
							special = true;
						}
					}
				}
				
				if(!special){
					System.out.print("~ ");
				}
				
				else{
					special = false;
				}
				
			}
			System.out.println();
		}
		
		System.out.print("  ");
		for(int i = 0; i < side; i++){
			System.out.print(i + " ");
		}
		System.out.println();
	}
	
	// Method that adds a ship to the board, implements collision detection.
	boolean addShip(ArrayList<Ship> array, Point start, boolean vertical, int length){
		boolean bump = false;
		for(int i = 0; i < enemyShips.size(); i++){
			if(new Ship(start, vertical, length).collidesWith(enemyShips.get(i))){
				bump = true;
			}
		}
		for(int i = 0; i < userShips.size(); i++){
			if(new Ship(start, vertical, length).collidesWith(userShips.get(i))){
				bump = true;
			}
		}
		if(!bump){
			array.add(new Ship(start,vertical,length));
		}
		
		return bump;
	}
	
	// Method that adds an enemy of a given length randomly to the board. Utilizes
	//  the addShip method for collision detection.
	void addEnemy(int length){
		Random rand = new Random();
		boolean vertical = rand.nextBoolean();

		if(vertical){
			while(addShip(	enemyShips,
							new Point(rand.nextInt((side - length - 0) + 1) + 0, rand.nextInt((side - 1 - 0) + 1) + 0),
							vertical,
							length));
		}
		else{
			while(addShip(	enemyShips,
							new Point(rand.nextInt((side - 1 - 0) + 1) + 0, rand.nextInt((side - length - 0) + 1) + 0),
							vertical,
							length));
		}
	}
	
	int liveEnemies(){
		int tiles = 0;
			for(int i = 0; i < enemyShips.size(); i++){
				tiles = tiles + enemyShips.get(i).live.size();
			}
		return tiles;
	}
}

public class main {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		
		System.out.println("Welcome to battleship!\n");
		Board field = new Board(10);
		
		field.printBoard();
		// 1 length 2 ship
		// 2 length 3 ships
		// 1 length 4
		// 1 length 5
		
		// Randomly place enemy pieces
		field.addEnemy(5);
		field.addEnemy(4);
		field.addEnemy(3);
		field.addEnemy(3);
		field.addEnemy(2);
		field.printBoard();
		
		while(field.liveEnemies() > 0){
			System.out.println("Where will you shoot next?");
			String str = input.nextLine();
			String[] coords = str.replaceAll("^\\D+","").split("\\D+");
			System.out.println(coords[0] + " " + coords[1]);
			
			
			field.shootAtEnemy(new Point(Integer.parseInt(coords[0]),Integer.parseInt(coords[1])));
		}
		
		System.out.println("\nYou've destroyed the enemy!");
		
		input.close();
	}

}
