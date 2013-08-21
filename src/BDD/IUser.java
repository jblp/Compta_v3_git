package BDD;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.text.format.Time;

public class IUser {
	 
	private String id				=	null;
	private String idUser			=	null;
	private String pseudo			=	null;
	private String mdp				=	null;
	private String mail				=	null;
	private float comptable			=	-1;
	private float banque			=	-1;
	private String[] categories		=	null;
	private String remember			=	null; // YYYYMMDD
	private boolean isRemember 		= 	false;
	private boolean authenticate 	= 	false;
	
	static int TIME_OUT = 2; // en jours
	
	// Variables nécessaires au hashage SAH-1
	private static final String HEX_DIGITS 		= "0123456789abcdef";
	private static final int 	BYTE_MSK		= 0xFF;
	private static final int 	HEX_DIGIT_MASK	= 0xF;
	private static final int 	HEX_DIGIT_BITS	= 4;

	public IUser(){}
 
	public IUser(String idUser, Boolean isRemember, String pseudo){
		this.idUser = idUser;
		this.pseudo = pseudo;
		this.isRemember = isRemember;
		this.setRemember();
	}
	
	public IUser(String[] param) {
		this.id			= param[0];
		this.idUser		= param[1];
		this.pseudo		= param[2];
		this.mail		= param[3];
		this.comptable	= Float.valueOf(param[4]);
		this.banque		= Float.valueOf(param[5]);
		this.categories	= param[6].split("/");
	}
 
	public String getId() {
		return id;
	}
 
	public void setId(String id) {
		this.id = id;
	}
	
	public String getIdUser() {
		return idUser;
	}
 
	public void setIdUser(String idUser) {
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

	public String getMdp() {
		return mdp;
	}

	public void setMdp(String mdp) {
		this.mdp = computeSha1OfString("kwsfv,DFVS>BV?nvsnvjs<VSD541a1-") + computeSha1OfString(mdp) + computeSha1OfString("Msfokv,xBVDNvnvnxnv4521dvnjfvDD,_-");
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public float getComptable() {
		return comptable;
	}

	public void setComptable(float comptable) {
		this.comptable = comptable;
	}

	public float getBanque() {
		return banque;
	}

	public void setBanque(float banque) {
		this.banque = banque;
	}
	
	public String getCategoriesString() {
		String categoriesString = categories[0];
		
		for(int i = 1; i < categories.length; i++) {
			categoriesString = "/" + categories[i];
		}
		
		return categoriesString;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories.split("/");
	}

	public void setRemember(String remember) {
		this.remember = remember;
	}
	
	public void setMdpToNull() {
		this.mdp = null;
	}
	
	public void setAuthenticate(boolean authenticate) {
		this.authenticate = authenticate;
	}
	
	public boolean getAuthenticate() {
		return authenticate;
	}
	
	public String userToString() {
		return id + " " + idUser + " " + pseudo + " " + mail + " " + String.valueOf(comptable/100) + " " + String.valueOf(banque/100);
	}
	
	// Les 3 fonctions suivantes permettent d'obtenir le m�me hash SHA-1 qu'avec PHP
	public static String computeSha1OfString(final String message) 
			throws UnsupportedOperationException, NullPointerException {
		try {
			return computeSha1OfByteArray(message.getBytes(("UTF-8")));
		} catch (UnsupportedEncodingException ex) {
			throw new UnsupportedOperationException(ex);
		}
	}

	private static String computeSha1OfByteArray(final byte[] message)
			throws UnsupportedOperationException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(message);
			byte[] res = md.digest();
			return toHexString(res);
		} catch (NoSuchAlgorithmException ex) {
			throw new UnsupportedOperationException(ex);
		}
	}

	private static String toHexString(final byte[] byteArray) {
		StringBuilder sb = new StringBuilder(byteArray.length * 2);
		for (int i = 0; i < byteArray.length; i++) {
			int b = byteArray[i] & BYTE_MSK;
			sb.append(HEX_DIGITS.charAt(b >>> HEX_DIGIT_BITS)).append(HEX_DIGITS.charAt(b & HEX_DIGIT_MASK));
		}
		return sb.toString();
	}
}