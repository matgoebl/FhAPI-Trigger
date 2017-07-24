package net.goebl.matthias.fhapitrigger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

public class ServerComm {
	
	public static String httpGet(Context context, String serverUrl) throws Exception {
		String result = "";

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(serverUrl); 

		URL url = new URL(serverUrl);
		String userInfo = url.getUserInfo();
		if( userInfo != null ) {
			httpget.setHeader("Authorization", "Basic " + Base64.encodeToString(userInfo.getBytes(), Base64.NO_WRAP));
		}
		String AndroidID = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
		httpget.addHeader("User-Agent", "FhAPI-Trigger@" + AndroidID + "/0.1");
		httpget.addHeader("X-AndroidID", AndroidID);

		HttpResponse response;
		response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader inReader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuffer strBuf = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = inReader.readLine()) != null) {
				strBuf.append(line + NL);
			}
			result = strBuf.toString();
			inReader.close();
		}
		int status = response.getStatusLine().getStatusCode();
		if ( status < 200 || status > 299 ) {
			throw new Exception(response.getStatusLine().toString());
		}
		return result;
	}
}
