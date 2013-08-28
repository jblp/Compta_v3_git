package main;

import BDD.User;
import BDD.UserBDD;
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
	
	private User user = null;
	
	private TextView pseudo 						= null;
	private TextView mdp 							= null;
	private CheckBox rmb 							= null;
	
	private UserBDD userBdd							= null;
	
	private ConnectivityManager connectivityManager = null;
	private NetworkInfo networkInfo					= null;
	
	private AuthAsynchTask authTask 				= null;
	
	public final static int REQUEST_CODE 			= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Connexion à la BDD intèrne
        userBdd = new UserBDD(this);
        userBdd.open();
        // Récupération d'un éventuel user en auto-connect
        user = userBdd.getUserWithRemember();
        
        // Test d'une demande de connexion automatique
        // Si oui
        if (user != null) {
        	// On vérifie qu'il est bien autorisé à se connecter et on affiche l'activité d'accueil
        	homeIntent(user.getIdUser(), user.getPseudo());
        }
        // Si pas de connexion automatique
        else {
        	// On affiche la page de log-in
        	setAuthenticateLayout();
        }
        
        userBdd.close();
    }
    
    // Evènement lancé au click du bouton "Ok" sur le clavier du Mdp
    private OnKeyListener okListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
        	if(keyCode == 66 && event.getAction() == KeyEvent.ACTION_DOWN)
        		connexion(v);
        	
        	return false;
        }
    };
    
    // Fonction qui permet de lancer la connexion à un compte
    public void connexion (View v) {
    	// Surveillance de l'état de connection au réseau internet
    	connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
    	networkInfo = connectivityManager.getActiveNetworkInfo();
    	
    	// Vérification du bon remplissage de tous les champs de log-in
    	if (!pseudo.getText().toString().equals("") && !mdp.getText().toString().equals("")) {
    		// Remplissage de l'utilisateur avec le pseudo et le mdp renseignés
    		user = new User();
    		user.setPseudo(pseudo.getText().toString());
    		user.setMdp(mdp.getText().toString());
    		
    		// Test d'un accès à internet
    		if(networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
    			// Ici il y a accès à internet
    			// Vérification de la véracité des identifiants avec le serveur à l'aide de la tâche asynchrone
    			authTask = new AuthAsynchTask();
    			authTask.execute(user.getPseudo(), user.getHashMdp());
    		}
    		else {
    			// Ici il n'y a pas d'accès à internet
    			// Test de l'existance de l'utilisateur en BDD intèrne pour charger ses données perso
    			UserBDD userBdd = new UserBDD(this);
    			userBdd.open();
    			
    			user = userBdd.getUserWithPseudoAndPwd(user.getPseudo(), user.getHashMdp());
    			
    			if (user != null) {
    				Toast.makeText(getApplicationContext(), "Connexion au compte " + user.getPseudo() + " en mode hors ligne", Toast.LENGTH_SHORT).show();
    				
    				// Gestion de la case de connexion automatique
    				setAutoConnect(rmb);
    				
    				homeIntent(user.getIdUser(), user.getPseudo());
    			}
    			// Sinon on affiche un message d'erreur
    			else
    				Toast.makeText(getApplicationContext(), "Erreur de connexion à internet", Toast.LENGTH_SHORT).show();
    			
    			userBdd.close();
    		}
    	}
    	else
    		Toast.makeText(MainActivity.this, "Veuillez renseigner tous les champs...", Toast.LENGTH_LONG).show();
    }
    
    public void newCompte(View v) {
    	Toast.makeText(MainActivity.this, "Création d'un nouveau compte", Toast.LENGTH_LONG).show();
    }
    
    public void forgetPwd(View v) {
		Toast.makeText(MainActivity.this, "régénération d'un nouveau mot de passe", Toast.LENGTH_LONG).show();
    }
   
    // Création de l'itent qui affiche l'activité de l apage d'accueil
    private void homeIntent(String idUser, String pseudo) {
    	// Création de l'intent
    	Intent intent = new Intent(MainActivity.this, HomeActivity.class);

    	// Ajout d'un extra
    	intent.putExtra("idUser", idUser);
    	intent.putExtra("pseudo", pseudo);

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
    
    // Fonction de gestion de la connexion automatique
    private void setAutoConnect(CheckBox cb) {
    	// Test de la valeur de la case à cocher
    	if (cb.isChecked()) {
    		UserBDD userBdd = new UserBDD(this);
    		userBdd.open();
    		
    		// Mise à jour de la BDD intèrne
    		userBdd.updateRememberBdd(user.getIdUser(), user.getDate());
    		
    		userBdd.close();
    	}
    }
    
    // Fonction de mise en place du layout de connexion par Log-ins
    private void setAuthenticateLayout() {
    	// On affiche la page de log-in
    	setContentView(R.layout.authenticate);

    	// Récupération des widgets nécessaires
    	pseudo 	= (TextView)findViewById(R.id.pseudo);
    	mdp 	= (TextView)findViewById(R.id.mdp);
    	rmb 	= (CheckBox)findViewById(R.id.remember);

    	// Attribution des listeners
    	mdp.setOnKeyListener(okListener);
    }
    
    // Tâche asynchrone permettant de vérifier l'existance d'un utilisateur sur le serveur
    // et de mettre à jour en BDD intèrne ses informations
    private class AuthAsynchTask extends AsyncTask<String, Void, User>
    {
    	private ProgressDialog dialogWait	=	null;
    	
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
    	protected User doInBackground(String... param) {
    		// Création de l'objet en charge de la requête HTTP
    		HTTPBddUser httpBddUser = new HTTPBddUser();
    		User returnUser = null;
    		
    		try {
    			// Param[0] = pseudo
    			// Param[1] = Hashmdp
				returnUser = httpBddUser.getUserWithPseudo(param[0], param[1]);
				
				// Si un utilisateur est reconnu, on cherche si il existe en BDD opérations intèrne
				// et si on doit mettre à jour des opérations vers le serveur
				if (returnUser != null) {
					// Si il existe en intèrne on met à jour les opérations sur les serveur si nécessaire
					
					// Sinon on lui créé une BDD opération intèrne
					
				}
				
				return returnUser;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
    	}
    	
    	@Override
    	protected void onPostExecute(User result) {
    		// Fermeture de la boîte de dialogue
    		user = result;
    		    		
    		// Résultat de la requête (existance ou non de l'utilisateur)
    		if (result != null) {
    			// Mise à jour en BDD intèrne des informations utilisateur
        		// Connexion à la BDD intèrne
                userBdd = new UserBDD(MainActivity.this);
                userBdd.open();
                
                // Vérification de l'existance de l'utilisateur en BDD intèrne
                if (userBdd.getUserWithIdUser(user.getIdUser()) != null) {
	                // Mise à jour des infos
	                userBdd.updateUser(user);
                }
                // Sinon on le rajoute
                else {
                	user.setRemember(true);
                	user.setLimit("30");
                	userBdd.insertUser(user);
                }
                
                // Gestion de la case à coché de connexion automatique
                setAutoConnect(rmb);
	                
                userBdd.close();
                
                dialogWait.cancel();
                
    			// On passe à l'activité d'accueil
    			homeIntent(user.getIdUser(), user.getPseudo());
    		}
    		else {
    			dialogWait.cancel();
    			Toast.makeText(MainActivity.this, "Les identifiants ne sont pas corrects", Toast.LENGTH_LONG).show();
    		}
    	}
    }
}
