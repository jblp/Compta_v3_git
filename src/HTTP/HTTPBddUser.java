/********************************************************************************************************************************
 *	source : https://github.com/commonsguy/cw-andtutorials/blob/master/15-HttpClient/Patchy/src/apt/tutorial/two/Patchy.java	*
 *******************************************************************************************************************************/

package HTTP;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import utilities.Base64;
import BDD.User;
import android.util.Log;

public class HTTPBddUser extends Thread {
	
	private String ADRESSE = "http://82.216.240.106:5190/Android/scriptUserAndroid.php";
	
	// Types de requêtes
	private static String REQUEST_GET = "GET_USER";
	private static String REQUEST_GET_USER = "GET_USER_ID";
	private static String REQUEST_UPDATE = "UPDATE_USER";
	private static String REQUEST_REMOVE = "REMOVE_USER";
	private JSONObject response = null;
	
	public boolean postData(List<NameValuePair> form) {
		
		/* Réponses serveur :
		- VOID = pas de user existant en bdd avec les identifiants fournis
		- JSON rempli = existance de l'utilisateur */

		try {
			DefaultHttpClient client=new DefaultHttpClient();
			HttpPost post=new HttpPost(ADRESSE);

			post.addHeader("Authorization", "Basic " + Base64.encode(("invite:invite").getBytes()));
			
			post.setEntity(new UrlEncodedFormEntity(form, "UTF-8"));

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = client.execute(post, responseHandler);
			response = new JSONObject(responseBody);
			
			return true;
		}
		catch (Throwable t) {
			Log.e("Patchy", "Exception in updateStatus()", t);
			return false;
		}
	}
	
	public User getUserWithPseudo(String pseudo, String pwd) throws Exception {
		
		// On ajoute nos données dans une liste
		List<NameValuePair> form = new ArrayList<NameValuePair>();
		
        // On ajoute nos valeurs ici un identifiant et un message
		form.add(new BasicNameValuePair("pseudo", pseudo));
		form.add(new BasicNameValuePair("pwd", pwd));
		form.add(new BasicNameValuePair("request", REQUEST_GET));
        
		if(postData(form)) {
			// Parse de l'objet retourné en JSON
			String[] param = {"", response.getString("id").toString(), response.getString("pseudo").toString(), pwd,
							  response.getString("mail").toString(), response.getString("totalComptable").toString(),
							  response.getString("totalBanque").toString(), response.getString("categories").toString()};
			
			return new User(param);
		}
		else {
			return null;
		}
	}
	
	public User getUserWithIdUser(String idUser) throws Exception {
		
		// On ajoute nos données dans une liste
		List<NameValuePair> form = new ArrayList<NameValuePair>();
		
        // On ajoute nos valeurs ici un identifiant et un message
		form.add(new BasicNameValuePair("idUser", idUser));
		form.add(new BasicNameValuePair("request", REQUEST_GET_USER));
        
		if(postData(form)) {
			// Parse de l'objet retourné en JSON
			String[] param = {"", response.getString("id").toString(), response.getString("pseudo").toString(), "",
							  response.getString("mail").toString(), response.getString("totalComptable").toString(),
							  response.getString("totalBanque").toString(), response.getString("categories").toString()};
			
			return new User(param);
		}
		else {
			return null;
		}
	}
	
	public void updateUser(User user){
		// On ajoute nos données dans une liste
		List<NameValuePair> form = new ArrayList<NameValuePair>();

		// On ajoute nos valeurs ici un identifiant et un message
		form.add(new BasicNameValuePair("idUser", user.getIdUser()));
		form.add(new BasicNameValuePair("pseudo", user.getPseudo()));
		form.add(new BasicNameValuePair("mail", user.getMail()));
		form.add(new BasicNameValuePair("comptable", String.valueOf(user.getComptable())));
		form.add(new BasicNameValuePair("banque", String.valueOf(user.getBanque())));
		form.add(new BasicNameValuePair("categories", user.getCategoriesString()));
		form.add(new BasicNameValuePair("request", REQUEST_UPDATE));

		postData(form);
	}
	
	public void removeUser(int idUser){
		
	}
}