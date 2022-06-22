package com.example.fasthire

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [findJobJobFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class findJobFragment : Fragment() {

    private lateinit var jobRecyclerView: RecyclerView;
    private lateinit var jobList: ArrayList<Job>
    private lateinit var jobAdapter: JobAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var jobCardShimmer = view.findViewById<ShimmerFrameLayout>(R.id.jobCardShimmer)
        jobCardShimmer.startShimmerAnimation();
        jobCardShimmer.visibility = View.VISIBLE;

        jobRecyclerView = view.findViewById(R.id.jobRecyclerView)
        jobRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
        jobRecyclerView.setHasFixedSize(true)
        jobRecyclerView.visibility = View.INVISIBLE

        jobList = arrayListOf();
        jobAdapter = JobAdapter(jobList)
        jobRecyclerView.adapter = jobAdapter

        database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Jobs")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(jobSnapshot in snapshot.children){
                        val job = jobSnapshot.getValue<Job>()
                        jobList.add(job!!)
                        Log.d("Check", jobList.toString())
                    }
                    jobAdapter.notifyDataSetChanged()
                    jobRecyclerView.visibility = View.VISIBLE
                    jobCardShimmer.visibility = View.GONE
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


    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_find_job, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment findJobJobFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            findJobFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}