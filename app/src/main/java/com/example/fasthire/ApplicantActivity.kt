package com.example.fasthire

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

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
                    replaceFragment(findJobFragment.newInstance());

                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, ){
        val fragmentTransition = supportFragmentManager.beginTransaction();
        fragmentTransition.replace(R.id.fragmentContainer, fragment).addToBackStack(Fragment::class.java.simpleName).commit();
    }

    private fun addFragment(fragment: Fragment){
        val fragmentTransition = supportFragmentManager.beginTransaction();
        fragmentTransition.add(R.id.fragmentContainer, fragment).addToBackStack(Fragment::class.java.simpleName).commit();


    }


}