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
		//On créer la BDD et sa table
		maBaseSQLite = new MaBaseSQLite(context, NOM_BDD, null, VERSION_BDD);
	}
 
	public void open(){
		//on ouvre la BDD en écriture
		bdd = maBaseSQLite.getWritableDatabase();
	}
 
	public void close(){
		//on ferme l'accès à la BDD
		bdd.close();
	}
 
	public SQLiteDatabase getBDD(){
		return bdd;
	}
 
	public long insertIUser(IUser iUser){
		//Création d'un ContentValues (fonctionne comme une HashMap)
		ContentValues values = new ContentValues();
		//on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
		values.put(COL_IDUSER, iUser.getIdUser());
		values.put(COL_PSEUDO, iUser.getPseudo());
		values.put(COL_REMEMBER, iUser.getRemember());
		//on insère l'objet dans la BDD via le ContentValues
		return bdd.insert(TABLE_IUSER, null, values);
	}
 
	public int updateIUser(int id, IUser iUser){
		//La mise à jour d'un IUser dans la BDD fonctionne plus ou moins comme une insertion
		//il faut simple préciser quelle IUser on doit mettre à jour grâce à l'ID
		ContentValues values = new ContentValues();
		values.put(COL_IDUSER, iUser.getIdUser());
		values.put(COL_PSEUDO, iUser.getPseudo());
		values.put(COL_REMEMBER, iUser.getRemember());
		return bdd.update(TABLE_IUSER, values, COL_ID + " = " + id, null);
	}
 
	public int removeIUserWithIdUser(int idUser){
		//Suppression d'un livre de la BDD grâce à l'ID
		return bdd.delete(TABLE_IUSER, COL_IDUSER + " = " + idUser, null);
	}
 
	public IUser getIUserWithRemember(){
		//Récupère dans un Cursor les valeur correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
		Cursor c = bdd.rawQuery("select * from " + TABLE_IUSER + " WHERE " + COL_REMEMBER + " IS NOT NULL", new String[]{});
		
		return cursorToIUser(c);
	}
 
	public IUser getIUserWithIdUser(int idUser){
		//Récupère dans un Cursor les valeur correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
		Cursor c = bdd.rawQuery("select * from " + TABLE_IUSER + " WHERE " + COL_IDUSER + " = " + idUser, new String[]{});
		
		return cursorToIUser(c);
	}
	
	//Cette méthode permet de convertir un cursor en un livre
	private IUser cursorToIUser(Cursor c){
		//si aucun élément n'a été retourné dans la requête, on renvoie null
		if (c.getCount() == 0)
			return null;
 
		//Sinon on se place sur le premier élément
		c.moveToFirst();
		//On créé un livre
		IUser iUser = new IUser();
		//on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
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
		// Mise de toutes les champs remember de la table à null
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
		// Mise de toutes les champs remember de la table à null
		ContentValues values = new ContentValues();
		values.putNull(COL_REMEMBER);
		bdd.update(TABLE_IUSER, values, null, null);
		values.clear();
	}
}
