package com.example.fasthire

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation


class ApplicantActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        val user: User = intent.getSerializableExtra("User") as User

        setContentView(R.layout.activity_applicant)


        var bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottomNav)


        bottomNavigation.show(0);
        bottomNavigation.add(MeowBottomNavigation.Model(0, R.drawable.ic_round_home_24))
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_round_bookmark_24))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_baseline_chat_bubble_24))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_baseline_person_24))

        if(!user.employer!!){
            var applicantHomePageFragment = ApplicantHomePageFragment.newInstance()
            val bundle = Bundle()
            bundle.putSerializable("User", user)
            applicantHomePageFragment.arguments = bundle

            addFragment(applicantHomePageFragment);
            bottomNavigation.setOnClickMenuListener {
                when(it.id){
                    0 -> {
                        replaceFragment(applicantHomePageFragment);
                    }
                    1 ->{
                        replaceFragment(savedJobFragment.newInstance());
                    }
                    2 -> {
                        var selectChatFragment = SelectChatFragment.newInstance()
                        selectChatFragment.arguments = bundle
                        replaceFragment(selectChatFragment);

                    }
                    3 -> {
                        var applicantProfileFragment = ApplicantProfileFragment.newInstance()
                        applicantProfileFragment.arguments = bundle
                        replaceFragment(applicantProfileFragment);
                    }
                }
            }
        }else if(user.employer!!){

            var employerHomePageFragment = EmployerHomePageFragment.newInstance()
            val bundle = Bundle()
            bundle.putSerializable("User", user)
            employerHomePageFragment.arguments = bundle

            addFragment(employerHomePageFragment);
            bottomNavigation.setOnClickMenuListener {
                when(it.id){
                    0 -> {
                        replaceFragment(employerHomePageFragment);
                    }

                    1 ->{
                        var savedCvsFragment = SavedCvsFragment.newInstance()
                        savedCvsFragment.arguments = bundle
                        replaceFragment(savedCvsFragment);
                    }
                    2 -> {

                        var selectChatFragment = SelectChatFragment.newInstance()
                        selectChatFragment.arguments = bundle
                        replaceFragment(selectChatFragment);

                    }
                    3 -> {
                        var employerProfileFragment = EmployerProfileFragment.newInstance()
                        employerProfileFragment.arguments = bundle
                        replaceFragment(employerProfileFragment);
                    }
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