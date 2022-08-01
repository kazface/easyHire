package com.example.fasthire

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList



class findJobFragment : Fragment() {

    private lateinit var jobRecyclerView: RecyclerView;
    private lateinit var jobList: ArrayList<Job>
    private lateinit var jobListSaved: ArrayList<Job>
    private lateinit var jobAdapter: JobAdapter



    private lateinit var database: DatabaseReference
    private var user: User? = null
    private var jobType: String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("User") as User
            jobType = it.getString("JobType");
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        Log.d("User", user.toString())
        var jobCardShimmer = view.findViewById<ShimmerFrameLayout>(R.id.jobCardShimmer)
        jobCardShimmer.startShimmerAnimation();
        jobCardShimmer.visibility = View.VISIBLE;

        jobRecyclerView = view.findViewById(R.id.jobRecyclerView)
        jobRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
        jobRecyclerView.setHasFixedSize(true)
        jobRecyclerView.visibility = View.INVISIBLE
        jobListSaved = arrayListOf();
        jobList = arrayListOf();
        jobAdapter = JobAdapter(jobList)
        jobRecyclerView.adapter = jobAdapter

        database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Jobs")

        var isSavedRef = FirebaseAuth.getInstance().uid?.let {
            Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("SavedJob").child(
                it
            )
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    for(jobSnapshot in snapshot.children){

                        var saved = 0
                        val job = jobSnapshot.getValue<Job>()
                        job!!.id = jobSnapshot.key.toString()

                        isSavedRef!!.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(saveSnapshot: DataSnapshot) {
                                if(saveSnapshot.hasChild((job?.id ?: -1).toString())){
                                    saved = 1
                                }
                                job!!.saved = saved
                                if(!jobList.contains(job)){
                                    if(jobType != null){
                                        if(job.type == jobType){
                                            jobList.add(job)
                                            jobListSaved.add(job)
                                        }
                                    }else{
                                        jobList.add(job)
                                        jobListSaved.add(job)
                                    }
                                    jobAdapter.notifyDataSetChanged()
                                    jobRecyclerView.visibility = View.VISIBLE
                                    jobCardShimmer.visibility = View.GONE
                                }

                                Log.d("JOBSS",saveSnapshot.toString())



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

        jobAdapter.onItemClick = {
            val jobDetailedFragment = jobDetailedFragment()
            val bundle = Bundle()
            Log.d("JobFromFindJob", it.toString())
            bundle.putParcelable("Job", it)
            bundle.putSerializable("User", user)
            jobDetailedFragment.arguments = bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, jobDetailedFragment)?.addToBackStack(null)
            transaction?.commit()
        }


        var searchView = view.findViewById<SearchView>(R.id.jobSearchView)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                jobList.clear()
                var searchText = p0!!.lowercase()
                if(searchText.isNotEmpty() && searchText != ""){
                    jobListSaved.forEach {
                        if (it.title.toString().lowercase()
                                .contains(searchText) && !jobList.contains(it)
                        ) {
                            jobList.add(it)
                        }
                    }
                    jobAdapter.notifyDataSetChanged()
                }else{
                    jobListSaved.forEach{
                        if(!jobList.contains(it)){
                            jobList.add(it)
                        }
                    }
                    jobAdapter.notifyDataSetChanged()
                }
                return false
            }


        })


     }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_find_job, container, false)
    }






    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            findJobFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}