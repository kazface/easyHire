package com.example.fasthire

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [EmployerHomePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmployerHomePageFragment : Fragment() {

    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = (it.getSerializable("User") as User?)!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var userFullNameText = view.findViewById<TextView>(R.id.userFullNameText)


        userFullNameText.text = user.fullName.toString()


        var createJobLayout = view.findViewById<RelativeLayout>(R.id.create_job_card_background)


        var createCompanyLayout = view.findViewById<RelativeLayout>(R.id.create_company_card_background)


        var watchCvs = view.findViewById<RelativeLayout>(R.id.watch_resume_card_background)


        createJobLayout.setOnClickListener{
            val createJobFragment = CreateJobFragment();
            val bundle: Bundle = Bundle();
            bundle.putSerializable("User", user)
            createJobFragment.arguments = bundle;
            fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer, createJobFragment)
                ?.addToBackStack(null)?.commit()

        }


        createCompanyLayout.setOnClickListener{
            val createCompanyFragment = CreateCompanyFragment();
            val bundle: Bundle = Bundle();
            bundle.putSerializable("User", user)
            createCompanyFragment.arguments = bundle;
            fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer, createCompanyFragment)
                ?.addToBackStack(null)?.commit()
        }


        watchCvs.setOnClickListener{
            val findCvsFragment = findCvsFragment()
            val bundle: Bundle = Bundle();
            bundle.putSerializable("User", user)
            findCvsFragment.arguments = bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, findCvsFragment)?.addToBackStack(null)
            transaction?.commit()

        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employer_home_page, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            EmployerHomePageFragment().apply {
            }
    }
}