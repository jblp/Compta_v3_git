package main;

import BDD.IUser;
import BDD.IUsersBDD;
import HTTP.HTTPBddUser;
import activity.HomeActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compta_v3.R;

public class MainActivity extends Activity {
	
	public IUser iUser = null;
	private TextView pseudo = null;
	private TextView mdp = null;
	private CheckBox rmb = null;
	private boolean isConnected = false;
	public final static int REQUEST_CODE = 0;
	
	private MyAsyncTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Récupération d'un éventuel utilisateur à connecter automatiquement
        iUser = existRemember();
        
        if (iUser != null){// && iUser.timeOut()) {
        	homeIntent(String.valueOf(iUser.getIdUser()), iUser.getPseudo());
        }
        else {
        	setAuthenticateLayout();
        }
    }
    
    private OnKeyListener okListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
        	if(keyCode == 66 && event.getAction() == KeyEvent.ACTION_DOWN)
        		connexion(v);
        	
        	return false;
        }
    };
    
    // Fonction de connexion d'un utilisateur par authentification
    public void connexion (View v) {
    	// Surveillance de l'état de connection au réseau internet
    	ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	
        if(networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
        	isConnected = true;
        }
        else {
        	isConnected = false;
        }
    	
    	// Tous les champs sont remplis et on a du réseau internet
    	if (!pseudo.getText().toString().equals("") && !mdp.getText().toString().equals("") && isConnected) {
    		// On remplit l'utilisateur avec les données
    		iUser = new IUser();
    		iUser.setPseudo(pseudo.getText().toString());
    		iUser.setMdp(mdp.getText().toString());
        	
        	// Lancement de la tâche asynchrone
        	task = new MyAsyncTask();
        	task.setPseudo(iUser.getPseudo());
        	task.setPwd(iUser.getMdp());
        	
        	// Suppression du mdp dans l'objet user
        	iUser.setMdpToNull();
        	
			task.execute();
    	}
    	else if (!isConnected){
    		Toast.makeText(MainActivity.this, "Aucune connexion internet disponible, Mode hors ligne activé !", Toast.LENGTH_LONG).show();
    	}
    	else
    		Toast.makeText(MainActivity.this, "Veuillez renseigner tous les champs", Toast.LENGTH_LONG).show();
    }
    
    // Création de l'itent qui affiche l'activité de l apage d'accueil
    private void homeIntent (String idUser, String pseudo) {
    	// Création de l'intent
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        
        // Ajout d'un extra
        intent.putExtra("idUser", iUser.getIdUser());
        intent.putExtra("pseudo", iUser.getPseudo());
        
        // Lancement de l'intent
        startActivityForResult(intent, REQUEST_CODE);
    }
    
    // Fonction appelée pour récupérer le retour de l'intent envoyé à Home_Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      // On vérifie tout d'abord à quel intent on fait référence ici à l'aide de notre identifiant
      if (requestCode == REQUEST_CODE) {
        // On vérifie aussi que l'opération s'est bien déroulée
        if (resultCode == RESULT_OK) {
        	// On a déconnecté proprement l'utilisateur
        	// On affiche donc la page d'authentification
        	setAuthenticateLayout();
        }
        else {
        	// L'activité Home aura été quittée avec le bouton "back"
        	// On ferme donc l'application
        	finish();
        }
      }
    }
    
    private void setAuthenticateLayout() {
    	// Chargement du layout d'authentification
        setContentView(R.layout.authenticate);
        
        // Récupération des widgets nécessaires
        pseudo = (TextView)findViewById(R.id.pseudo);
        mdp = (TextView)findViewById(R.id.mdp);
        rmb = (CheckBox)findViewById(R.id.remember);
        
        // Attribution des listeners
        mdp.setOnKeyListener(okListener);
    }
    
    private IUser existRemember() {
    	IUser iUserExiste = null;
    	
    	//Création d'une instance de la classe IUsersBDD
        IUsersBDD iUserBdd = new IUsersBDD(this);
        //On ouvre la base de données pour écrire dedans
        iUserBdd.open();      
        // Récupération du résultat de la requête BDD
        iUserExiste = iUserBdd.getIUserWithRemember();
        // Fermeture de la BDD
        iUserBdd.close();
        
        return iUserExiste;
    }
    
    public void newCompte(View v) {
    	Toast.makeText(MainActivity.this, "Création d'un nouveau compte", Toast.LENGTH_LONG).show();
    }
    
    public void forgetPwd(View v) {
		Toast.makeText(MainActivity.this, "régénération d'un nouveau mdp", Toast.LENGTH_LONG).show();
    }
    
    private class MyAsyncTask extends AsyncTask<Void, Integer, IUser>
    {
    	private ProgressDialog dialogWait	=	null;
    	private String pseudoAsync 			=	null;
    	private String pwdAsync 			=	null;
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		// Lancement d'une boite de dialogue d'attente
    		dialogWait = new ProgressDialog(MainActivity.this);
    		dialogWait.setMessage("Authentification...");
    		dialogWait.setCancelable(true);
    		dialogWait.show();
    	}

    	@Override
    	protected IUser doInBackground(Void... arg0) {
    		// Création de l'objet en charge de la requête HTTP
    		HTTPBddUser httpBdd = new HTTPBddUser();
    		
    		try {
				return httpBdd.getUserWithPseudo(pseudoAsync, pwdAsync);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
    	}
    	
    	@Override
    	protected void onPostExecute(IUser result) {
    		// Fermeture de la boîte de dialogue
    		iUser = result;
    		dialogWait.cancel();
    		    		
    		// Test de la validité des données
        	if (iUser != null) {
        		iUser.setAuthenticate(true);
        		
        		// Mise à jour de l'utilisateur en BDD intèrne
        		//Création d'une instance de ma classe IUsersBDD
                IUsersBDD iUserBdd = new IUsersBDD(MainActivity.this);
                iUserBdd.open();
        		
                // Lancement de la commande d'update en BDD intèrne
                iUserBdd.updateIUser(iUser);           
                
        		// Si l'utilisateur veut resté conencté, on modifie la BDD Iuser
    	        if (rmb.isChecked() && iUser.getAuthenticate()) {
    	        	// On change son statut de connexion automatique
    	        	iUser.setIsRemember(true);
    	        	
                    // Permet de mettre tous les users à Remember = null et de mettre à jour celui en cours de connexion
                    iUserBdd.updateRememberBdd(iUser, iUser.getDate());
    	        }
    	        
    	        iUserBdd.close();
    	        
    	        //Lancement de l'intent pour passer à l'accueil
    	        homeIntent(iUser.getIdUser(), iUser.getPseudo());
        	}
        	else {
        		Toast.makeText(MainActivity.this, "Les identifiants ne sont pas corrects", Toast.LENGTH_SHORT).show();
        	}
    	}
    	
    	public void setPseudo(String pseudo) {
    		this.pseudoAsync = pseudo;
    	}
    	
    	public void setPwd(String pwd) {
    		this.pwdAsync = pwd;
    	}
    }
}
