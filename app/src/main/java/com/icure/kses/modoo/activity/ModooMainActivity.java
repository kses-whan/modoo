package com.icure.kses.modoo.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.icure.kses.modoo.R;
import com.icure.kses.modoo.fragments.ImageListFragment;
import com.icure.kses.modoo.notification.NotificationCountSetClass;
import com.icure.kses.modoo.options.CartListActivity;
import com.icure.kses.modoo.options.SearchResultActivity;
import com.icure.kses.modoo.options.WishlistActivity;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ModooMainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener{

    public static int notificationCountCart = 0;
    public static int notificationPushMsg = 0;
    static ViewPager viewPager;
    static TabLayout tabLayout;
//    static CollapsingToolbarLayout tabLayout;

    private static final int MENU_PREFERENCES = Menu.FIRST + 1;
    private static final int SHOW_PREFERENCES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

         viewPager = (ViewPager) findViewById(R.id.viewpager);
         tabLayout = findViewById(R.id.tabs);

        if (viewPager != null) {
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }

//        ImageView iv_toolbar = findViewById(R.id.iv_toolbar);
//
//        Picasso.with(iv_toolbar.getContext())
//                .load("https://static.pexels.com/photos/33283/stack-of-books-vintage-books-book-books-medium.jpg")
//                .noFade()
//                .noPlaceholder()
//                .into(iv_toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                UserManagement.getInstance()
//                        .requestLogout(new LogoutResponseCallback() {
//                            @Override
//                            public void onCompleteLogout() {
//                                Log.i("tagg", "카카오톡 로그아웃 완료");
//                            }
//                        });
//
//                FirebaseAuth.getInstance().signOut();
//                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestIdToken(getString(R.string.default_web_client_id))
//                        .build();
//                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(ModooMainActivity.this, gso);
//                mGoogleSignInClient.signOut().addOnCompleteListener(ModooMainActivity.this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.i("tagg", "구글 로그아웃 완료");
//                    }
//                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
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

        menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_settings);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        MenuItem itemCart = menu.findItem(R.id.action_cart);
        NotificationCountSetClass.setAddToCart(ModooMainActivity.this, itemCart, notificationCountCart);

        MenuItem itemPush = menu.findItem(R.id.action_notifications);
        NotificationCountSetClass.setAddToPushMsg(ModooMainActivity.this, itemPush, notificationPushMsg);
        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_search:
                startActivity(new Intent(ModooMainActivity.this, SearchResultActivity.class));
                return true;
            case R.id.action_cart :
                /* NotificationCountSetClass.setAddToCart(MainActivity.this, item, notificationCount);
            invalidateOptionsMenu();*/
                startActivity(new Intent(ModooMainActivity.this, CartListActivity.class));
               /* notificationCount=0;//clear notification count
                invalidateOptionsMenu();*/
                return true;
            case MENU_PREFERENCES :
                Intent intent = new Intent(ModooMainActivity.this, ModooSettingsActivity.class);
                startActivityForResult(intent, SHOW_PREFERENCES);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        ImageListFragment fragment = new ImageListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, getString(R.string.item_1));
        fragment = new ImageListFragment();
        bundle = new Bundle();
        bundle.putInt("type", 2);
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, getString(R.string.item_2));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 3);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_3));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 4);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_4));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 5);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_5));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 6);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_6));
        viewPager.setAdapter(adapter);
    }

    int category = 1;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_item1) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_item2) {
            viewPager.setCurrentItem(1);
        }
//        else if (id == R.id.nav_item3) {
//            viewPager.setCurrentItem(2);
//        } else if (id == R.id.nav_item4) {
//            viewPager.setCurrentItem(3);
//        } else if (id == R.id.nav_item5) {
//            viewPager.setCurrentItem(4);
//        }else if (id == R.id.nav_item6) {
//            viewPager.setCurrentItem(5);
//        }
        else if (id == R.id.my_wishlist) {
            startActivity(new Intent(ModooMainActivity.this, WishlistActivity.class));
        }else if (id == R.id.my_cart) {
            startActivity(new Intent(ModooMainActivity.this, CartListActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
