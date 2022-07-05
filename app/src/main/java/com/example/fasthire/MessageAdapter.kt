package com.example.fasthire

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.sql.Date
import java.text.SimpleDateFormat

class MessageAdapter(private val messageList: ArrayList<Message>, private val context : Context): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    var savedmessageList: ArrayList<Message>? = null
    init{
        savedmessageList = ArrayList(messageList)
    }

    val VIEW_TYPE_SENDER = 1
    val VIEW_TYPE_RECEIVER = 2


    var onItemClick : ((Message) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView: View = if(viewType == VIEW_TYPE_SENDER){
            LayoutInflater.from(parent.context).inflate(R.layout.message_right_card, parent, false)
        }else{
            LayoutInflater.from(parent.context).inflate(R.layout.message_left_card, parent, false)
        }
        return ViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        val message: Message = messageList[position]
        if(message.fromId == FirebaseAuth.getInstance().uid){
            return VIEW_TYPE_SENDER
        }else{
            return VIEW_TYPE_RECEIVER
        }
//        return super.getItemViewType(position)
    }

    fun getDateTime(timeStamp: Int): String? {
        val dt = Date(timeStamp.toLong())
        val sdf = SimpleDateFormat("hh:mm aa")
        val time = sdf.format(dt)
        return time
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message: Message = messageList[position]

        holder.txtMessage.text = message.message
        holder.txtTime.text = getDateTime(message.unixTime!!)


        val storage = FirebaseStorage.getInstance()
        holder.progressBar.visibility = View.VISIBLE
        holder.userPictureImage.visibility = View.INVISIBLE
        var profilePhotoRef = storage.getReferenceFromUrl("gs://fasthire-ae6c0.appspot.com/profilePhotos/${message.fromEmail}")
        GlideApp.with(context)
            .load(profilePhotoRef)
            .dontTransform()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.ic_baseline_person_24)
            .listener(object :
                RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    holder.userPictureImage.visibility = View.VISIBLE
                    return false;
                }
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    holder.userPictureImage.visibility = View.VISIBLE
                    return false;
                }
            })
            .into(holder.userPictureImage)

    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var txtMessage = itemView.findViewById<TextView>(R.id.txtMessage)
        var userPictureImage = itemView.findViewById<ImageView>(R.id.userProfilePic)
        var progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
        var txtTime = itemView.findViewById<TextView>(R.id.txtTime)




//        var userName = itemView.findViewById<TextView>(R.id.userName);
//        var userPictureImage = itemView.findViewById<ImageView>(R.id.userPictureImage);
//        var userLastMessage = itemView.findViewById<TextView>(R.id.userLastMessage);
//        var progressBar = itemView.findViewById<ProgressBar>(R.id.progressUserBar)
    }


}