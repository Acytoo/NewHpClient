package com.acytoo.newhpcliend.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.acytoo.newhpcliend.R;
import com.acytoo.newhpcliend.utils.HttpManager;
import com.acytoo.newhpcliend.utils.MyCookieJar;

import java.util.ArrayList;
import java.util.List;

import okhttp3.CookieJar;

/**
 * A login screen that offers login via sid/password.
 */

/**
 * Format : json{
 *     stuID:20150000
 *     username:someoen
 *     password:stupid content
 *     }
 *
 *     程序的鲁棒性非常重要， 我们可以在应用上允许注册， 然后注册后要使用功能需要绑     爱的是
 *
 *
 *   We use cookies to cert user login state and login-allow time
 *
 *     Alec Chen
 *     12/5/2018 22:31
 *
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    private UserLoginTask mAuthTask = null;

    private EditText input_login_id;
    private EditText input_login_password;
    private View mProgressView;
    private View mLoginFormView;
    HttpManager httpManager;
    CookieJar myCookieJar;
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        input_login_id = (EditText) findViewById(R.id.input_login_id);
        input_login_password = (EditText) findViewById(R.id.input_login_password);
        input_login_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mIdSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mIdSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        TextView register_slogan = findViewById(R.id.register_slogan);
        register_slogan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerActivity);
            }
        });
        Log.d("YLjson", "onCreate LoginActivity");

        myCookieJar = new MyCookieJar();
        httpManager = new HttpManager(myCookieJar);

    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        input_login_id.setError(null);
        input_login_password.setError(null);

        // Store values at the time of the login attempt.
        String sid = input_login_id.getText().toString();
        String password = input_login_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            input_login_password.setError(getString(R.string.error_invalid_password));
            focusView = input_login_password;
            cancel = true;
        }

        // Check for a valid sid address.
        if (TextUtils.isEmpty(sid)) {
            input_login_id.setError(getString(R.string.error_field_required));
            focusView = input_login_id;
            cancel = true;
        } else if (!isSIdValid(sid)) {
            input_login_id.setError(getString(R.string.error_invalid_email));
            focusView = input_login_id;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(sid, password);
            mAuthTask.execute((Void) null);
        }
    }


    private boolean isSIdValid(String Sid) {
        int id = Integer.parseInt(Sid);
        return ((id > 20139999) && (id < 20180000));
    }

    private boolean isPasswordValid(String password) {
        //教务处密码可以有多少位
        return password.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only sid addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary sid addresses first. Note that there won't be
                // a primary sid address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }



    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mId;
        private final String mPassword;

        UserLoginTask(String sid, String password) {
            mId = sid;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                //send json data
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("YLjson", "do in backGround, in thread");
                        httpManager.doLogin(mId, mPassword);
                    }
                }).start();

            } catch (Exception e) {
                Log.d("ytsave", "after login error " + e.toString());
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                input_login_password.setError(getString(R.string.error_incorrect_password));
                input_login_password.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

