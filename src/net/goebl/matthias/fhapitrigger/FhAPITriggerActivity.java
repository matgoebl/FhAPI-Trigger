package net.goebl.matthias.fhapitrigger;

import net.goebl.matthias.fhapitrigger.ServerComm;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import net.goebl.matthias.fhapitrigger.R;


public class FhAPITriggerActivity extends Activity {
	private static final String TAG = FhAPITriggerActivity.class.getSimpleName();
	String baseUrl;
	private Boolean buttonsLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		baseUrl = getResources().getString(R.string.baseUrl);

		findViewById(R.id.buttonSend).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String params = ((EditText)findViewById(R.id.CustomTrigger)).getText().toString();
				new triggerUrl().execute(params);
			}
		});
		new triggerUrl().execute("");
	}

	View.OnClickListener handleOnClick(final Button button) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				String params = v.getTag().toString();
				new triggerUrl().execute(params);
			}
		};
	}

	private void addButtons(String[] buttonNames) {
		if (buttonsLoaded) return;
		buttonsLoaded = true;
		LinearLayout linear = (LinearLayout) findViewById(R.id.LinearLayout);
		LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		Button[] btn = new Button[buttonNames.length];
		for (int i = 0; i < buttonNames.length; i++) {
			btn[i] = new Button(getApplicationContext());
			btn[i].setText(buttonNames[i].toString());
			btn[i].setTextSize(22);
			btn[i].setTag(buttonNames[i].toString());
			btn[i].setLayoutParams(param);
			linear.addView(btn[i]);
			btn[i].setOnClickListener(handleOnClick(btn[i]));
		}
	}

	private class triggerUrl extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String urlParams = params[0];
			if(urlParams.length()>0) urlParams="state?set=" + urlParams;
			String serverURL=baseUrl + urlParams;
			String status = "";
			try {
				status = ServerComm.httpGet(getApplicationContext(), serverURL);
			} catch (Exception e) {
				status = "ERROR\n\n" + e.toString() ;
				e.printStackTrace();
			}
			Log.i(TAG, status);
			return status;
		}

		@Override
		protected void onPostExecute(String result) {
			TextView textView  = (TextView)findViewById(R.id.ResultView);
			textView.setMovementMethod(new ScrollingMovementMethod());
			textView.setTextColor(0xFF000000);
			if(result.startsWith("ERROR")) {
				textView.setBackgroundColor(0xFFFF0000);
			} else {
				textView.setBackgroundColor(0xFFFFFFFF);
			}
			try {
				JSONObject json = new JSONObject(result);
				addButtons(json.optString("_appCmd","").split(":"));
				String text = json.optString("state","ok");
				Iterator<String> it = json.keys();
				ArrayList<String>keys = new ArrayList();
				while (it.hasNext()) {
					keys.add(it.next());
				}
				Collections.sort(keys);
				for (int i = 0; i < keys.size(); i++) {	
					String key = keys.get(i);
					if(!key.startsWith("_") && !key.equals("state")) {
						try {
							String val = json.optString(key,"");
							text += "\n" + key + ": " + val;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				textView.setText(text);
			} catch (Exception e) {
				textView.setText(result);
			}
		}

		@Override
		protected void onPreExecute() {
			TextView textView  = (TextView)findViewById(R.id.ResultView);
			textView.setText(R.string.waiting);
			textView.setBackgroundColor(0xFFFFFF00);
			textView.setTextColor(0xFF000000);
		}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
