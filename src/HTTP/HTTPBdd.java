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
import org.json.JSONException;
import org.json.JSONObject;

import utilities.Base64;
import utilities.User;
import android.util.Log;

public class HTTPBdd extends Thread {
	
	private User user = null;
	private String ADRESSE = "http://82.216.240.106:5190/Android/scriptUserAndroid.php";
	
	// Types de requ�tes
	private static String REQUEST_GET = "GET_USER";
	private static String REQUEST_UPDATE = "UPDATE_USER";
	private static String REQUEST_REMOVE = "REMOVE_USER";
	private JSONObject response = null;
	
	public boolean postData(List form) {
		
		/* R�ponses serveur :
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
		
		// On ajoute nos donn�es dans une liste
		List<NameValuePair> form = new ArrayList<NameValuePair>();
		
        // On ajoute nos valeurs ici un identifiant et un message
		form.add(new BasicNameValuePair("pseudo", pseudo));
		form.add(new BasicNameValuePair("pwd", pwd));
		form.add(new BasicNameValuePair("request", REQUEST_GET));
        
		if(postData(form)) {
			user = new User();
						
			// Parse de l'objet retourn� en JSON
			user.setId(response.getString("id").toString());
			user.setPseudo(response.getString("pseudo").toString());
			
			return user;
		}
		else {
			return user;
		}
	}
	
	public void updateUser(User newUser){
		
	}
	
	public void removeUser(int idUser){
		
	}
}