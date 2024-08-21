package notice;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

import oracle.jdbc.OracleTypes;

public class Login {
	
	public void powerOn() {
		System.out.println("1: 회원가입");
		System.out.println("2: 로그인");
		System.out.println("3: 아이디 찾기");
		System.out.println("4: 비밀번호 초기화");
		System.out.println("5: 종료");
	}

	void changePw(Connection conn,Scanner sc) throws SQLException {
		System.out.println("이름을 입력해주세요");
		String name = sc.nextLine();
		System.out.println("전화번호를 입력해주세요");
		String tel = sc.nextLine();
		String check_id = do_find_id(conn,name,tel);
		System.out.println("아이디를 입력해주세요");
		String id = sc.nextLine();
		if(id.equals(check_id)) doChangePw(conn,sc,id);
		else System.out.println("일치하는 데이터가 없습니다.");
	}

	private void doChangePw(Connection conn, Scanner sc,String id) throws SQLException {
		System.out.println("새로운 비밀번호를 입력해주세요");
		String pw = sc.nextLine();
		String sql = "{call change_pw(?,?)}";
		CallableStatement changeSql = conn.prepareCall(sql);
		changeSql.setString(1,id);
		changeSql.setString(2,pw);
		changeSql.execute();
		changeSql.close();
		System.out.println("새로운 비밀번호로 변경되었습니다.");
	}

	void find_Id(Connection conn,Scanner sc) throws SQLException {
		System.out.println("이름을 입력해주세요");
		String name = sc.nextLine();
		System.out.println("전화번호를 입력해주세요");
		String tel = sc.nextLine();
		String id = do_find_id(conn,name,tel);
		if(id == null) System.out.println("해당 데이터와 일치하는 아이디가 존재하지 않습니다");
		else System.out.println(name + "님의 아이디는"+ id+"입니다");
	}
	
	private String do_find_id(Connection conn,String name, String tel) throws SQLException {	
		String sql = "{call find_id(?,?,?)}";
		CallableStatement findSql = conn.prepareCall(sql);
		findSql.setString(1,name);
		findSql.setString(2,tel);
		findSql.registerOutParameter(3, Types.VARCHAR);
		findSql.execute();
		String id = findSql.getString(3);		
		return id;
	}
	
	MainMenu login(Connection conn,Scanner sc) throws SQLException{
		System.out.println("아이디를 입력해주세요");
		String id = sc.nextLine();
		System.out.println("비밀번호를 입력해주세요");
		String pw = sc.nextLine();
		Info info = get_Id(conn,sc,id);
		if(info == null) {
			return null;
		}
		else if(info.getPw().equals(pw)) {
			String sql = "{call check_login(?)}";
			CallableStatement checkLogin = conn.prepareCall(sql);
			checkLogin.setString(1,id);
			checkLogin.execute();
			checkLogin.close();
			return new MainMenu(info);
		}
		return null;
	}
	
	private Info get_Id(Connection conn,Scanner sc,String id) throws SQLException {
		String sql = "{call do_login(?,?)}";
		CallableStatement doLogin = conn.prepareCall(sql);
		doLogin.setString(1,id);
		doLogin.registerOutParameter(2, OracleTypes.CURSOR);
		doLogin.execute();
		ResultSet rs = (ResultSet) doLogin.getObject(2);
		if(!rs.next()) return null;
		Info info = set_Info(rs.getString("id"),rs.getString("pw"),rs.getString("name")
				,rs.getString("tel"),rs.getString("adress"),rs.getInt("sexual"),rs.getInt("authority"));
		rs.close();
		return info;
	}

	void accession(Connection conn, Scanner sc) throws SQLException {
		Info info = get_Info(sc);
		while(true) {
			System.out.println("1: 가입");
			System.out.println("2: 다시입력");
			System.out.println("3: 이전 화면으로");
			switch(sc.nextLine()) {
				case "1" :					
					put_info(conn,info);
					return;
				case "2" :
					accession(conn,sc);
					return;
				case "3" :
					return;
				default : 
					System.out.println("잘못입력하셨습니다");
					break;
			}
		}
	}
	private void put_info(Connection conn, Info info) throws SQLException {
		boolean run = check_Info(conn,info);
		if(run) {
			String sql = "{call users_create(?,?,?,?,?,?)}";
			CallableStatement userCreate = conn.prepareCall(sql);
			userCreate.setString(1, info.getId());
			userCreate.setString(2, info.getPw());
			userCreate.setString(3, info.getName());
			userCreate.setString(4, info.getTel());
			userCreate.setString(5, info.getAdress());
			userCreate.setInt(6, info.getSexual());
			userCreate.execute();        
	        userCreate.close();
		}
		
	}
	private Info get_Info(Scanner sc) {
		System.out.println("아이디: ");
		String id = sc.nextLine();
		System.out.println("비번: ");
		String pw = sc.nextLine();
		System.out.println("이름: ");
		String name = sc.nextLine();
		System.out.println("전화번호: ");
		String tel = sc.nextLine();
		System.out.println("주소: ");
		String adress = sc.nextLine();
		System.out.println("성별: ");
		int sexual = sexual_Put(sc);
		return set_Info(id,pw,name,tel,adress,sexual,0);
	}
	
	static Info set_Info(String id,String pw, String name, String tel, String adress, int sexual, int authority) {
		Info info = new Info();
		info.setId(id);
		info.setPw(pw);
		info.setName(name);
		info.setTel(tel);
		info.setAdress(adress);
		info.setSexual(sexual);
		info.setAuthority(authority);
		return info;
	}

	static int sexual_Put(Scanner sc) {
		String sexual = sc.nextLine();
		if(sexual.equals("남") || sexual.equals("남자")) return 1;
		else if(sexual.equals("여") || sexual.equals("여자")) return 2;
		else {
			return -1;
		}
	}
	
	private boolean check_Info(Connection conn, Info info) throws SQLException {
		if(id_Checker(conn,info.getId())) {
			System.out.println("중복되는 아이디 입니다.");
			return false;
		}
		if(info.getSexual()==-1) {
			System.out.println("성별이 잘못되었습니다.");
			return false;
		}
		return true;
		
	}
	private boolean id_Checker(Connection conn,String id) throws SQLException {
		String sql = "{? = call check_Id(?)}";
		CallableStatement getId = conn.prepareCall(sql);
		getId.registerOutParameter(1,Types.INTEGER);
		getId.setString(2, id);
		getId.execute();
		int check_id = getId.getInt(1);
        getId.close();
        if(check_id ==1) return true;
        else return false;
	}
}
