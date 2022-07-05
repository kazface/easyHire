package com.example.fasthire

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.card.MaterialCardView
import com.google.firebase.storage.FirebaseStorage

class UserChatAdapter(private val userChatList: ArrayList<Message>, private val context : Context): RecyclerView.Adapter<UserChatAdapter.ViewHolder>() {
    var saveduserChatList: ArrayList<Message>? = null
    init{
        saveduserChatList = ArrayList(userChatList)
    }

    var onItemClick : ((Message) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_card, parent, false)
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userChat: Message = userChatList[position]
        holder.userLastMessage.text = userChat.message
        holder.userName.text = userChat.fromName
        val storage = FirebaseStorage.getInstance()
        var profilePhotoRef = storage.getReferenceFromUrl("gs://fasthire-ae6c0.appspot.com/profilePhotos/${userChat.fromEmail}")
        GlideApp.with(context)
            .load(profilePhotoRef)
//            .dontTransform()
//            .skipMemoryCache(true)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.ic_baseline_person_24)            .listener(object :
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

        holder.userCardView.setOnClickListener{
            onItemClick?.invoke(userChat)
        }
    }

    override fun getItemCount(): Int {
        return userChatList.size
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var userName = itemView.findViewById<TextView>(R.id.userName);
        var userPictureImage = itemView.findViewById<ImageView>(R.id.userPictureImage);
        var userLastMessage = itemView.findViewById<TextView>(R.id.userLastMessage);
        var progressBar = itemView.findViewById<ProgressBar>(R.id.progressUserBar)
        var userCardView = itemView.findViewById<MaterialCardView>(R.id.userCardView)
    }



}