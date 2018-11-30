package ricardopazdemiquel.com.imotos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.signin.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import ricardopazdemiquel.com.imotos.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotos.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotos.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotos.utiles.Contexto;
import ricardopazdemiquel.com.imotos.utiles.Token;
import ricardopazdemiquel.com.imotos.utils.Tools;

public class LoginSocial extends AppCompatActivity implements OnClickListener {

    //login face
    private static final String TAG = "LoginSocial";
    private static final int RC_SIGN_IN = 9001;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnfacebook;
    private CheckBox check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_social);

        signInButton = findViewById(R.id.sign_in_button);
        btnfacebook = findViewById(R.id.btn_face);
        check = findViewById(R.id.check);

        btnfacebook.setOnClickListener(this);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Toast.makeText(LoginSocial.this, b+"verdadero", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
             //   signIn();
            }
        });*/

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/
        // [END build_client]

        // [START customize_button]

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        //  updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }


    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            Toast.makeText(this, account.getDisplayName(), Toast.LENGTH_LONG).show();
            String id = account.getId();
            try {
                String resp = new get_usr_gmail(id).execute().get();
                if (resp == null) {
                    Toast.makeText(LoginSocial.this, "Error al conectarse con el servidor.", Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                } else {
                    if (resp.contains("falso")) {
                        Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                    } else {
                        try {
                            JSONObject obj = new JSONObject(resp);
                            if (obj.getString("exito").equals("si")) {
                                SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferencias.edit();
                                editor.putString("usr_log", obj.toString());
                                editor.commit();
                                Intent intent = new Intent(LoginSocial.this, PedirSieteMap.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {

                                ////esteeee <=-------
                                Intent intent = new Intent(LoginSocial.this, Iniciar_cuenta_gmail_Activity.class);
                                JSONObject object = new JSONObject();
                                object.put("diplayname", account.getDisplayName());
                                object.put("email", account.getEmail());
                                object.put("id", account.getId());
                                object.put("familyname", account.getFamilyName());
                                object.put("givenname", account.getGivenName());
                                intent.putExtra("usr_face", object.toString());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                //LoginManager.getInstance().logOut();
                                signOut();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_face:
                InitLoginFacebook();
                break;
        }
    }

    /*private LoginButton loginBFace;
    public LoginSocial(LoginButton loginBFace) {
        this.loginBFace=loginBFace;
    }*/

    private void InitLoginFacebook() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.loginFacebook);
        loginButton.setReadPermissions("email");
        loginButton.callOnClick();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String id = object.getString("id");
                                    try {
                                        String resp = new get_usr_face(id).execute().get();
                                        if (resp == null) {
                                            Toast.makeText(LoginSocial.this, "Error al conectarse con el servidor.", Toast.LENGTH_SHORT).show();
                                            LoginManager.getInstance().logOut();
                                        } else {
                                            if (resp.contains("falso")) {
                                                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                                            } else {
                                                try {
                                                    JSONObject obj = new JSONObject(resp);
                                                    if (obj.getString("exito").equals("si")) {
                                                        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = preferencias.edit();
                                                        editor.putString("usr_log", obj.toString());
                                                        editor.commit();
                                                        Intent intent = new Intent(LoginSocial.this, PedirSieteMap.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    } else {

                                                        ////esteeee <=-------
                                                        Intent intent = new Intent(LoginSocial.this, Iniciar_cuenta_fb_Activity.class);
                                                        intent.putExtra("usr_face", object.toString());
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        LoginManager.getInstance().logOut();

                                                    }


                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,middle_name,last_name,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                if (AccessToken.getCurrentAccessToken() == null) {
                    return; // already logged out
                }
                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                        .Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        LoginManager.getInstance().logOut();
                        LoginManager.getInstance().logInWithReadPermissions(LoginSocial.this, Arrays.asList("public_profile,email"));

                    }
                }).executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                AccessToken.setCurrentAccessToken(null);
                LoginManager.getInstance().logInWithReadPermissions(LoginSocial.this, Arrays.asList("public_profile,email,user_birthday"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public class get_usr_face extends AsyncTask<Void, String, String> {
        private String id;

        public get_usr_face(String id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_usuario_face");
            parametros.put("id_usr", id + "");
            parametros.put("token", Token.currentToken);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);


        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    public class get_usr_gmail extends AsyncTask<Void, String, String> {
        private String id;

        public get_usr_gmail(String id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_usuario_gmail");
            parametros.put("id_usr", id + "");
            parametros.put("token", Token.currentToken);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);


        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }
}
