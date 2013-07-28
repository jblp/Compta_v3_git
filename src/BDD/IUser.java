package BDD;

import android.text.format.Time;

public class IUser {
	 
	private int id;
	private int idUser;
	private String pseudo;
	private String remember; // YYYYMMDD
	private boolean isRemember = false;
	
	static int TIME_OUT = 2;
 
	public IUser(){}
 
	public IUser(int idUser, Boolean isRemember, String pseudo){
		this.idUser = idUser;
		this.pseudo = pseudo;
		this.isRemember = isRemember;
		this.setRemember();
	}
 
	public int getId() {
		return id;
	}
 
	public void setId(int id) {
		this.id = id;
	}
	
	public int getIdUser() {
		return idUser;
	}
 
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
 
	public String getPseudo() {
		return pseudo;
	}
 
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
 
	public String getRemember() {
		return remember;
	}
 
	public void setRemember() {
		if (isRemember) {
			this.remember = getDate();
		}
		else {
			this.remember = null;
		}
	}
	
	public void setRememberFromBdd (String remember) {
		this.remember = remember;
	}
 
	public boolean isRemember() {
		return isRemember;
	}

	public void setIsRemember(boolean isRemember) {
		this.isRemember = isRemember;
	}
	
	public String getDate() {
		// Récupération de la date actuelle
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		
		return (today.format("%Y%m%d"));
	}
	
	public boolean timeOut() {
		int rememb = Integer.parseInt(remember);
		int now = Integer.parseInt(getDate());
		
		if(now - rememb <= TIME_OUT)		
			return true;
		else
			return false;
	}
}