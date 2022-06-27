package com.example.fasthire

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
 * Use the [ApplicantCvsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ApplicantCvsFragment : Fragment() {
    private lateinit var cvRecyclerView: RecyclerView;
    private lateinit var cvList: ArrayList<Cv>
    private lateinit var cvAdapter: CvAdapter
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


        cvRecyclerView = view.findViewById(R.id.cvRecyclerView)
        cvRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
        cvRecyclerView.setHasFixedSize(true)
        cvList = arrayListOf();

        cvAdapter = CvAdapter(view.context, cvList)
        cvRecyclerView.adapter = cvAdapter

        database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Cvs")
        var userCvsRef = FirebaseAuth.getInstance().uid?.let { database.child(it) }
        userCvsRef!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(cvSnapshot in snapshot.children){
                        var cv: Cv = cvSnapshot.getValue<Cv>()!!
                        cvList.add(cv)
                    }
                    cvAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        cvAdapter.notifyDataSetChanged()


        cvAdapter.onItemClick = { cv: Cv, bitmap: Bitmap ->
            val cvDetailedFragment = CvDetailedFragment()
            val transaction = fragmentManager?.beginTransaction()
            val bundle = Bundle()
            bundle.putParcelable("CV", cv)
            bundle.putParcelable("CVPhoto", bitmap)
            cvDetailedFragment.arguments = bundle
            transaction?.replace(R.id.fragmentContainer, cvDetailedFragment)
            transaction?.commit()



        }





    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_applicant_cvs, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ApplicantCvsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ApplicantCvsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}