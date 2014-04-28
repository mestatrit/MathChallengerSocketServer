package it.mathchallenger.server.entities;

public class Account {
	private String username, authcode, email;
	private int id;
	
	public Account(){}
	public Account(String user, String auth){
		setUsername(user);
		setAuthCode(auth);
	}
	public String getUsername(){
		return username;
	}
	public String getAuthCode(){
		return authcode;
	}
	public void setUsername(String u){
		username=u;
	}
	public void setAuthCode(String a){
		authcode=a;
	}
	public int getID(){
		return id;
	}
	public void setID(int i){
		id=i;
	}
	public String getEmail(){
		return email;
	}
	public void setEmail(String email){
		this.email=email;
	}
}
