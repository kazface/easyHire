package com.example.fasthire

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class CvAdapter(val context: Context, private val cvList: ArrayList<Cv>, val isSavedShow: Boolean = true): RecyclerView.Adapter<CvAdapter.ViewHolder>() {
    var savedJobList: ArrayList<Cv>? = null
    init{
        savedJobList = ArrayList(cvList)
    }





    var onItemClick : ((cv: Cv, bitmap: Bitmap) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cv_card, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val cv: Cv = cvList[position]


        holder.cvCardView.setOnClickListener{
            onItemClick?.invoke(cv, holder.cvPictureImage.drawable.toBitmap())
        }

        if(!isSavedShow){
            holder.saveCheckBox.visibility = View.INVISIBLE
        }

        val storage = FirebaseStorage.getInstance()
        var profilePhotoRef = storage.getReferenceFromUrl("gs://fasthire-ae6c0.appspot.com/profilePhotos/${cv.email}")

        holder.progressBar.visibility = View.VISIBLE

        holder.cvPictureImage.visibility = View.INVISIBLE

        GlideApp.with(context)
            .load(profilePhotoRef)
            .dontTransform()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
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
                    holder.cvPictureImage.visibility = View.VISIBLE
                    return false;
                }
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    holder.cvPictureImage.visibility = View.VISIBLE
                    return false;
                }
            })
            .into(holder.cvPictureImage)

        holder.saveCheckBox.isChecked = (cv.saved == 1)

        holder.cvTitle.text = cv.title.toString()
        holder.cvDescriptionText.text = cv.description.toString()
        holder.cvLocation.text = cv.location.toString()
        holder.cvFullName.text = cv.fullName.toString()

        holder.saveCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            var firebaseAuth: FirebaseAuth
            var database : FirebaseDatabase = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/")
            firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser!!.uid

            var jobsRef = database
                .getReference("SavedCvs")
            Log.d("IsChecked", isChecked.toString())
            if(isChecked){
                cv.saved = 1
                cv.id?.let {
                    jobsRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child(it)
                        .setValue(true).addOnCompleteListener{
                        }
                };
            }else{
                cv.saved = 0
                cv.id?.let { jobsRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child(it).removeValue() }
            }
        }
    }

    override fun getItemCount(): Int {
        return cvList.size
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){


        var cvCardView = itemView.findViewById<MaterialCardView>(R.id.cvCardView);
        var cvTitle = itemView.findViewById<TextView>(R.id.cvTitle);
        var cvDescriptionText = itemView.findViewById<TextView>(R.id.cvDescriptionText);
        var cvLocation = itemView.findViewById<TextView>(R.id.cvLocation);
        var cvFullName = itemView.findViewById<TextView>(R.id.cvFullName);
        var progressBar = itemView.findViewById<ProgressBar>(R.id.progressCvBar);

        var cvPictureImage = itemView.findViewById<ImageView>(R.id.cvPictureImage);


        var saveCheckBox = itemView.findViewById<CheckBox>(R.id.saveCheckBox);

    }


}