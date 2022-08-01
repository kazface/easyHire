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
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class SelectChatFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("User") as User
        }
    }
    lateinit var user: User

    lateinit var chatRecyclerView: RecyclerView
    lateinit var chatList: ArrayList<Message>;
    lateinit var chatAdapter: UserChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)


        chatRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
        chatRecyclerView.setHasFixedSize(true)

        chatList = arrayListOf();

        chatAdapter = UserChatAdapter(chatList, view.context)

        chatRecyclerView.adapter = chatAdapter

        var messagesRef = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("messages")

        var uid = FirebaseAuth.getInstance().uid


        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var tempArray = arrayListOf<Message>()
                    for(messageSnapshot in snapshot.children){
                        var message: Message = messageSnapshot.getValue<Message>()!!
                        var messageAdd: Message = messageSnapshot.getValue<Message>()!!
                        Log.d("fromId", message.fromId.toString())
                        Log.d("fromId", FirebaseAuth.getInstance().uid.toString())

                        if(message.fromId.toString() == FirebaseAuth.getInstance().uid.toString() || message.toId.toString() == FirebaseAuth.getInstance().uid.toString()){
                            messageAdd.fromId = message.toId
                            messageAdd.fromEmail = message.toEmail
                            messageAdd.fromName = message.toName
                            Log.d("messageAdd",messageAdd.fromId.toString())
                            tempArray.add(messageAdd)
                        }
                    }
                    Log.d("CheckArray", tempArray.toString())

                    var filtred = tempArray.groupBy { it.fromId }.map {
                        it.value.maxByOrNull { it.unixTime!! }
                    }
                    chatList.addAll(filtred.filterNotNull())
                    chatAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        chatAdapter.onItemClick = {
            val messengerFragment = MessengerFragment()
            val transaction = fragmentManager?.beginTransaction()
            val bundle = Bundle()
            bundle.putParcelable("Message", it)
            bundle.putSerializable("User", user)
            messengerFragment.arguments = bundle
            transaction?.replace(R.id.fragmentContainer, messengerFragment)?.addToBackStack(null)
            transaction?.commit()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_chat, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SelectChatFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}