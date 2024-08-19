package notice;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import oracle.jdbc.OracleTypes;

public class Board {
	private Info info;
	public Board(Info info) {
		this.info = info;
	}
	public boolean board(Connection conn,Scanner sc) throws SQLException {
		while(true) {
			System.out.println("1: 나의정보확인");
			System.out.println("2: 게시물 목록");
			System.out.println("3: 회원 목록");
			System.out.println("4: 로그아웃");
			System.out.println("5: 종료");
			switch(sc.nextLine()) {
				case "1" :					
					boolean run = viewInfo(conn,sc);
					if(run) break;
					else return true;
				case "2" :
					viewBoard();
				case "3" :
					viewAccountList(conn,sc);
					break;
				case "4" :
					logout(conn);
					System.out.println("로그아웃되었습니다.");
					return true;
				case "5" :
					return false;	
				default  : 
					System.out.println("잘못 입력하셨습니다.");
					break;
			}
		}
	}
	
	private boolean viewInfo(Connection conn,Scanner sc) throws SQLException {
		while(true) {
			System.out.println(info);
			System.out.println("1: 정보 수정");
			System.out.println("2: 회원 탈퇴");
			System.out.println("3: 나가기");
			switch(sc.nextLine()) {
				case "1" :					
					modifyInfo(conn,sc);
					break;
				case "2" :					
					secessionInfo(conn,sc);
					return false;
				case "3" :
					return true;	
				default  : 
					System.out.println("잘못 입력하셨습니다.");
					break;
			}
		}
	}
	private void secessionInfo(Connection conn,Scanner sc) throws SQLException {		
		if(!check_pw(sc)) {
			System.out.println("비밀번호가 다릅니다");
			return;
		}
		else {
			while(true) {
				System.out.println(info);
				System.out.println("정말 삭제하시겠습니까?" + "\n 1: 예   2: 아니오");
				switch(sc.nextLine()) {
					case "1" :
						doSessionInfo(conn,info.getId());
						return;
					case "2" :
						return;
					default :
						System.out.println("잘못입력하셨습니다.");
						break;
				}
			}
		}
		
	}
	
	private void doSessionInfo(Connection conn,String id) throws SQLException {
		String sql = "{call users_session(?)}";
		CallableStatement getId = conn.prepareCall(sql);
		getId.setString(1, id);
		getId.execute();        
        getId.close();
        System.out.println("Id : "+ id + "가 삭제되었습니다.");
	}
	
	private void modifyInfo(Connection conn,Scanner sc) throws SQLException {
		if(!check_pw(sc)) {
			System.out.println("비밀번호가 다릅니다");
			return;
		}
		else doModifyInfo(conn,sc);
	}
	
	private void doModifyInfo(Connection conn, Scanner sc) throws SQLException {
		Info modifyInfo = getModifyInfo(sc);
		System.out.println(modifyInfo+" 바꿀 비밀번호 : "+modifyInfo.getPw());
		System.out.println("1: 수정완료");
		System.out.println("2: 다시입력");
		System.out.println("3: 이전 화면으로");
		switch(sc.nextLine()) {
			case "1" :					
				putModifyInfo(conn,modifyInfo);
				break;
			case "2" :
				doModifyInfo(conn,sc);
				break;
			case "3" :
				break;
			default :
				System.out.println("잘못입력하셨습니다.");
				break;
		}
		
	}
	private Info getModifyInfo(Scanner sc) {
		
		Info modifyInfo = new Info();
		modifyInfo.setId(info.getId());
		System.out.println("새로운 비밀번호: ");
		modifyInfo.setPw(sc.nextLine());
		System.out.println("새로운 이름: ");
		modifyInfo.setName(sc.nextLine());
		System.out.println("새로운 전화번호: ");
		modifyInfo.setTel(sc.nextLine());
		System.out.println("새로운 주소: ");
		modifyInfo.setAdress(sc.nextLine());
		System.out.println("새로운 성별: ");
		modifyInfo.setSexual(Notice.sexual_Put(sc));
		return modifyInfo;
		
	}
	private boolean check_pw(Scanner sc) {
		System.out.println("비밀번호를 입력해 주세요");
		if(info.getPw().equals(sc.nextLine())) return true;
		else return false;
		
	}
	private void putModifyInfo(Connection conn,Info modifyInfo) throws SQLException {
		info.setPw(modifyInfo.getPw());
		info.setName(modifyInfo.getName());
		info.setTel(modifyInfo.getTel());
		info.setAdress(modifyInfo.getAdress());
		info.setSexual(modifyInfo.getSexual());
		String sql = "{call users_modify(?,?,?,?,?,?)}";
		CallableStatement getId = conn.prepareCall(sql);
		getId.setString(1, info.getId());
		getId.setString(2, info.getPw());
		getId.setString(3, info.getName());
		getId.setString(4, info.getTel());
		getId.setString(5, info.getAdress());
		getId.setInt(6, info.getSexual());
		getId.execute();        
        getId.close();
        
	}
	
	private void viewBoard() {
		// TODO Auto-generated method stub
		
	}
	private void viewAccountList(Connection conn, Scanner sc) throws SQLException {
		if(info.getAuthority()==1) {
			managerBoard(conn,sc);
		}
		else {
			System.out.println("권한이 없습니다");
			return;
		}
	}
	private void managerBoard(Connection conn,Scanner sc) throws SQLException {
		List<Info> list = new ArrayList<>();
		addList(list,conn);
		while(true) {
			list.forEach(info -> System.out.println(info));
			System.out.println("1: 회원 삭제");
			System.out.println("2: 나가기");
			switch(sc.nextLine()) {
				case "1" :					
					System.out.println("삭제할 아이디를 입력해주세요");
					Optional<Info> optinalInfo = findId(list, sc.nextLine());
					if(!optinalInfo.isPresent()) System.out.println("삭제할 아이디가 존재하지 않습니다");
					else {
						doSessionInfo(conn,optinalInfo.get().getId());
						list.remove(optinalInfo.get());
					}
					break;
				case "2" :
					return;
				default :
					System.out.println("잘못입력하셨습니다.");
					break;
			}		
		}
	}
	public Optional<Info> findId(List<Info> list, String id) {
	    return list.stream()
	               .filter(info -> info.getId().equals(id)) // id가 일치하는 객체 필터링
	               .findFirst(); // 첫 번째 일치하는 객체 반환
	}
	
	private void addList(List<Info> list,Connection conn) throws SQLException {
		String sql = "{call managercall_users(?)}";
		CallableStatement managerCall = conn.prepareCall(sql);
		managerCall.registerOutParameter(1, OracleTypes.CURSOR);
		managerCall.execute();
		ResultSet rs = (ResultSet) managerCall.getObject(1);
		if (!rs.isBeforeFirst()) {
		    System.out.println("등록된 회원이 없습니다");
		}
		while(rs.next()) {
			Info info = Notice.set_Info(rs.getString("id"),rs.getString("pw"),rs.getString("name")
					,rs.getString("tel"),rs.getString("adress"),rs.getInt("sexual"),rs.getInt("authority"));
			list.add(info);
		}
		rs.close();
		
	}
	private void logout(Connection conn) throws SQLException {
		String sql = "{call logout_user(?)}";
		CallableStatement logoutUser = conn.prepareCall(sql);
		logoutUser.setString(1,info.getId());
		logoutUser.execute();
		logoutUser.close();
	}
	public String getPw() {
		// TODO Auto-generated method stub
		return info.getPw();
	}
}
