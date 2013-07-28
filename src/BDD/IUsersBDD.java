package BDD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class IUsersBDD {
	 
	private static final int VERSION_BDD = 2;
	private static final String NOM_BDD = "compta.v3.db";
 
	private static final String TABLE_IUSER = "table_iuser";
	private static final String COL_ID = "ID";
	private static final int NUM_COL_ID = 0;
	private static final String COL_IDUSER = "IdUser";
	private static final int NUM_COL_IDUSER = 1;
	private static final String COL_PSEUDO = "Pseudo";
	private static final int NUM_COL_PSEUDO = 2;
	private static final String COL_REMEMBER = "Remember";
	private static final int NUM_COL_REMEMBER = 3;
 
	private SQLiteDatabase bdd;
 
	private MaBaseSQLite maBaseSQLite;
 
	public IUsersBDD(Context context){
		//On cr�er la BDD et sa table
		maBaseSQLite = new MaBaseSQLite(context, NOM_BDD, null, VERSION_BDD);
	}
 
	public void open(){
		//on ouvre la BDD en �criture
		bdd = maBaseSQLite.getWritableDatabase();
	}
 
	public void close(){
		//on ferme l'acc�s � la BDD
		bdd.close();
	}
 
	public SQLiteDatabase getBDD(){
		return bdd;
	}
 
	public long insertIUser(IUser iUser){
		//Cr�ation d'un ContentValues (fonctionne comme une HashMap)
		ContentValues values = new ContentValues();
		//on lui ajoute une valeur associ� � une cl� (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
		values.put(COL_IDUSER, iUser.getIdUser());
		values.put(COL_PSEUDO, iUser.getPseudo());
		values.put(COL_REMEMBER, iUser.getRemember());
		//on ins�re l'objet dans la BDD via le ContentValues
		return bdd.insert(TABLE_IUSER, null, values);
	}
 
	public int updateIUser(int id, IUser iUser){
		//La mise � jour d'un IUser dans la BDD fonctionne plus ou moins comme une insertion
		//il faut simple pr�ciser quelle IUser on doit mettre � jour gr�ce � l'ID
		ContentValues values = new ContentValues();
		values.put(COL_IDUSER, iUser.getIdUser());
		values.put(COL_PSEUDO, iUser.getPseudo());
		values.put(COL_REMEMBER, iUser.getRemember());
		return bdd.update(TABLE_IUSER, values, COL_ID + " = " + id, null);
	}
 
	public int removeIUserWithIdUser(int idUser){
		//Suppression d'un livre de la BDD gr�ce � l'ID
		return bdd.delete(TABLE_IUSER, COL_IDUSER + " = " + idUser, null);
	}
 
	public IUser getIUserWithRemember(){
		//R�cup�re dans un Cursor les valeur correspondant � un livre contenu dans la BDD (ici on s�lectionne le livre gr�ce � son titre)
		Cursor c = bdd.rawQuery("select * from " + TABLE_IUSER + " WHERE " + COL_REMEMBER + " IS NOT NULL", new String[]{});
		
		return cursorToIUser(c);
	}
 
	public IUser getIUserWithIdUser(int idUser){
		//R�cup�re dans un Cursor les valeur correspondant � un livre contenu dans la BDD (ici on s�lectionne le livre gr�ce � son titre)
		Cursor c = bdd.rawQuery("select * from " + TABLE_IUSER + " WHERE " + COL_IDUSER + " = " + idUser, new String[]{});
		
		return cursorToIUser(c);
	}
	
	//Cette m�thode permet de convertir un cursor en un livre
	private IUser cursorToIUser(Cursor c){
		//si aucun �l�ment n'a �t� retourn� dans la requ�te, on renvoie null
		if (c.getCount() == 0)
			return null;
 
		//Sinon on se place sur le premier �l�ment
		c.moveToFirst();
		//On cr�� un livre
		IUser iUser = new IUser();
		//on lui affecte toutes les infos gr�ce aux infos contenues dans le Cursor
		iUser.setId(c.getInt(NUM_COL_ID));
		iUser.setIdUser(c.getInt(NUM_COL_IDUSER));
		iUser.setPseudo(c.getString(NUM_COL_PSEUDO));
		iUser.setRememberFromBdd(c.getString(NUM_COL_REMEMBER));
		//On ferme le cursor
		c.close();
 
		//On retourne le livre
		return iUser;
	}
	
	public void updateRememberBdd(IUser iUser, String date) {
		// Mise de toutes les champs remember de la table � null
		setRememberToVoidBdd();
		
		if(this.getIUserWithIdUser(iUser.getIdUser()) != null) {
			ContentValues values = new ContentValues();
			values.put(COL_REMEMBER, date);
			bdd.update(TABLE_IUSER, values, COL_IDUSER + " = " + iUser.getIdUser(), null);
			values.clear();
		}
		else {
			this.insertIUser(iUser);
		}
	}
	
	public void setRememberToVoidBdd() {
		// Mise de toutes les champs remember de la table � null
		ContentValues values = new ContentValues();
		values.putNull(COL_REMEMBER);
		bdd.update(TABLE_IUSER, values, null, null);
		values.clear();
	}
}
