package com.manojpc.healthcareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manojpc.healthcareapp.Retrofit.APIClient;
import com.manojpc.healthcareapp.Retrofit.GetResult;
import com.manojpc.healthcareapp.Utils.CustPrograssbar;
import com.manojpc.healthcareapp.model.Example;
import com.manojpc.healthcareapp.model.ResponseBasic;
import com.manojpc.healthcareapp.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements GetResult.MyListener {

    private static int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private Button signUpBtn;
    private EditText emailText;
    private EditText passwordText;
    private Button loginBtn;
    private Button creatBtn;
    private EditText secondPass;
    private EditText confirme;
    SignInButton signInButton;
    CustPrograssbar custPrograssbar;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference UsersRef = db.collection("User");

    GoogleSignInClient mGoogleSignInClient;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_notification_channel_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        custPrograssbar = new CustPrograssbar();
        confirme = (EditText) findViewById(R.id.editText3);
        confirme.setVisibility(View.INVISIBLE);
        signInButton = findViewById(R.id.sign_in_button);

        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText("Or Sign in with Google");

        emailText = (EditText) findViewById(R.id.editText2);
        passwordText = (EditText) findViewById(R.id.editText);
        secondPass = (EditText) findViewById(R.id.editText3);
        signUpBtn = (Button) findViewById(R.id.SignUpBtn);
        loginBtn = (Button) findViewById(R.id.LoginBtn);
        creatBtn = findViewById(R.id.CreateAccount);
        signUpBtn.setVisibility(View.GONE);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                String confirmPass = secondPass.getText().toString();
                if (!email.isEmpty() && !password.isEmpty() && password.equals(confirmPass)) {
                    custPrograssbar.progressCreate(MainActivity.this);
                    custPrograssbar.setCancel(false);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "createUserWithEmail:success");
                                        currentUser = mAuth.getCurrentUser();
                                        registerforApi(email, password);
                                        //updateUI(user);
                                    } else {
                                        custPrograssbar.close();
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }

                                    // ...
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "vous devez rensegner toutes les champs",
                            Toast.LENGTH_SHORT).show();
                    if (!password.equals(confirmPass)) {
                        Toast.makeText(MainActivity.this, "Confirm pass don't match password",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    custPrograssbar.progressCreate(MainActivity.this);
                    custPrograssbar.setCancel(false);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        loginWith(email, password);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(MainActivity.this, "PROBLEM", Toast.LENGTH_SHORT).show();
                                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, task.getException().toString(),
                                                Toast.LENGTH_SHORT).show();
                                        custPrograssbar.close();
                                        updateUI(null);

                                    }

                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "vous devez rensegnier toutes les champs",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        creatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailText.setText("");
                passwordText.setText("");
                if (creatBtn.getText().toString().equals("Create Account")) {
                    confirme.setVisibility(View.VISIBLE);
                    signUpBtn.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.INVISIBLE);
                    creatBtn.setText("Back to login");
                    signInButton.setVisibility(View.GONE);
                } else {
                    confirme.setVisibility(View.INVISIBLE);
                    signUpBtn.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    creatBtn.setText("Create Account");
                    signInButton.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    private void loginWith(String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", email);
            jsonObject.put("password", password);
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().login((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.onNCHandle(call, "login");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void registerforApi(String email, String password) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            JsonParser jsonParser = new JsonParser();
            Toast.makeText(this, "You are registering with us ", Toast.LENGTH_SHORT).show();
            Call<JsonObject> call = APIClient.getInterface().registerphase1((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.onNCHandle(call, "register");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {

            //Log.d("currentuser@2255",currentUser.getEmail());

        } else {

            getStatusbyemail(currentUser.getEmail());
            Log.d("getCurrentUserEmailMain" ,currentUser.getEmail());

        }

    }

    private void getStatusbyemail(String email) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().statusbymail((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.onNCHandle(call, "statusbymail");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ..
                    }
                });
    }

    private void updateUI(final FirebaseUser currentUser) {
        if (currentUser != null) {
            try {

                UsersRef.document(currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UsersRef.document(currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    User user = documentSnapshot.toObject(User.class);

                                    if (user.getType().equals("Patient")) {
                                        Intent k = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(k);
                                    } else if(user.getType().equals("Doctor"))  {
                                        Intent k = new Intent(MainActivity.this, DoctorHomeActivity.class);
                                        startActivity(k);
                                        //Snackbar.make(findViewById(R.id.main_layout), "Doctor interface entraint de realisation", Snackbar.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Intent k = new Intent(MainActivity.this, AdminActivity.class);
                                        startActivity(k);
                                    }

                                }
                            });

                        } else {
                            Intent k = new Intent(MainActivity.this, FirstSigninActivity.class);
                            startActivity(k);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {

        if (callNo.equalsIgnoreCase("register")) {
            Gson gson = new Gson();
            ResponseBasic responseBasic = gson.fromJson(result.toString(), ResponseBasic.class);
            if (responseBasic.getResult().equals("true")) {
                custPrograssbar.close();
                updateUI(currentUser);
            }


        } else if (callNo.equalsIgnoreCase("login")) {

            Gson gson = new Gson();

            Example logincred = gson.fromJson(result.toString(), Example.class);

            custPrograssbar.close();

            if (logincred.getUsers().getRole().equals("doctor")) {

                if (!logincred.getUsers().getStatus().equalsIgnoreCase("0")) {
                    Intent k = new Intent(MainActivity.this, DoctorHomeActivity.class);
                    startActivity(k);
                } else {
                    Toast.makeText(this, "Please wait for the Administrator approval", Toast.LENGTH_SHORT).show();

                }

            } else if (logincred.getUsers().getRole().equals("patient")) {

                Intent k = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(k);

            } else {

                Intent k = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(k);

            }


        } else if (callNo.equalsIgnoreCase("statusbymail")) {

            Gson gson = new Gson();
            Example example = gson.fromJson(result.toString(), Example.class);
            if (example.getResultData().getUserDetails().getStatus().equalsIgnoreCase("1")) {

                updateUI(currentUser);

            } else {

                Toast.makeText(this, "Please wait for the Administrator approval", Toast.LENGTH_SHORT).show();

            }


        }

    }
}
