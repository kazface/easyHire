package com.example.fasthire

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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

/**
 * A simple [Fragment] subclass.
 * Use the [savedJobFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class savedJobFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var jobRecyclerView: RecyclerView;
    private lateinit var jobList: ArrayList<Job>
    private lateinit var jobAdapter: JobAdapter
    private lateinit var database: DatabaseReference
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("User") as User?
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var jobCardShimmer = view.findViewById<ShimmerFrameLayout>(R.id.jobCardShimmer)
        jobCardShimmer.startShimmerAnimation();
        jobCardShimmer.visibility = View.VISIBLE;

        jobRecyclerView = view.findViewById(R.id.jobRecyclerView)

        var swiperefresh = view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh)


        jobRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
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
        fun updateRecycle(){

            database.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.exists()){
                        for(jobSnapshot in snapshot.children){
                            var saved = 0
                            val job = jobSnapshot.getValue<Job>()
                            job!!.id = jobSnapshot.key.toString()
                            isSavedRef!!.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(saveSnapshot: DataSnapshot) {
                                    Log.d("SavedJobPage", saveSnapshot.toString())
                                    if(saveSnapshot.hasChild((job.id).toString())){
                                        Log.d("SavedJobPage", jobList.toString())
                                        if(!jobList.contains(job)){
                                            saved = 1
                                            job!!.saved = saved
                                            jobList.add(job)
                                            jobAdapter.notifyDataSetChanged()
                                            jobRecyclerView.visibility = View.VISIBLE
                                            jobCardShimmer.visibility = View.GONE
                                        }

                                    }
                                    jobRecyclerView.visibility = View.VISIBLE
                                    jobCardShimmer.visibility = View.GONE

                                }
                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                            Log.d("Check", job.toString())
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
        updateRecycle()

        fun filterData(){
            var jobListSaved = ArrayList(jobList)

//            jobList = jobList.filter{it.saved == 1} as ArrayList<Job>
            jobList.clear()
            jobListSaved.forEach{
                if(it.saved == 1){
                    jobList.add(it)
                }
            }
            jobAdapter.notifyDataSetChanged()
        }

        swiperefresh.setOnRefreshListener{
            filterData()
            swiperefresh.isRefreshing = false
        }
        jobAdapter.onItemClick = {
            val jobDetailedFragment = jobDetailedFragment()
            val bundle = Bundle()
            bundle.putParcelable("Job", it)
            bundle.putSerializable("User", user)
            jobDetailedFragment.arguments = bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, jobDetailedFragment)?.addToBackStack(null)
            transaction?.commit()
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_job, container, false)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            savedJobFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}