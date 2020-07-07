package com.icure.kses.modoo.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icure.kses.modoo.R
import com.icure.kses.modoo.fragments.ImageListFragment
import com.icure.kses.modoo.notification.NotificationCountSetClass
import com.icure.kses.modoo.options.CartListActivity
import com.icure.kses.modoo.options.SearchResultActivity
import com.icure.kses.modoo.options.WishlistActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class ModooMainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        //        ImageView iv_toolbar = findViewById(R.id.iv_toolbar);
//
//        Picasso.with(iv_toolbar.getContext())
//                .load("https://static.pexels.com/photos/33283/stack-of-books-vintage-books-book-books-medium.jpg")
//                .noFade()
//                .noPlaceholder()
//                .into(iv_toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
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
//            }
//        });

//         tabLayout = findViewById(R.id.tabs);

        nav_view?.let { it.setOnNavigationItemSelectedListener(this) }
        if (viewpager != null) {
            setupViewPager(viewpager)
//            tabLayout.setupWithViewPager(viewPager);
        }
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    override fun onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_settings)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        val itemCart = menu.findItem(R.id.action_cart)
        NotificationCountSetClass.setAddToCart(this@ModooMainActivity, itemCart, notificationCountCart)
        val itemPush = menu.findItem(R.id.action_notifications)
        NotificationCountSetClass.setAddToPushMsg(this@ModooMainActivity, itemPush, notificationPushMsg)
        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        when (id) {
            R.id.action_search -> {
                startActivity(Intent(this@ModooMainActivity, SearchResultActivity::class.java))
                return true
            }
            R.id.action_cart -> {
                /* NotificationCountSetClass.setAddToCart(MainActivity.this, item, notificationCount);
            invalidateOptionsMenu();*/startActivity(Intent(this@ModooMainActivity, CartListActivity::class.java))
                /* notificationCount=0;//clear notification count
                invalidateOptionsMenu();*/return true
            }
            MENU_PREFERENCES -> {
                val intent = Intent(this@ModooMainActivity, ModooSettingsActivity::class.java)
                startActivityForResult(intent, SHOW_PREFERENCES)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = Adapter(supportFragmentManager)
        var fragment = ImageListFragment()
        var bundle = Bundle()
        bundle.putInt("type", 1)
        fragment.arguments = bundle
        adapter.addFragment(fragment, getString(R.string.item_1))
        fragment = ImageListFragment()
        bundle = Bundle()
        bundle.putInt("type", 2)
        fragment.arguments = bundle
        adapter.addFragment(fragment, getString(R.string.item_2))
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
        viewPager!!.adapter = adapter
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> nav_view.selectedItemId = R.id.nav_item1
                    1 -> nav_view.selectedItemId = R.id.nav_item2
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_item1) {
            viewpager.currentItem = 0
        } else if (id == R.id.nav_item2) {
            viewpager.currentItem = 1
        } else if (id == R.id.my_wishlist) {
            startActivity(Intent(this@ModooMainActivity, WishlistActivity::class.java))
        } else if (id == R.id.my_cart) {
            startActivity(Intent(this@ModooMainActivity, CartListActivity::class.java))
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true
    }

    internal class Adapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragments: MutableList<Fragment> = ArrayList()
        private val mFragmentTitles: MutableList<String> = ArrayList()
        fun addFragment(fragment: Fragment, title: String) {
            mFragments.add(fragment)
            mFragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitles[position]
        }
    }

    companion object {

        private const val MENU_PREFERENCES = Menu.FIRST + 1
        private const val SHOW_PREFERENCES = 1

        var notificationCountCart = 0
        var notificationPushMsg = 0
    }
}