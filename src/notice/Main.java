package notice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Connection conn = null;
		try {
			
			Class.forName("oracle.jdbc.OracleDriver");

			conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@localhost:1521/xe", 
				"user01", 
				"1004"
			);	
			Scanner sc = new Scanner(System.in);
			boolean run = true;
			Login login = new Login();
			while(run) {
				login.powerOn();
				switch(sc.nextLine()) {
					case "1" :					
						login.accession(conn,sc);
						break;
					case "2" :
						MainMenu board =login.login(conn,sc);
						if(board !=null) run = board.mainMenu(conn,sc);
						else System.out.println("아이디나 비밀번호가 잘못되었습니다.");
						break;
					case "3" :
						login.find_Id(conn,sc);
						break;
					case "4" :
						login.changePw(conn,sc);
						break;
					case "5" :
						run = false;
						break;		
					default  : 
						System.out.println("잘못 입력하셨습니다.");
						break;		
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				try { 
					//연결 끊기
					conn.close(); 
					
				} catch (SQLException e) {}
			}
		}
	}

}
