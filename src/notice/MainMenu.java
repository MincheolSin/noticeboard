package notice;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

import oracle.jdbc.OracleTypes;

public class MainMenu {
	private Info info;
	public MainMenu(Info info) {
		this.info = info;
	}
	public boolean mainMenu(Connection conn,Scanner sc) throws SQLException {
		while(true) {
			System.out.println("1: 나의정보확인");
			System.out.println("2: 게시물 관리");
			System.out.println("3: 회원 목록");
			System.out.println("4: 로그아웃");
			System.out.println("5: 종료");
			switch(sc.nextLine()) {
				case "1" :					
					boolean run = viewInfo(conn,sc);
					if(run) break;
					else return true;
				case "2" :
					boardMenu(conn,sc);
					break;
				case "3" :
					viewAccountList(conn,sc);
					break;
				case "4" :
					logout(conn);
					System.out.println("로그아웃되었습니다.");
					return true;
				case "5" :
					logout(conn);
					System.out.println("로그아웃되었습니다.");
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
					boolean success = secessionInfo(conn,sc);
					if(success) return false;
					else break;
				case "3" :
					return true;	
				default  : 
					System.out.println("잘못 입력하셨습니다.");
					break;
			}
		}
	}
	private boolean secessionInfo(Connection conn,Scanner sc) throws SQLException {		
		if(!check_pw(sc)) {
			System.out.println("비밀번호가 다릅니다");
			return false;
		}
		else {
			while(true) {
				System.out.println(info);
				System.out.println("정말 삭제하시겠습니까?" + "\n 1: 예   2: 아니오");
				switch(sc.nextLine()) {
					case "1" :
						doSessionInfo(conn,info.getId());
						return true;
					case "2" :
						return false;
					default :
						System.out.println("잘못입력하셨습니다.");
						break;
				}
			}
		}
		
	}
	
	private void doSessionInfo(Connection conn,String id) throws SQLException {
		String sql = "{call users_session(?)}";
		CallableStatement user_session = conn.prepareCall(sql);
		user_session.setString(1, id);
		user_session.execute();        
		user_session.close();
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
		while(true) {
			System.out.println(modifyInfo+" 바꿀 비밀번호 : "+modifyInfo.getPw());
			System.out.println("1: 수정완료");
			System.out.println("2: 다시입력");
			System.out.println("3: 이전 화면으로");
			switch(sc.nextLine()) {
				case "1" :					
					putModifyInfo(conn,modifyInfo);
					return;
				case "2" :
					doModifyInfo(conn,sc);
					return;
				case "3" :
					return;
				default :
					System.out.println("잘못입력하셨습니다.");
					break;
			}
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
		modifyInfo.setSexual(Login.sexual_Put(sc));
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
		CallableStatement user_modify = conn.prepareCall(sql);
		user_modify.setString(1, info.getId());
		user_modify.setString(2, info.getPw());
		user_modify.setString(3, info.getName());
		user_modify.setString(4, info.getTel());
		user_modify.setString(5, info.getAdress());
		user_modify.setInt(6, info.getSexual());
		user_modify.execute();        
		user_modify.close();     
	}
	
	private void viewBoard(Connection conn,List<Board> boardlist) throws SQLException {
		System.out.printf("%-10s %-10s %-15s %-10s %-10s%n", "게시물 번호", "작성자", "제목", "읽은수", "작성일");
	    LocalDate today = LocalDate.now();
	    for (Board board : boardlist) {
	        if (board.getBdate().toLocalDate().equals(today)) {
	            System.out.printf("%-10s %-10s %-15s %-10s %-10s%n", 
	                board.getRowbno(), 
	                board.getBwriter(), 
	                board.getBname(), 
	                board.getBread(), 
	                String.format("%02d:%02d", board.getBdate().getHour(), board.getBdate().getMinute()));
	        } else {
	            System.out.printf("%-10s %-10s %-15s %-10s %-10s%n", 
	                board.getRowbno(), 
	                board.getBwriter(), 
	                board.getBname(), 
	                board.getBread(), 
	                board.getBdate().toLocalDate());
	        }
	    }
	}
	
	private void boardMenu(Connection conn, Scanner sc) throws SQLException {
		boolean run =true;
		int pagenumber = 1;
		while(run) {
			if(pagenumber<=0) {
				System.out.println("잘못된 페이지 번호입니다.");
				pagenumber=1;
			}
			List<Board> boardlist = getBoardList(conn,pagenumber);
			if(boardlist == null) {
				System.out.println("현재 페이지에 등록된 게시물이 없습니다.");
				System.out.println("현재 페이지 번호 : " + pagenumber);
			}
			else {
				viewBoard(conn,boardlist);
				System.out.println("현재 페이지 번호 : " + pagenumber);
			}
			System.out.println("1: 게시물 등록");
			System.out.println("2: 게시물 상세보기");
			System.out.println("3: 이전 페이지  4: 원하는 페이지 이동  5: 다음 페이지");
			System.out.println("6: 나가기");
			switch(sc.nextLine()) {
				case "1" :					
					registBoard(conn,sc);
					break;
				case "2" :
					detailBoard(conn,sc,boardlist);
					break;
				case "3" :
					pagenumber--;
					break;
				case "4" :
					System.out.println("페이지 번호를 입력해주세요");
					pagenumber = getNumber(sc);
					break;
				case "5" :
					pagenumber++;
					break;
				case "6" :
					run=false;
					break;
				default :
					System.out.println("잘못입력하셨습니다.");
					break;
			}
		}
	}
	private void detailBoard(Connection conn, Scanner sc, List<Board> boardlist) throws SQLException {
		if(boardlist == null) {
			System.out.println("현재 페이지에 게시물이 존재하지 않습니다");
			return;
		}
		System.out.println("자세히 보고싶은 게시물 번호를 입력해 주세요");
		int rowbno = getNumber(sc);
		Board board = getDetail(conn,rowbno,boardlist);
		if(board == null) System.out.println("존재하지 않는 게시물 입니다");		
		else viewDetail(conn,sc,board);		
	}
	
	private Board getDetail(Connection conn, int rowbno,List<Board> boardlist) throws SQLException {
		Optional<Board> optionalBoard = findBoard(boardlist,rowbno);
		Board board;
		if(! optionalBoard.isPresent()) return null;
		else board = optionalBoard.get();
		String sql = "{call find_board(?,?,?)}";
		CallableStatement boardFind = conn.prepareCall(sql);
		boardFind.setInt(1, board.getBno());
		boardFind.registerOutParameter(2, Types.VARCHAR);
		boardFind.registerOutParameter(3, Types.INTEGER);
		boardFind.execute();
		String bdetail = boardFind.getString(2);
		int bread = boardFind.getInt(3);
		boardFind.close();
		if(bdetail == null) return null;
		board.setBdetail(bdetail);
		board.setBread(bread);
		return board;
	}
	
	private Optional<Board> findBoard(List<Board> boardlist,int rowbno) {
		return boardlist.stream().filter(board -> board.getRowbno()==rowbno).findFirst();
		
	}
	
	private void viewDetail(Connection conn, Scanner sc,Board board) throws SQLException {
		boolean run = true;
		while(run) {
			System.out.println("게시물 번호 : " + board.getRowbno()+" 게시물 이름 : "+board.getBname()+ " 작성자 : " + board.getBwriter());
			System.out.println("작성 날짜 : "+board.getBdate() +" 조회수: "+board.getBread());
			System.out.println("게시물 내용 : "+board.getBdetail());
			if(info.getAuthority()==1 || info.getId().equals(board.getBwriter())) run= manageDetailMenu(conn,sc,board);
			else run= detailMenu(conn,sc,board);
		}
	}
	
	private boolean manageDetailMenu(Connection conn,Scanner sc, Board board) throws SQLException {
		while(true) {
			System.out.println("1: 수정");
			System.out.println("2: 삭제");
			System.out.println("3: 이전 화면으로");
			switch(sc.nextLine()) {
				case "1" :					
					modifyBoard(conn,sc,board);
					return true;
				case "2" :
					return !sessionBoard(conn,sc,board);
				case "3" :
					return false;
				default: 
					System.out.println("잘못입력하셨습니다");
					break;
			}
		}
	}
	
	private boolean sessionBoard(Connection conn, Scanner sc, Board board) throws SQLException {
		if(info.getAuthority()==0) {
			if(!check_pw(sc)) {
				System.out.println("비밀번호가 다릅니다");
				return false;
			}
		}
		while(true) {
			System.out.println(board.getRowbno() +" : "+board.getBname());
			System.out.println("정말 삭제하시겠습니까?" + "\n 1: 예   2: 아니오");
			switch(sc.nextLine()) {
				case "1" :
					doSessionBoard(conn,board.getBno());
					return true;
				case "2" :
					return false;
				default :
					System.out.println("잘못입력하셨습니다.");
					break;
			}
		}	
	}
	
	private void doSessionBoard(Connection conn, int bno) throws SQLException {
		String sql = "{call board_session(?)}";
		CallableStatement board_session = conn.prepareCall(sql);
		board_session.setInt(1, bno);
		board_session.execute();        
		board_session.close();
        System.out.println("게시물이 삭제되었습니다.");
		
	}
	
	private void modifyBoard(Connection conn, Scanner sc, Board board) throws SQLException {
		if(info.getAuthority()==0) {
			if(!check_pw(sc)) {
				System.out.println("비밀번호가 다릅니다");
				return;
			}
		}
		doModifyBoard(conn,sc,board);
		
	}
	
	private void doModifyBoard(Connection conn, Scanner sc,Board board) throws SQLException {
		Board modifyBoard = getModifyBoard(sc,board);
		while(true) {
			System.out.println("1: 수정완료");
			System.out.println("2: 다시입력");
			System.out.println("3: 이전 화면으로");
			switch(sc.nextLine()) {
				case "1" :					
					putModifyBoard(conn,modifyBoard);
					board.setBname(modifyBoard.getBname());
					board.setBdetail(modifyBoard.getBdetail());
					return;
				case "2" :
					doModifyBoard(conn,sc,board);
					return;
				case "3" :
					return;
				default :
					System.out.println("잘못입력하셨습니다.");
					break;
			}
		}
		
	}
	
	private Board getModifyBoard(Scanner sc, Board board) {	
		System.out.println("게시물 제목: ");
		String modifyName = sc.nextLine();
		System.out.println("내용: ");
		String modifyDetail = sc.nextLine();
		return set_Board(board.getRowbno(),board.getBno(),board.getBwriter(), 
						 modifyName,board.getBread(), board.getBdate(), 
						 board.getBpw(),modifyDetail);
	}
	
	private void putModifyBoard(Connection conn, Board board) throws SQLException {
		String sql = "{call board_modify(?,?,?)}";
		CallableStatement board_modify = conn.prepareCall(sql);
		board_modify.setInt(1, board.getBno());
		board_modify.setString(2, board.getBname());
		board_modify.setString(3, board.getBdetail());
		board_modify.execute();        
        board_modify.close();	
	}
	
	private boolean detailMenu(Connection conn,Scanner sc, Board board) {
		while(true) {
			System.out.println("1: 이전화면으로");
			switch(sc.nextLine()) {
				case "1" :					
					return false;			
				default:
					System.out.println("잘못입력하셨습니다");
					break;
			}
		}
		
	}
	
	private void registBoard(Connection conn, Scanner sc) throws SQLException {
		Board board = getBoard(sc);
		while(true) {
			System.out.println("1: 등록");
			System.out.println("2: 다시입력");
			System.out.println("3: 이전 화면으로");
			switch(sc.nextLine()) {
				case "1" :					
					putBoard(conn,board);
					return;
				case "2" :
					registBoard(conn,sc);
					return;
				case "3" :
					return;
				default: 
					System.out.println("잘못입력하셨습니다");
					break;
			}
		}
		
	}
	
	private void putBoard(Connection conn, Board board) throws SQLException {
		String sql = "{call board_create(?,?,?,?)}";
		CallableStatement boardCreate = conn.prepareCall(sql);
		boardCreate.setString(1, board.getBwriter());
		boardCreate.setString(2, board.getBname());
		boardCreate.setString(3, board.getBpw());
		boardCreate.setString(4, board.getBdetail());
		boardCreate.execute();        
		boardCreate.close();
		
	}
	
	private Board getBoard(Scanner sc) {
		System.out.println("게시물 제목: ");
		String bname = sc.nextLine();
		System.out.println("내용: ");
		String bdetail = sc.nextLine();
		System.out.println("게시물 비밀번호: ");
		String bpw = sc.nextLine();
		return set_Board(0,0,info.getId(),bname,0,null,bpw,bdetail);
	}
	
	private Board set_Board(int rowbno,int bno,String bwriter, String bname, int bread, LocalDateTime bdate, String bpw,String bdtail) {
		Board board = new Board();
		board.setRowbno(rowbno);
		board.setBno(bno);
		board.setBwriter(bwriter);
		board.setBname(bname);
		board.setBread(bread);
		board.setBdate(bdate);
		board.setBpw(bpw);
		board.setBdetail(bdtail);
		return board;
	}

	private int getNumber(Scanner sc) {
		String number = sc.nextLine();
		if(isInteger(number)) return Integer.parseInt(number);
		else return -1;
	}
	
	private boolean isInteger(String str) {
        return Pattern.matches("-?\\d+", str);
    }
	
	private List<Board> getBoardList(Connection conn,int pagenumber) throws SQLException {
		List<Board> boardlist = new ArrayList<>();
		String sql = "{call view_board(?,?)}";
		CallableStatement view_board = conn.prepareCall(sql);
		view_board.setInt(1,pagenumber);
		view_board.registerOutParameter(2, OracleTypes.CURSOR);
		view_board.execute();
		ResultSet rs = (ResultSet) view_board.getObject(2);
		if (!rs.isBeforeFirst()) {
		    return null;
		}
		while(rs.next()) {
			Board board = set_Board(rs.getInt("rowbno"),rs.getInt("bno"),rs.getString("bwriter"),rs.getString("bname"),
					                rs.getInt("bread"),rs.getTimestamp("bdate").toLocalDateTime(),
					                rs.getString("bpw"),null);
			boardlist.add(board);
		}
		rs.close();
		return boardlist;
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
					Optional<Info> optionalInfo = findId(list, sc.nextLine());
					if(!optionalInfo.isPresent()) System.out.println("삭제할 아이디가 존재하지 않습니다");
					else {
						doSessionInfo(conn,optionalInfo.get().getId());
						list.remove(optionalInfo.get());
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
	               .filter(info -> info.getId().equals(id)) 
	               .findFirst(); 
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
			Info info = Login.set_Info(rs.getString("id"),rs.getString("pw"),rs.getString("name")
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
		return info.getPw();
	}
}
