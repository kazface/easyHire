package com.example.fasthire

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class ApplicantActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_applicant)

        addFragment(ApplicantHomePageFragment.newInstance());


        var bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottomNav)
        bottomNavigation.show(0);
        bottomNavigation.add(MeowBottomNavigation.Model(0, R.drawable.ic_round_home_24))
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_round_bookmark_24))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_baseline_chat_bubble_24))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_baseline_person_24))
        bottomNavigation.setOnClickMenuListener {
            when(it.id){
                0 -> {
                    replaceFragment(ApplicantHomePageFragment.newInstance());
                }
                1 ->{
                    replaceFragment(savedJobFragment.newInstance());
                }
            }
        }




    }


    private fun replaceFragment(fragment: Fragment){
        val fragmentTransition = supportFragmentManager.beginTransaction();
        fragmentTransition.replace(R.id.fragmentContainer, fragment).addToBackStack(Fragment::class.java.simpleName).commit();
    }

    private fun addFragment(fragment: Fragment){
        val fragmentTransition = supportFragmentManager.beginTransaction();
        fragmentTransition.add(R.id.fragmentContainer, fragment).addToBackStack(Fragment::class.java.simpleName).commit();


    }


}