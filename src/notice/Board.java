package notice;

import java.time.LocalDateTime;

public class Board {
	private int rowbno;
	private int bno;
	private	String bwriter;
	private	String bname;
	private	int bread;
	private	LocalDateTime bdate;	
	private String bpw;
	private String bdetail;

	public Board() {
		
	}

	public int getBno() {
		return bno;
	}
	public void setBno(int bno) {
		this.bno = bno;
	}
	public String getBwriter() {
		return bwriter;
	}
	public void setBwriter(String bwriter) {
		this.bwriter = bwriter;
	}
	public String getBname() {
		return bname;
	}
	public void setBname(String bname) {
		this.bname = bname;
	}
	public int getBread() {
		return bread;
	}
	public void setBread(int bread) {
		this.bread = bread;
	}

	public LocalDateTime getBdate() {
		return bdate;
	}

	public void setBdate(LocalDateTime localDateTime) {
		this.bdate = localDateTime;
	}
	
	public String getBpw() {
		return bpw;
	}

	public void setBpw(String bpw) {
		this.bpw = bpw;
	}
	
	public String getBdetail() {
		return bdetail;
	}

	public void setBdetail(String bdetail) {
		this.bdetail = bdetail;
	}

	public int getRowbno() {
		return rowbno;
	}

	public void setRowbno(int rowbno) {
		this.rowbno = rowbno;
	}
	
}
