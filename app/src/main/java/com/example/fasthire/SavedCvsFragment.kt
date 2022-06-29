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

class SavedCvsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var cvRecyclerView: RecyclerView;
    private lateinit var cvList: ArrayList<Cv>
    private lateinit var cvAdapter: CvAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var cvCardShimmer = view.findViewById<ShimmerFrameLayout>(R.id.cvCardShimmer)
        cvCardShimmer.startShimmerAnimation();
        cvCardShimmer.visibility = View.VISIBLE;

        cvRecyclerView = view.findViewById(R.id.cvRecyclerView)

        var swiperefresh = view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh)


        cvRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
        cvRecyclerView.setHasFixedSize(true)
        cvRecyclerView.visibility = View.INVISIBLE

        cvList = arrayListOf();
        cvAdapter = CvAdapter(view.context,cvList)
        cvRecyclerView.adapter = cvAdapter

        database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Cvs")

        var isSavedRef = FirebaseAuth.getInstance().uid?.let {
            Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("SavedCvs").child(
                it
            )
        }
        fun updateRecycle(){

            database.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.exists()){
                        for(cvSnapshot in snapshot.children){
                            for(cvChild in cvSnapshot.children){
                                var saved = 0
                                val cv = cvChild.getValue<Cv>()
                                cv!!.id = cvChild.key.toString()
                                isSavedRef!!.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(saveSnapshot: DataSnapshot) {
                                        Log.d("SavedcvPage", saveSnapshot.toString())
                                        if(saveSnapshot.hasChild((cv.id).toString())){
                                            Log.d("SavedcvPage", cvList.toString())
                                            if(!cvList.contains(cv)){
                                                saved = 1
                                                cv!!.saved = saved
                                                cvList.add(cv)
                                                cvAdapter.notifyDataSetChanged()
                                                cvRecyclerView.visibility = View.VISIBLE
                                                cvCardShimmer.visibility = View.GONE
                                            }

                                        }
                                        cvRecyclerView.visibility = View.VISIBLE
                                        cvCardShimmer.visibility = View.GONE

                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                })
                                Log.d("Check", cv.toString())



                            }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
        updateRecycle()

        fun filterData(){
            var cvListSaved = ArrayList(cvList)

//            cvList = cvList.filter{it.saved == 1} as ArrayList<cv>
            cvList.clear()
            cvListSaved.forEach{
                if(it.saved == 1){
                    cvList.add(it)
                }
            }
            cvAdapter.notifyDataSetChanged()
        }

        swiperefresh.setOnRefreshListener{
            filterData()
            swiperefresh.isRefreshing = false
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_cvs, container, false)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SavedCvsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}