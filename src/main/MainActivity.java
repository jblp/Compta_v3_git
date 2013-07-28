package main;

import utilities.User;
import BDD.IUser;
import BDD.IUsersBDD;
import HTTP.HTTPBdd;
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
	public User user = null;
	private TextView pseudo = null;
	private TextView mdp = null;
	private CheckBox rmb = null;
	private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Récupération d'un éventuel utilisateur à connecter automatiquement
        iUser = existRemember();
        
        if (iUser != null && iUser.timeOut()) {
        	homeIntent(iUser.getIdUser(), iUser.getPseudo());
        }
        else {
        	setAuthenticateLayout();
        }
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
    	
    	// Chargement du layout et de tout ce qu'il a besoin pour fonctionner
    	setAuthenticateLayout();
    }
    
    private OnKeyListener okListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
        	if(keyCode == 66 && event.getAction() == KeyEvent.ACTION_DOWN)
        		connexion(v);
        	
        	return false;
        }
    };
    
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
    		user = new User();
    		user.setPseudo(pseudo.getText().toString());
        	user.setMdp(mdp.getText().toString());
        	
        	// Lancement de la tâche asynchrone
        	MyAsyncTask task = new MyAsyncTask();
        	task.setPseudo(user.getPseudo());
        	task.setPwd(user.getMdp());
			task.execute();
        	
        	// Test de la validité des données
        	if (user.getPseudo().equals("jb") && mdp.getText().toString().equals("a")) {
        		user.setAuthenticate(true);
        		
        		// Si l'utilisateur veut resté conencté, on modifie la BDD Iuser
    	        if (rmb.isChecked() && user.getAuthenticate()) {
    	        	// On remplit un IUser pour modiffier la bdd
    	        	iUser = new IUser(user.getId(), true, pseudo.getText().toString());
    	        	
    	        	//Création d'une instance de ma classe LivresBDD
                    IUsersBDD iUserBdd = new IUsersBDD(this);
                    iUserBdd.open();
                    // Permet de mettre tous les users à Remember = null et de mettre à jour celui en cours de connexion
                    iUserBdd.updateRememberBdd(iUser, iUser.getDate());
                    iUserBdd.close();
    	        }
        		
    	        // Lancement de l'intent pour passer à l'accueil
    	        homeIntent(user.getId(), user.getPseudo());
        	}
        	else {
        		Toast.makeText(MainActivity.this, "Les identifiants ne sont pas correctes", Toast.LENGTH_SHORT).show();
        	}
    	}
    	else if (!isConnected){
    		Toast.makeText(MainActivity.this, "Aucune connexion internet disponible !", Toast.LENGTH_LONG).show();
    	}
    	else
    		Toast.makeText(MainActivity.this, "Veuillez renseigner tous les champs", Toast.LENGTH_LONG).show();
    }
    
    private void homeIntent (int idUser, String pseudo) {
    	// Création de l'intent
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        // Ajout d'un extra
        intent.putExtra("IDUSER", idUser);
        intent.putExtra("PSEUDO", pseudo);
        // Lancement de l'intent
        startActivity(intent);
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
    	
    	//Création d'une instance de ma classe LivresBDD
        IUsersBDD iUserBdd = new IUsersBDD(this);
 
        //On ouvre la base de données pour écrire dedans
        iUserBdd.open();      
        
        // Récupération du résultat de la requête BDD
        iUserExiste = iUserBdd.getIUserWithRemember();
        
        // Fermeture de la BDD
        iUserBdd.close();
        
        return iUserExiste;
    }
    
    private class MyAsyncTask extends AsyncTask<Void, Integer, String>
    {

    	private ProgressDialog dialogWait;
    	private String pseudo = null;
    	private String pwd = null;
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		// Lancement d'une boite de dialogue d'attente
    		dialogWait = ProgressDialog.show(MainActivity.this, "", "Authentification...", true);
    	}

    	@Override
    	protected String doInBackground(Void... arg0) {
    		// Création de l'objet en charge de la requete HTTP
    		HTTPBdd httpBdd = new HTTPBdd();
    		
    		// Demande de réponse du serveur
//    		User userServeur = httpBdd.getUserWithPseudo(pseudo, pwd);
    		
    		return httpBdd.getUserWithPseudo(pseudo, pwd);
    	}

    	@Override
    	protected void onPostExecute(String result) {
    		// Fermeture de la boîte de dialogue
    		dialogWait.dismiss();
    		
    		// Test du résultat de la requêtte
    		if(result != null) {
    			Toast.makeText(getApplicationContext(), "Le traitement asynchrone est terminé : " + result, Toast.LENGTH_LONG).show();
    		}
    		else {
    			Toast.makeText(getApplicationContext(), "Le traitement asynchrone est terminé : Echec de la connexion au serveur", Toast.LENGTH_LONG).show();
    		}
    	}
    	
    	public void setPseudo(String pseudo) {
    		this.pseudo = pseudo;
    	}
    	
    	public void setPwd(String pwd) {
    		this.pwd = pwd;
    	}
    }
}
