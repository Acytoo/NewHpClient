package com.acytoo.newhpcliend;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.CookieJar;


//每个Layout中的id可以重复吗？
public class RegisterActivity extends AppCompatActivity {

    private UserRegisterTask userRegisterTask = null;

    private EditText input_stuid;
    private EditText input_user_name;
    private EditText input_password;
    private EditText input_captha;
    private Button btn_register;
    private View registerProgressView;
    private View registerFormView;
    CookieJar aaoCookieJar;
    HttpManager aaoManager;
    Handler imgHandler;
    static ImageView img_captha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        input_stuid = findViewById(R.id.input_id);
        input_user_name = findViewById(R.id.input_user_name);
        input_password = findViewById(R.id.input_password);
        input_captha = findViewById(R.id.input_captha);
        btn_register = findViewById(R.id.btn_register);
        img_captha = findViewById(R.id.img_captha);

        input_captha.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        registerFormView = findViewById(R.id.register_form);
        registerProgressView = findViewById(R.id.register_progress);
        aaoCookieJar = new PersistentCookieJar(new SetCookieCache(),
                new SharedPrefsCookiePersistor(MyApplication.getInstance()));
        aaoManager= new HttpManager(aaoCookieJar);
        imgHandler = new Handler();
        Log.d("yllogin", "started register");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap capthaImg = aaoManager.getCaptha("https://zhjw.neu.edu.cn/ACTIONVALIDATERANDOMPICTURE.APPPROCESS?id=");
                if (capthaImg == null){
                    Log.d("yllogin", "it's fucking null");
                }
                imgHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        img_captha.setImageBitmap(capthaImg);
                        Log.d("yllogin", "get captha : " + capthaImg.toString());
                    }
                });
            }
        }).start();

        Log.d("yllogin", "get captha1");


    }


    private void attemptRegister(){
        if (userRegisterTask != null) {
            return;
        }

        // Reset errors.
        input_stuid.setError(null);
        input_password.setError(null);

        // Store values at the time of the login attempt.
        String id = input_stuid.getText().toString();
        String username = input_user_name.getText().toString();
        String password = input_password.getText().toString();
        String captha = input_captha.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            input_password.setError(getString(R.string.error_invalid_password));
            focusView = input_password;
            cancel = true;
        }

        //check for a valid sid
        if (!isSIdValid(id)) {
            input_stuid.setError(getString(R.string.error_invalid_email));
            focusView = input_stuid;
            cancel = true;
        }

        // Check for a valid username.

        if (TextUtils.isEmpty(username)) {
            input_user_name.setError(getString(R.string.error_field_required));
            focusView = input_user_name;
            cancel = true;
        }

        if (TextUtils.isEmpty(captha)) {
            input_captha.setError(getString(R.string.error_field_required));
            focusView = input_captha;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            userRegisterTask = new RegisterActivity.UserRegisterTask(id, username, password, captha);
            userRegisterTask.execute((Void) null);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mId;
        private final String mUserName;
        private final String mPassword;
        private final String mCaptha;

        UserRegisterTask(String id, String username, String password, String captha) {
            mId = id;
            mUserName = username;
            mPassword = password;
            mCaptha = captha;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                //send json data
                Log.d("yllogin", mId + mCaptha + mPassword+mUserName);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // aao -> save ing -> sign to our server
                        //Build two client
                        //The register method using persistent cookieJar form Github, since my own CookieJar won't clean the olk cookies
                        //After login, we can save a file to the internal storage, every time the app boot,
                        //we can check, to get the login info.

                        //Login to the aao first to judge weather the user have the access to our server


                        aaoManager.loginAAO(mId, mPassword, mCaptha);

                        if (aaoManager.doGetImage("https://zhjw.neu.edu.cn/ACTIONVALIDATERANDOMPICTURE.APPPROCESS?id=")) {

                            HttpManager httpManager = new HttpManager();
                            Log.d("yllogin", "asdfasdfasdfasdfasdf");
                            httpManager.doRegister(mId, mUserName, mPassword, mCaptha);
                        }
                        else {
                            Log.d("yllogin", "can not login to aao");
                        }
                    }
                }).start();

            } catch (Exception e) {
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            userRegisterTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                input_password.setError(getString(R.string.error_incorrect_password));
                input_password.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            userRegisterTask = null;
            showProgress(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean isSIdValid(String Sid) {
        int id = Integer.parseInt(Sid);
        return ((id > 20139999) && (id < 20180000));
    }

    private boolean isPasswordValid(String password) {
        //教务处
        return password.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        registerFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        registerProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        registerProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registerProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }



}
