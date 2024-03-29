package com.example.fasthire

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class EmployerJobsFragment : Fragment() {
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


        jobRecyclerView = view.findViewById(R.id.jobRecyclerView)
        var shimmer = view.findViewById<ShimmerFrameLayout>(R.id.jobShimmerLayout)
        shimmer.startShimmerAnimation()
        shimmer.visibility = View.VISIBLE
        var fabAddCV = view.findViewById<FloatingActionButton>(R.id.fabAddJob)

        jobRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
        jobRecyclerView.setHasFixedSize(true)
        jobRecyclerView.visibility = View.INVISIBLE

        jobList = arrayListOf();

        jobAdapter = JobAdapter(jobList, false)

        jobRecyclerView.adapter = jobAdapter

        database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Jobs")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(cvSnapshot in snapshot.children){
                        var job: Job = cvSnapshot.getValue<Job>()!!
                        job.id = cvSnapshot.key
                        Log.d("snapshot", job.toString())
                        Log.d("id", user.id.toString())

                        if(job.userId.toString() == FirebaseAuth.getInstance().uid.toString())
                            jobList.add(job)
                    }
                    jobRecyclerView.visibility = View.VISIBLE
                    shimmer.stopShimmerAnimation()
                    shimmer.visibility = View.GONE
                    jobAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        jobAdapter.notifyDataSetChanged()


        jobAdapter.onItemClick = {
            val jobDetailedFragment = jobDetailedFragment()
            val bundle = Bundle()

            bundle.putParcelable("Job", it)
            bundle.putBoolean("isEdit", true)

            jobDetailedFragment.arguments = bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, jobDetailedFragment)?.addToBackStack(null)
            transaction?.commit()
        }

        fabAddCV.setOnClickListener{
            val createJobFragment = CreateJobFragment();
            val transaction = fragmentManager?.beginTransaction()
            val bundle = Bundle()
            bundle.putSerializable("User", user)
            createJobFragment.arguments = bundle
            transaction?.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
            transaction?.replace(R.id.fragmentContainer, createJobFragment)?.addToBackStack(null)
            transaction?.commit()
        }




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employer_jobs, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            EmployerJobsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}