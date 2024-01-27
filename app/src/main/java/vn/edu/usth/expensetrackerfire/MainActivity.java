package vn.edu.usth.expensetrackerfire;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import vn.edu.usth.expensetrackerfire.activities.LoginActivity;
import vn.edu.usth.expensetrackerfire.fragments.DashBoardFragment;
import vn.edu.usth.expensetrackerfire.fragments.ExpenseFragment;
import vn.edu.usth.expensetrackerfire.fragments.IncomeFragment;
import vn.edu.usth.expensetrackerfire.fragments.OcraFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private BottomNavigationView bottomNavigationView;
    private FrameLayout framelayout;
    //fragment

    private DashBoardFragment dashBoardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;
    private OcraFragment ocraFragment;

    private FirebaseAuth mAuth;
    Switch aSwitch;
    boolean nightMODE;
    SharedPreferences shared;
    SharedPreferences.Editor edito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // mode light. night
//        getSupportActionBar().hide(); // ẩn thanh header
        aSwitch = findViewById(R.id.switcher);
        //save mode if the exite app
        shared = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = shared.getBoolean("night", false); // light mode default

        if(nightMODE){
            aSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nightMODE){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    edito = shared.edit();
//                    edito.putBoolean("night", false);
                    nightMODE = false;
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    edito = shared.edit();
//                    edito.putBoolean("night", true);
                    nightMODE = true;
                }
                edito = shared.edit();
                edito.putBoolean("night", nightMODE);
                edito.apply();
            }
        });
    ////
        //logout
        mAuth = FirebaseAuth.getInstance();
        //retrieve data
        Intent intent = getIntent();
        if(intent != null){
            String name = intent.getStringExtra("NAME_KEY");
            String email = intent.getStringExtra("EMAIL_KEY");
            String personPhotoUrl = intent.getStringExtra("PROFILE_KEY");
            if (personPhotoUrl != null) {
                Uri personPhotoUri = Uri.parse(personPhotoUrl);
                // Sử dụng Uri để hiển thị hoặc làm việc với hình ảnh theo nhu cầu của bạn
                ImageView imgv = findViewById(R.id.profileImage);
                Glide.with(this)
                        .load(personPhotoUri)
                        .into(imgv);
            }

            // Find TextViews in your layout and set the name and email
            TextView nameTextView = findViewById(R.id.nameTV);
            TextView emailTextView = findViewById(R.id.mailTV);

            nameTextView.setText(name);
            emailTextView.setText(email);
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationbar);
        framelayout = findViewById(R.id.main_frame);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.naView);
        navigationView.setNavigationItemSelectedListener(this);

        dashBoardFragment = new DashBoardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();
        ocraFragment = new OcraFragment();

        setFragment(dashBoardFragment);

        //list menu
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dashboard:
                        setFragment(dashBoardFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.dashBoard_color);
                        return true;
                    case R.id.income:
                        setFragment(incomeFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.income_color);
                        return true;
                    case R.id.expense:
                        setFragment(expenseFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.expense_color);
                        return true;
                    case R.id.ocra:
                        setFragment(ocraFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.expense_color);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }else{
            super.onBackPressed();
        }

    }

        // list menu (có thể thêm 1 task chart vào đây để tạo và hiển thị chart)
    public void displaySelectedListener(int itemId){
        Fragment fragment = null;
        switch (itemId){
            case R.id.dashboard:
                fragment = new DashBoardFragment();
                break;
            case R.id.income:
                fragment = new IncomeFragment();
                break;
            case R.id.expense:
                fragment = new ExpenseFragment();
                break;
            case R.id.ocra:
                fragment = new OcraFragment();
                break;
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }


        if (fragment!= null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame,fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return true;
    }
}