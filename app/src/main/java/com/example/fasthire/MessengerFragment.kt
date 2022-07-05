package com.example.fasthire

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MessengerFragment : Fragment() {

    private lateinit var user: User
    private lateinit var messager: Message



    lateinit var messageRecyclerView: RecyclerView
    lateinit var messageList: ArrayList<Message>;
    lateinit var messageAdapter: MessageAdapter

    private var cv: Cv? = null
    private var job: Job? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("User") as User
            messager = it.getParcelable<Message>("Message")!!
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userFullNameText = view.findViewById<TextView>(R.id.userFullNameText)
        var textInput = view.findViewById<TextInputEditText>(R.id.textInput)
        var sendButton = view.findViewById<AppCompatButton>(R.id.sendButton)
        var appliedJobButton = view.findViewById<MaterialButton>(R.id.appliedJobButton)
        var appliedCvButton = view.findViewById<MaterialButton>(R.id.appliedCvButton)


        appliedJobButton.visibility = View.INVISIBLE
        appliedCvButton.visibility = View.INVISIBLE

        userFullNameText.text = messager.fromName


        messageRecyclerView = view.findViewById(R.id.messagesRecycleView)
        messageRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
        messageRecyclerView.setHasFixedSize(true)
        messageList = arrayListOf()
        messageAdapter = MessageAdapter(messageList, view.context)
        messageRecyclerView.adapter = messageAdapter
        var messagesRef = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("messages")
        var uid = FirebaseAuth.getInstance().uid

        messagesRef.orderByChild("unixTime").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                if(snapshot.exists()){
                    var tempArray = arrayListOf<Message>()
                    for(messageSnapshot in snapshot.children){
                        var message: Message = messageSnapshot.getValue<Message>()!!
                        if(message.fromId == uid || message.toId == uid) {
                            messageList.add(message)
                        }
                        Log.d("messageSnapshot", message.toString())
                        Log.d("messageSnapshot", message.job.toString())

                        if(message.job.toString() != "" || message.job != null) {
                            message.job?.let {
                                Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference("Jobs").child(it).addValueEventListener(object: ValueEventListener{
                                        override fun onDataChange(snapshot1: DataSnapshot) {
                                            job = snapshot1.getValue<Job>()
                                            appliedJobButton.visibility = View.VISIBLE
                                            Log.d("JobFound", job.toString())
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                            }
                        }

                        if(message.cv.toString() != "" || message.cv != null) {
                            Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("Cvs").addValueEventListener(
                                    object : ValueEventListener {
                                        override fun onDataChange(snapshot2: DataSnapshot) {
                                            for(cvChild in snapshot2.children){
                                                for (cvSnapshot in cvChild.children){
                                                    if(cvSnapshot.key == message.cv){
                                                        cv = cvSnapshot.getValue<Cv>()
                                                        appliedCvButton.visibility = View.VISIBLE
                                                    }

                                                    Log.d("CVCC", cvSnapshot.toString())
                                                }
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    }
                                )
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        appliedJobButton.setOnClickListener{

            val jobDetailedFragment = jobDetailedFragment()
            val bundle = Bundle()
            bundle.putParcelable("Job", job)
            jobDetailedFragment.arguments = bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, jobDetailedFragment)?.addToBackStack(null)
            transaction?.commit()
        }

        appliedCvButton.setOnClickListener{
            val cvDetailedFragment = CvDetailedFragment()
            val transaction = fragmentManager?.beginTransaction()
            val bundle = Bundle()
            bundle.putParcelable("CV", cv)
            bundle.putSerializable("User", user)
            bundle.putBoolean("IsOffer", false)

            var bitmap: Bitmap? = null
            bundle.putParcelable("CVPhoto", bitmap)
            cvDetailedFragment.arguments = bundle
            transaction?.replace(R.id.fragmentContainer, cvDetailedFragment)?.addToBackStack(null)
            transaction?.commit()

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messenger, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MessengerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}