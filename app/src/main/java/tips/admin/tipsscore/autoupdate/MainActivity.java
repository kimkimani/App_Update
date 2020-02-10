package tips.admin.tipsscore.autoupdate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static  String url = "http://tipsscorepro.com/php%20json/update.json";
    String VersionUpdate;
    String VersionName = BuildConfig.VERSION_NAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new VersionCheck().execute();
    }


    private class VersionCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();


            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null){
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray version = jsonObj.getJSONArray("Version");
                    for (int i = 0; i < version.length(); i++){

                        JSONObject v = version.getJSONObject(i);

                        VersionUpdate = v.getString("version");
                    }



                }catch (final JSONException e) {
                    // Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                //Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute ( result );


            if ( !isNetworkAvailable () ) {
                AlertDialog.Builder CheckBuilder=new AlertDialog.Builder ( MainActivity.this );

                CheckBuilder.setTitle ( "No Internet Connection" );
                CheckBuilder.setMessage ( "You need Internet Connection to access this. " + "Check you mobile data and try again" );

                CheckBuilder.setPositiveButton ( "Retry" , new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog , int which) {
                        Intent intent=getIntent ();
                        fileList ();
                        startActivity ( intent );

                    }
                } );
                AlertDialog alert=CheckBuilder.create ();
                alert.show ();

            }
            else {

                if ( VersionUpdate.equals ( VersionName ) ) {

                    //Do Nothing
                } else {

                    AlertDialog.Builder builder=new AlertDialog.Builder ( MainActivity.this );
                    builder.setTitle ( "Our App got Update" );
                    builder.setIcon ( R.mipmap.ic_launcher );
                    builder.setCancelable ( false );
                    builder.setMessage ( "New version available, select update to update our app" ).setPositiveButton ( "UPDATE" , new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface dialog , int which) {

                            final String appName=getPackageName ();

                            try {
                                startActivity ( new Intent ( Intent.ACTION_VIEW , Uri.parse ( "market://details?id=" + appName ) ) );
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity ( new Intent ( Intent.ACTION_VIEW , Uri.parse ( "http://play.google.com/store/apps/details?id=" + appName ) ) );
                            }

                            finish ();

                        }
                    } ).setNegativeButton ( "No, thanks" , new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface dialog , int which) {

                            finish ();
                        }
                    } ).create ();

                    builder.show ();
                    AlertDialog alert=builder.create ();
                    alert.show ();


                }

            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager= (ConnectivityManager)this.getSystemService ( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo ();
        return  activeNetworkInfo !=null;
    }
}
