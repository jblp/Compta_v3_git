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
import utilities.User;
import android.util.Log;

public class HTTPBdd extends Thread {
	
	private User user = null;
	private String ADRESSE = "http://82.216.240.106:5190/Android/scriptUserAndroid.php";
	private String reponseString = null;
	
	// Types de requêtes
	private static String REQUEST_GET = "GET_USER";
	private static String REQUEST_UPDATE = "UPDATE_USER";
	private static String REQUEST_REMOVE = "REMOVE_USER";
	
	public String postData(List form) {

		try {
			DefaultHttpClient client=new DefaultHttpClient();
			HttpPost post=new HttpPost(ADRESSE);

			post.addHeader("Authorization", "Basic " + Base64.encode(("invite:invite").getBytes()));
			
			post.setEntity(new UrlEncodedFormEntity(form));//, HTTP.UTF_8));

			ResponseHandler<String> responseHandler=new BasicResponseHandler();
			String responseBody=client.execute(post, responseHandler);
			return responseBody;
			//JSONObject response=new JSONObject(responseBody);
		}
		catch (Throwable t) {
			Log.e("Patchy", "Exception in updateStatus()", t);
			return "FAIL";
		}

		/*// On créé un client http
		    HttpClient httpclient = new DefaultHttpClient();

		    // On créé notre entête
		    HttpPost httppost = new HttpPost(ADRESSE);
		    httppost.addHeader("Authorization", "basic " + Base64.encode("jb:03061992jb".getBytes()));

		    try {
		        // Ajoute la liste à notre entête
		        httppost.setEntity(new UrlEncodedFormEntity(listeValues));

		        // On exécute la requête tout en récupérant la réponse
		        HttpResponse response = httpclient.execute(httppost);

		        // On peut maintenant afficher la réponse
		        return response.toString();

		    } catch (ClientProtocolException e) {
		        return null;
		    } catch (IOException e) {
		    	return null;
		    }*/
	}
	
	public String getUserWithPseudo(String pseudo, String pwd){
		
		// On ajoute nos données dans une liste
		List<NameValuePair> form = new ArrayList<NameValuePair>();
		
        // On ajoute nos valeurs ici un identifiant et un message
		form.add(new BasicNameValuePair("pseudo", pseudo));
		form.add(new BasicNameValuePair("pwd", pwd));
		form.add(new BasicNameValuePair("request", REQUEST_GET));
		
//        user = new User();
//        user.setId(Integer.valueOf(postData(nameValuePairs)));
        
		return postData(form);
	}
	
	public void updateUser(User newUser){
		
	}
	
	public void removeUser(int idUser){
		
	}
}