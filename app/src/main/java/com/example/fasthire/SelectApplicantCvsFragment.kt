package com.example.fasthire

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [SelectApplicantCvsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectApplicantCvsFragment : Fragment() {
    private lateinit var cvRecyclerView: RecyclerView;
    private lateinit var cvList: ArrayList<Cv>
    private lateinit var cvAdapter: CvAdapter
    private lateinit var database: DatabaseReference
    private lateinit var user: User

    private  var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("User") as User
            job = it.getParcelable<Job>("Job")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        cvRecyclerView = view.findViewById(R.id.cvRecyclerView)
        var shimmer = view.findViewById<ShimmerFrameLayout>(R.id.cvShimmerLayout)
        shimmer.startShimmerAnimation()
        shimmer.visibility = View.VISIBLE

        cvRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
        cvRecyclerView.setHasFixedSize(true)
        cvRecyclerView.visibility = View.INVISIBLE

        cvList = arrayListOf();

        cvAdapter = CvAdapter(view.context, cvList, false)
        Log.d("Job", job.toString())
        cvRecyclerView.adapter = cvAdapter

        database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Cvs")
        var userCvsRef = FirebaseAuth.getInstance().uid?.let { database.child(it) }
        userCvsRef!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(cvSnapshot in snapshot.children){
                        var cv: Cv = cvSnapshot.getValue<Cv>()!!
                        cv.id = cvSnapshot.key
                        Log.d("CVCheck",cv.toString())

                        cvList.add(cv)
                    }
                    cvRecyclerView.visibility = View.VISIBLE
                    shimmer.stopShimmerAnimation()
                    shimmer.visibility = View.GONE
                    cvAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        cvAdapter.notifyDataSetChanged()


        cvAdapter.onItemClick = { cv: Cv, bitmap: Bitmap ->
            var textMsg = "Hello! My name is ${user.fullName}, I am ${cv.title}. I am interesting in '${job?.title}' position "
            var sendUser: User?

            Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
                .child(job!!.userId.toString())
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("Snapshot", snapshot.toString())
                            sendUser = snapshot.getValue<User>()
                            Log.d("CV",cv.toString())
                            sendUser?.id = snapshot.key
                            var message = Message(cv.id, FirebaseAuth.getInstance().uid, user.fullName, job?.id, textMsg, sendUser?.id, sendUser?.fullName, textMsg, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt(), user.email, sendUser?.email  )
                            var messagesRef = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("messages")
                            messagesRef.push().setValue(message)
                            Toast.makeText(view.context, "Message sent successfully! Please check message section", Toast.LENGTH_SHORT).show()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(view.context, "Please try again!", Toast.LENGTH_SHORT).show()
                    }
                })
        }





    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_applicant_cvs, container, false)
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
            SelectApplicantCvsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}