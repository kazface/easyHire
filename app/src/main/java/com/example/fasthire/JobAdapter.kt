package com.example.fasthire

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class JobAdapter(private val jobList: ArrayList<Job>, var isSavedShow: Boolean = true): RecyclerView.Adapter<JobAdapter.ViewHolder>() {
    var savedJobList: ArrayList<Job>? = null
    init{
        savedJobList = ArrayList(jobList)
    }

    var onItemClick : ((Job) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.job_card, parent, false)
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job: Job = jobList[position]
        holder.companyName.text = job.companyName
        holder.jobPositionText.text = job.title
        holder.jobLocation.text = job.location
        holder.jobSalary.text = job.salary.toString()
        holder.periodJob.text = job.period
        holder.jobType.text = job.type
        holder.jobCardView.setOnClickListener{
            onItemClick?.invoke(job)
        }
        holder.saveCheckBox.isChecked = (job.saved == 1)

        if(!isSavedShow){
            holder.saveCheckBox.visibility = View.INVISIBLE
            holder.applyButton.visibility = View.INVISIBLE

        }


        holder.saveCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            var firebaseAuth: FirebaseAuth
            var database : FirebaseDatabase = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/")
            firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser!!.uid

            var jobsRef = database
                .getReference("SavedJob")
            Log.d("IsChecked", isChecked.toString())
            if(isChecked){
                job.saved = 1
                job.id?.let {
                    jobsRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child(it)
                        .setValue(true).addOnCompleteListener{
                        }
                };
            }else{
                job.saved = 0
                job.id?.let { jobsRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child(it).removeValue() }
            }
        }

    }

    override fun getItemCount(): Int {
        return jobList.size
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var jobPositionText = itemView.findViewById<TextView>(R.id.jobPositionName);
        var companyName = itemView.findViewById<TextView>(R.id.companyName);
        var jobLocation = itemView.findViewById<TextView>(R.id.jobLocation);
        var jobSalary = itemView.findViewById<TextView>(R.id.jobSalary);
        var periodJob = itemView.findViewById<TextView>(R.id.period);
        var jobType = itemView.findViewById<TextView>(R.id.jobType);
        var applyButton = itemView.findViewById<TextView>(R.id.applyButton);
        var saveCheckBox = itemView.findViewById<CheckBox>(R.id.saveCheckBox);
        var jobCardView = itemView.findViewById<MaterialCardView>(R.id.jobCardView);
        var searchView = itemView.findViewById<SearchView>(R.id.jobSearchView);
    }


}