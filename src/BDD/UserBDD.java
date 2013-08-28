package BDD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserBDD {
	 
	private static final int VERSION_BDD = 1;
	private static final String NOM_BDD = "compta.v3.db";
 
	private static final String TABLE_USER 				= "table_user";
	private static final String COL_ID 					= "ID";
	private static final int NUM_COL_ID 				= 0;
	private static final String COL_IDUSER 				= "IdUser";
	private static final int NUM_COL_IDUSER 			= 1;
	private static final String COL_PSEUDO 				= "Pseudo";
	private static final int NUM_COL_PSEUDO 			= 2;
	private static final String COL_MDP 				= "Mdp";
	private static final int NUM_COL_MDP 				= 3;
	private static final String COL_REMEMBER 			= "Remember";
	private static final int NUM_COL_REMEMBER 			= 4;
	private static final String COL_MAIL 				= "Mail";
	private static final int NUM_COL_MAIL 				= 5;
	private static final String COL_COMPTABLE 			= "Comptable";
	private static final int NUM_COL_COMPTABLE 			= 6;
	private static final String COL_BANQUE 				= "Banque";
	private static final int NUM_COL_BANQUE 			= 7;
	private static final String COL_CATEGORIES 			= "Categories";
	private static final int NUM_COL_CATEGORIES 		= 8;
	private static final String COL_LIMIT_OPERATIONS 	= "Limite";
	private static final int NUM_COL_LIMIT_OPERATIONS 	= 9;
 
	private SQLiteDatabase bdd;
 
	private MaBaseSQLite maBaseSQLite;
 
	public UserBDD(Context context){
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
 
	public long insertUser(User user){
		//Création d'un ContentValues (fonctionne comme une HashMap)
		ContentValues values = new ContentValues();
		//on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
		values.put(COL_IDUSER, user.getIdUser());
		values.put(COL_PSEUDO, user.getPseudo());
		values.put(COL_MDP, user.getHashMdp());
		values.put(COL_COMPTABLE, user.getComptable());
		values.put(COL_BANQUE, user.getBanque());
		values.put(COL_CATEGORIES, user.getCategoriesString());
		values.put(COL_REMEMBER, user.getRemember());
		values.put(COL_LIMIT_OPERATIONS, "30");
		//on insère l'objet dans la BDD via le ContentValues
		return bdd.insert(TABLE_USER, null, values);
	}
 
	public int updateUser(User user){
		//La mise à jour d'un User dans la BDD fonctionne plus ou moins comme une insertion
		//il faut simplement préciser quelle User on doit mettre à jour gràce à l'ID
		ContentValues values = new ContentValues();
		values.put(COL_PSEUDO, user.getPseudo());
		values.put(COL_MDP, user.getHashMdp());
		values.put(COL_MAIL, user.getMail());
		values.put(COL_BANQUE, user.getBanque());
		values.put(COL_COMPTABLE, user.getComptable());
		values.put(COL_CATEGORIES, user.getCategoriesString());
		
		return bdd.update(TABLE_USER, values, COL_IDUSER + " = " + user.getIdUser(), null);
	}
	
	public void updateLimit(String idUser, String limit) {
		ContentValues values = new ContentValues();
		values.put(COL_LIMIT_OPERATIONS, limit);
		bdd.update(TABLE_USER, values, COL_IDUSER + " = " + idUser, null);
		values.clear();
	}
	
	public int removeUserWithIdUser(String idUser){
		//Suppression d'un livre de la BDD grâce à l'ID
		return bdd.delete(TABLE_USER, COL_IDUSER + " = " + idUser, null);
	}
	
	public User getUserWithRemember(){
		//Récupère dans un Cursor les valeur correspondant à un user contenu dans la BDD (ici on sélectionne l'utilisateur grâce à son remember)
		Cursor c = bdd.rawQuery("select * from " + TABLE_USER + " WHERE " + COL_REMEMBER + " IS NOT NULL", new String[]{});
		
		User user = cursorToUser(c);
		
		if (user != null) {
			user = this.getUserWithIdUserAndPwd(user.getIdUser(), user.getHashMdp());
		}
		
		return user;
	}
 
	public User getUserWithIdUser(String idUser){
		//Récupére dans un Cursor les valeur correspondant à un User contenu dans la BDD (ici on s�lectionne le livre gr�ce � son titre)
		Cursor c = bdd.rawQuery("select * from " + TABLE_USER + " WHERE " + COL_IDUSER + " = ?", new String[]{idUser});
		
		return cursorToUser(c);
	}
	
	public User getUserWithIdUserAndPwd (String idUser, String pwd){
		//Récupére dans un Cursor les valeur correspondant à un User contenu dans la BDD (ici on s�lectionne le livre gr�ce � son titre)
		Cursor c = bdd.rawQuery("select * from " + TABLE_USER + " WHERE " + COL_IDUSER + " = ? AND " + COL_MDP + " = ?", new String[]{idUser, pwd});
		
		return cursorToUser(c);
	}
	
	public User getUserWithPseudoAndPwd (String pseudo, String pwd){
		//Récupére dans un Cursor les valeur correspondant à un User contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
		Cursor c = bdd.rawQuery("select * from " + TABLE_USER + " WHERE " + COL_PSEUDO + " = ? AND " + COL_MDP + " =?", new String[]{pseudo, pwd});
		
		return cursorToUser(c);
	}
	
	//Cette méthode permet de convertir un cursor en un User
	private User cursorToUser(Cursor c){
		//si aucun élément n'a été retourné dans la requête, on renvoie null
		if (c.getCount() == 0)
			return null;
 
		//Sinon on se place sur le premier élément
		c.moveToFirst();
		//On créé un livre
		User user = new User();
		//on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
		user.setId(c.getString(NUM_COL_ID));
		user.setIdUser(c.getString(NUM_COL_IDUSER));
		user.setPseudo(c.getString(NUM_COL_PSEUDO));
		user.setHashMdp(c.getString(NUM_COL_MDP));
		user.setRememberFromBdd(c.getString(NUM_COL_REMEMBER));
		user.setMail(c.getString(NUM_COL_MAIL));
		user.setComptable(c.getInt(NUM_COL_COMPTABLE));
		user.setBanque(c.getInt(NUM_COL_BANQUE));
		user.setCategories(c.getString(NUM_COL_CATEGORIES));
		user.setLimit(c.getString(NUM_COL_LIMIT_OPERATIONS));
		//On ferme le cursor
		c.close();

		//On retourne l'utilisateur
		return user;
	}
	
	public void updateRememberBdd(String idUser, String date) {
		// Mise à jour de tous les champs remember de la table à null
		setRememberToVoidBdd();
		
		ContentValues values = new ContentValues();
		values.put(COL_REMEMBER, date);
		bdd.update(TABLE_USER, values, COL_IDUSER + " = " + idUser, null);
		values.clear();
	}
	
	public void setRememberToVoidBdd() {
		// Mise de toutes les champs remember de la table � null
		ContentValues values = new ContentValues();
		values.putNull(COL_REMEMBER);
		bdd.update(TABLE_USER, values, null, null);
		values.clear();
	}
}
