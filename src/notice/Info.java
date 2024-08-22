package notice;

public class Info {
	private String id;
	private String pw;
	private String name;
	private String tel;
	private String adress;
	private int sexual;
	private int authority;
	
	public void setId(String id) {
		this.id = id;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getId() {
		return id;
	}
	public String getPw() {
		return pw;
	}
	public String getName() {
		return name;
	}
	public String getTel() {
		return tel;
	}
	public String getAdress() {
		return adress;
	}
	public int getSexual() {
		return sexual;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public void setSexual(int sexual) {
		this.sexual = sexual;
	}
	@Override
	public String toString() {
		return "Info [id=" + id + ",  name=" + name + ", tel=" + tel + ", adress=" + adress + ", sexual="
				+ viewSexual() + "]";
	}
	public String viewSexual() {
		if(sexual== 1) return"남자";
		else if(sexual== 2)return "여자";
		else return "잘못 입력된 성별";
	}
	public int getAuthority() {
		return authority;
	}
	public void setAuthority(int authority) {
		this.authority = authority;
	}
	
	
}
