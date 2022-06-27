package com.example.fasthire

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ApplicantHomePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ApplicantHomePageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var jobRecyclerView: RecyclerView;
    private lateinit var jobList: ArrayList<Job>
    private lateinit var jobAdapter: JobAdapter
    private lateinit var database: DatabaseReference
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("User") as User
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var jobCardShimmer = view.findViewById<ShimmerFrameLayout>(R.id.jobCardShimmer)
        jobCardShimmer.startShimmerAnimation();
        jobCardShimmer.visibility = View.VISIBLE;
        Log.d("InsideHomePage", user.fullName)
        var userFullNameText = view.findViewById<TextView>(R.id.userFullNameText)
        userFullNameText.text = user.fullName

        jobRecyclerView = view.findViewById(R.id.recentJobCardRecycle)
        jobRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL ,false)
        jobRecyclerView.setHasFixedSize(true)
        jobRecyclerView.visibility = View.INVISIBLE

        jobList = arrayListOf();
        jobAdapter = JobAdapter(jobList)
        jobRecyclerView.adapter = jobAdapter

        database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Jobs")
        var isSavedRef = FirebaseAuth.getInstance().uid?.let {
            Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("SavedJob").child(
                it
            )
        }

        database.orderByChild("createdDateUnix").limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var saved = 0
                    for(jobSnapshot in snapshot.children){
                        val job = jobSnapshot.getValue<Job>()
                        isSavedRef!!.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(saveSnapshot: DataSnapshot) {
                                job!!.id = jobSnapshot.key.toString()
                                if(saveSnapshot.hasChild((job.id ?: 1231242).toString())){
                                    saved = 1
                                }
                                job!!.saved = saved
                                if(!jobList.contains(job)){
                                    jobList.add(job)
                                    jobAdapter.notifyDataSetChanged()
                                    jobRecyclerView.visibility = View.VISIBLE
                                    jobCardShimmer.visibility = View.GONE
                                }
                                Log.d("JOBSS",saveSnapshot.toString())
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        jobAdapter.onItemClick = {
            val jobDetailedFragment = jobDetailedFragment()
            val bundle = Bundle()
            bundle.putParcelable("Job", it)
            jobDetailedFragment.arguments = bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, jobDetailedFragment)
            transaction?.commit()
        }

        var findJobBtn = view.findViewById<RelativeLayout>(R.id.find_job_card_background);
        findJobBtn.setOnClickListener{
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, findJobFragment())
            transaction?.commit()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_applicant_home_page, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ApplicantHomePageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ApplicantHomePageFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}