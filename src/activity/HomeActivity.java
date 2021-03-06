package activity;

import BDD.User;
import BDD.UserBDD;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compta_v3.R;

public class HomeActivity extends Activity implements android.view.View.OnClickListener {
	
	private User user;
	private TextView actionBar		= null;
	
	private int ACTIVITY_OPERATION	= 1;
	private int ACTIVITY_PERIODE	= 2;
	private int ACTIVITY_SYNTHESE	= 3;
	private int ACTIVITY_COMPTE		= 4;
	private int ACTIVITY_SETTINGS	= 5;
	private int ACTIVITY_ABOUT		= 6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		actionBar = (TextView)findViewById(R.id.actionBarPseudo);
		
		// Récupération de l'intent
		Intent mainIntent = getIntent();
		
		// Récupération des extras dans un objet user
		user = new User();
		user.setPseudo(mainIntent.getStringExtra("pseudo"));
		user.setIdUser(mainIntent.getStringExtra("idUser"));
		
		// On remplit l'actionBar
		actionBar.setText(user.getPseudo());
		
		findViewById(R.id.boperation).setOnClickListener(this);
		findViewById(R.id.bperiodique).setOnClickListener(this);
		findViewById(R.id.bsynth).setOnClickListener(this);
		findViewById(R.id.bcpt).setOnClickListener(this);
		findViewById(R.id.bparam).setOnClickListener(this);
		findViewById(R.id.babout).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.boperation:
			personalIntent(user, ACTIVITY_OPERATION);
			break;
		case R.id.bperiodique:
			Toast.makeText(this, "periodique", Toast.LENGTH_SHORT).show();
			break;
		case R.id.bsynth:
			Toast.makeText(this, "syntheses", Toast.LENGTH_SHORT).show();
			break;
		case R.id.bcpt:
			Toast.makeText(this, "compte", Toast.LENGTH_SHORT).show();
			break;
		case R.id.bparam:
			Toast.makeText(this, "parametres", Toast.LENGTH_SHORT).show();
			break;
		case R.id.babout:
			Toast.makeText(this, "à propos", Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	// Création de l'itent qui affiche l'activité correspondante au choix de l'utilisateur via le menu
    private void personalIntent (User user, int activity) {
    	// Création de l'intent en fonction de l'action déclenchée
        Intent intent = new Intent(HomeActivity.this, OperationActivity.class);
        // Ajout d'un extra
        intent.putExtra("idUser", user.getIdUser());
        
        // Lancement de l'intent
        startActivity(intent);
    }
	
	public void deconnexion(View v) {
		//Création d'une instance de ma classe LivresBDD
        UserBDD iUserBdd = new UserBDD(this);
        //On ouvre la base de donn�es pour �crire dedans
        iUserBdd.open();
        
        // On enlève toute trace de remember dans la bdd
        iUserBdd.setRememberToVoidBdd();
        
        // On ferme la connexion à la bdd
        iUserBdd.close();
        
        // Création de l'intent de retour
        Intent result = new Intent();
        result.putExtra("ACTION", "QUIT");
        setResult(RESULT_OK, result);
        
        // On ferme l'activité
        finish();
	}
}