package com.example.fasthire

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
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
 * Use the [findCvsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class findCvsFragment : Fragment() {


    private lateinit var cvRecyclerView: RecyclerView;
    private lateinit var cvList: ArrayList<Cv>
    private lateinit var cvListSaved: ArrayList<Cv>
    private lateinit var cvAdapter: CvAdapter
    private  var user: User? = null


    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("User") as User
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var cvCardShimmer = view.findViewById<ShimmerFrameLayout>(R.id.cvCardShimmer)
        cvCardShimmer.startShimmerAnimation();
        cvCardShimmer.visibility = View.VISIBLE;


        cvRecyclerView = view.findViewById(R.id.cvRecyclerView)
        cvRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
        cvRecyclerView.setHasFixedSize(true)
        cvRecyclerView.visibility = View.INVISIBLE
        cvListSaved = arrayListOf();
        cvList = arrayListOf();
        cvAdapter = CvAdapter(view.context, cvList)
        cvRecyclerView.adapter = cvAdapter





        database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Cvs")

        var isSavedRef = FirebaseAuth.getInstance().uid?.let {
            Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("SavedCvs").child(
                it
            )
        }

        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    for(cvSnapshot in snapshot.children){
                        for(cvSnapshotChild in cvSnapshot.children){

                            var saved = 0
                            val cv = cvSnapshotChild.getValue<Cv>()
                            cv!!.id = cvSnapshotChild.key.toString()

                            Log.d("Cvid", cv.id.toString())


                            isSavedRef!!.addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(child: DataSnapshot) {
                                    if(child.hasChild((cv.id ?: -1).toString())){
                                        saved = 1
                                    }
                                    cv!!.saved = saved
                                    if(!cvList.contains(cv)){
                                        cvList.add(cv)
                                        cvListSaved.add(cv)
                                        cvAdapter.notifyDataSetChanged()
                                        cvRecyclerView.visibility = View.VISIBLE
                                        cvCardShimmer.visibility = View.GONE
                                    }

                                    Log.d("cvSnapshot",child.toString())

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
                TODO("Not yet implemented")
            }
        })


        cvAdapter.onItemClick = { cv: Cv, bitmap: Bitmap ->
            val cvDetailedFragment = CvDetailedFragment()
            val transaction = fragmentManager?.beginTransaction()
            val bundle = Bundle()
            bundle.putParcelable("CV", cv)
            bundle.putParcelable("CVPhoto", bitmap)
            bundle.putBoolean("isOffer", true)

            bundle.putSerializable("User", user)

            cvDetailedFragment.arguments = bundle
            transaction?.replace(R.id.fragmentContainer, cvDetailedFragment)?.addToBackStack(null)
            transaction?.commit()
        }


        var searchView = view.findViewById<SearchView>(R.id.cvSearchView)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                cvList.clear()
                var searchText = p0!!.lowercase()
                if(searchText.isNotEmpty() && searchText != ""){
                    cvListSaved.forEach {
                        if (it.title.toString().lowercase()
                                .contains(searchText) && !cvList.contains(it)
                        ) {
                            cvList.add(it)
                        }
                    }
                    cvAdapter.notifyDataSetChanged()
                }else{
                    cvListSaved.forEach{
                        if(!cvList.contains(it)){
                            cvList.add(it)
                        }
                    }
                    cvAdapter.notifyDataSetChanged()
                }
                return false
            }


        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_cvs, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment findCvsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            findCvsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}