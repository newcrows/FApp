package com.crowsnet.fappexample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.crowsnet.fappexample.R;
import com.crowsnet.fappexample.agenda.AgendaFragment;
import com.crowsnet.fappexample.core.provider.DateProvider;
import com.crowsnet.fappexample.core.exception.UserProviderException;
import com.crowsnet.fappexample.core.pojo.User;
import com.crowsnet.fappexample.core.provider.UserProvider;

public class MainActivity extends AppCompatActivity {

    private static final String CURRENT_TAG = "current";
    private DateProvider dateProvider;

    //default credentials for debug
    final String email = "janf.mund@gmail.com";
    final String pass = "hallo123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        UserProvider.getInstance().checkSignIn(new UserProvider.SignInCallback() {
            @Override
            public void onSuccess(User user) {
                DateProvider.getInstance().setUser(user);
            }

            @Override
            public void onFail(Object error) {
                if (error instanceof UserProviderException)
                    UserProvider.getInstance().signIn(email, pass, this);
            }
        });

        dateProvider = DateProvider.getInstance();
        dateProvider.start();

        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentByTag(CURRENT_TAG);
        if (frag == null) {
            frag = new AgendaFragment();
            fm.beginTransaction().add(R.id.frameLayout, frag, CURRENT_TAG).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
