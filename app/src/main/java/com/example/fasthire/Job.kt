package com.example.fasthire

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Job {
    constructor(){

    }


    constructor(companyName: String, location: String, period: String, salary: Long, userId: String, title: String, type: String) {
        this.companyName = companyName
        this.location = location
        this.period = period
        this.salary = salary
        this.userId = userId
        this.title = title
        this.type = type
    }
    var type: String =""
    var title: String =""
    var companyName: String =""
    var location: String =""
    var period: String =""
    var salary: Long  = 0
    var userId: String =""

    companion object {
        private lateinit var database: DatabaseReference

        fun getAllJobs(): ArrayList<Job> {
            var jobs: ArrayList<Job> = ArrayList<Job>()

            database = FirebaseDatabase.getInstance().getReference("Jobs")
            database.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        Log.d("Children", snapshot.children.toString())
                        for(jobSnapshot in snapshot.children){
                            val job = jobSnapshot.getValue(Job::class.java)
                            jobs.add(job!!)

                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            return jobs;

        }



    }

}



