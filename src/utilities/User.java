package utilities;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class User {
	
	private String id;
	private String pseudo = null;
	private String mdp = null;
	private String mail = null;
	private int banque = 0;
	private int comptable = 0;
	private String remember = null; // Format = YYYY-MM-DD HH:MM:SS
	private boolean authenticate = false;
	
	
	// Variables nécessaires au hashage SAH-1
	private static final String HEX_DIGITS = "0123456789abcdef";
	private static final int BYTE_MSK = 0xFF;
    private static final int HEX_DIGIT_MASK = 0xF;
    private static final int HEX_DIGIT_BITS = 4;
	
    
    // Constructeur par défaut
    public User () {}
    
    
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPseudo() {
		return pseudo;
	}
	
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	
	public void setMdp(String mdp) {
		this.mdp = computeSha1OfString("kwsfv,DFVS>BV?nvsnvjs<VSD541a1-") + computeSha1OfString(mdp) + computeSha1OfString("Msfokv,xBVDNvnvnxnv4521dvnjfvDD,_-");
	}
	
	public String getMdp() {
		return mdp;
	}


	public String getMail() {
		return mail;
	}
	
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	public int getBanque() {
		return banque;
	}
	
	public void setBanque(int banque) {
		this.banque = banque;
	}
	
	public int getComptable() {
		return comptable;
	}
	
	public void setComptable(int comptable) {
		this.comptable = comptable;
	}
	
	public String getRemember() {
		return remember;
	}

	public void setRemember(String remember) {
		this.remember = remember;
	}

	private boolean isAuthenticate() {
		this.mdp = null;
		
		return authenticate;
	}
	
	public boolean getAuthenticate() {
		return authenticate;
	}
	
	public void setAuthenticate(boolean authenticate) {
		this.authenticate = authenticate;
	}
	
	// Fonction de déconnexion
	public void deconnexion (int idUser) {
		
	}
	

	// Les 3 fonctions suivantes permettent d'obtenir le même hash SHA-1 qu'avec PHP
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