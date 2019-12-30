package com.sf.evento.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.sf.evento.Adapters.ViewPageAdapter;
import com.sf.evento.Fragments.FragmentFriendRequests;
import com.sf.evento.Fragments.FragmentMyFriends;
import com.sf.evento.Fragments.fragmentAddFriends;
import com.sf.evento.R;

public class FriendsActivity extends AppCompatActivity {
 private TabLayout tablayout;
 private AppBarLayout appBarLayout;
 public ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
    tablayout=(TabLayout)findViewById(R.id.tabellayout_id);
    appBarLayout=(AppBarLayout) findViewById(R.id.appbarid);
    viewPager=(ViewPager)findViewById(R.id.viewpager_id);
    ViewPageAdapter adpter = new ViewPageAdapter((getSupportFragmentManager()));
    adpter.AddFragment(new FragmentMyFriends(),"My Friends");
    adpter.AddFragment(new FragmentFriendRequests(),"Friends Requests");
    adpter.AddFragment(new fragmentAddFriends(),"Add Friends");
    viewPager.setAdapter(adpter);
    tablayout.setupWithViewPager(viewPager);
    }
}
