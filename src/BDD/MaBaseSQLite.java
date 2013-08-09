// sources :
//		- http://www.tutorialspoint.com/sqlite/sqlite_null_values.htm
//		- http://www.tutomobile.fr/comment-utiliser-sqlite-sous-android-tutoriel-android-n%C2%B019/19/10/2010/

package BDD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MaBaseSQLite extends SQLiteOpenHelper {
	 
	private static final String TABLE_IUSER = "table_iuser";
	private static final String COL_ID = "ID";
	private static final String COL_IDUSER = "IdUser";
	private static final String COL_PSEUDO = "Pseudo";
	private static final String COL_REMEMBER = "Remember";
	private static final String COL_MAIL = "Mail";
	private static final String COL_COMPTABLE = "Comptable";
	private static final String COL_BANQUE = "Banque";
	private static final String COL_CATEGORIES = "Categories";
 
	private static final String CREATE_BDD = "CREATE TABLE " + TABLE_IUSER + " ("
	+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_IDUSER + " INTEGER NOT NULL, "
	+ COL_PSEUDO + " TEXT NOT NULL, " + COL_REMEMBER + " TEXT, " + COL_MAIL + " TEXT, "
	+ COL_COMPTABLE + " INTEGER, " + COL_BANQUE + " INTEGER, " + COL_CATEGORIES + " TEXT);";
 
	public MaBaseSQLite(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		//on créé la table à partir de la requête écrite dans la variable CREATE_BDD
		db.execSQL(CREATE_BDD);
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//On peut fait ce qu'on veut ici moi j'ai d�cid� de supprimer la table et de la recr�er
		//comme �a lorsque je change la version les id repartent de 0
		db.execSQL("DROP TABLE " + TABLE_IUSER + ";");
		onCreate(db);
	}
 
}