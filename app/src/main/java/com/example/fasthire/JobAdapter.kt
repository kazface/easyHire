package com.example.fasthire

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JobAdapter(private val jobList: ArrayList<Job>): RecyclerView.Adapter<JobAdapter.ViewHolder>() {

    var onItemClick : ((Job) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.job_card, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job = jobList[position]
        holder.companyName.text = job.companyName
        holder.jobPositionText.text = job.title
        holder.jobLocation.text = job.location
        holder.jobSalary.text = job.salary.toString()
        holder.periodJob.text = job.period
        holder.jobType.text = job.type


        holder.applyButton.setOnClickListener{

            onItemClick?.invoke(job)

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
        var saveCheckBox = itemView.findViewById<TextView>(R.id.saveCheckBox);



    }

}