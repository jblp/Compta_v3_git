package activity;

import utilities.User;
import BDD.IUsersBDD;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compta_v3.R;

public class HomeActivity extends Activity {

	private int idUser;
	private String pseudo = null;
	private User user = new User();
	
	private TextView actionBar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		actionBar = (TextView)findViewById(R.id.actionBarPseudo);
		
		// Récupération de l'intent
		Intent mainIntent = getIntent();
		
		// Récupération des extras dans un objet user
		user.setId(mainIntent.getStringExtra("IDUSER"));
		user.setPseudo(pseudo = mainIntent.getStringExtra("PSEUDO"));		
		
		actionBar.setText(pseudo);
	}
	
	public void deconnexion(View v) {
		//Création d'une instance de ma classe LivresBDD
        IUsersBDD iUserBdd = new IUsersBDD(this);
        //On ouvre la base de données pour écrire dedans
        iUserBdd.open();
        
        // On enlève toute trace de remember dans la bdd
        iUserBdd.setRememberToVoidBdd();
        
        // On ferme la connexion à la bdd
        iUserBdd.close();
        
        finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
}