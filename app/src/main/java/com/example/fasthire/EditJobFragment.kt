package com.example.fasthire

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class EditJobFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var job: Job


    var selectedCompany: Company? = null
    var companies: ArrayList<Company>? = null


    var selectedDurationPeriod: String? = null

    lateinit var radioGroup: RadioGroup
    lateinit var jobTypeRadioButton: RadioButton
    lateinit var database: FirebaseDatabase



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var selectCompanySpinner = view.findViewById<Spinner>(R.id.selectCompanySpinner)
        var selectDurationSpinner = view.findViewById<Spinner>(R.id.jobPeriodType)
        var addSkillButton = view.findViewById<MaterialButton>(R.id.addSkillButton)
        val skillsLayout = view.findViewById<LinearLayout>(R.id.skillsLayout)
        var deleteSkillButton = view.findViewById<MaterialButton>(R.id.deleteSkillButton)
        var jobPeriodDurationLayout = view.findViewById<TextInputLayout>(R.id.jobPeriodDurationLayout)
        var jobPeriodDuration = view.findViewById<TextInputEditText>(R.id.jobPeriodDuration)
        var placeJobButton = view.findViewById<AppCompatButton>(R.id.placeJobButton)

        radioGroup = view.findViewById<RadioGroup>(R.id.jobTypesGroup)
        database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/")
        var companyNames = arrayListOf("Select company")
        companies = arrayListOf()

        var durationArray = arrayListOf("Select period", "day","month")

        FirebaseAuth.getInstance().uid?.let {
            database.getReference("Companies").child(it).addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (companySnapshot in snapshot.children){
                            var company = companySnapshot.getValue<Company>()
                            companyNames.add(company?.name.toString())
                            companies?.add(company!!)
                            if(company!!.name == job.companyName){
                                selectedCompany = company
                                selectCompanySpinner.setSelection(companyNames.indexOf(selectedCompany!!.name))

                            }

                        }




                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }

        var companiesAdapter = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_spinner_dropdown_item, companyNames
        )




        selectCompanySpinner.adapter = companiesAdapter
        selectCompanySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCompany = if(p2 != 0) companies?.get(p2-1) else null
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        var durationAdapter = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_spinner_dropdown_item, durationArray
        )
        selectDurationSpinner.adapter = durationAdapter
        selectDurationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                selectedDurationPeriod = if(p2 != 0) durationArray[p2] else null
                jobPeriodDurationLayout.isEnabled = selectedDurationPeriod != null
                jobPeriodDurationLayout.suffixText = selectedDurationPeriod
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


        addSkillButton.setOnClickListener{
            val et = TextInputEditText(view.context)
            et.hint = "Skill ${skillsLayout.childCount+1}"
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            skillsLayout.addView(et, lp)

        }
        Log.d("JobId", job.id.toString())

        deleteSkillButton.setOnClickListener{
            skillsLayout.removeViewAt(skillsLayout.childCount-1)
        }

        var jobTitleText = view.findViewById<TextInputEditText>(R.id.jobTitleText)
        var jobSalaryText = view.findViewById<TextInputEditText>(R.id.jobSalaryText)

        var jobVaccination = view.findViewById<SwitchMaterial>(R.id.jobVaccination)
        var jobDescrrText = view.findViewById<TextInputEditText>(R.id.jobDescrrText)

        jobTitleText.setText(job.title)

        jobSalaryText.setText(job.salary.toString())

        jobVaccination.isChecked = job.vaccination == 1

        jobDescrrText.setText(job.jobDescription)


        var period = job.period!!.split(" ")[1]
        var duration = job.period!!.split(" ")[0]


        selectDurationSpinner.setSelection(durationArray.indexOf(period))

        jobPeriodDuration.setText(duration)


        for(skill in job.skills!!){

            val et = TextInputEditText(view.context)
            et.hint = "Skill ${skillsLayout.childCount}"

            et.setText(skill.toString())

            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            skillsLayout.addView(et, lp)

        }





        fun isFormValid(): Boolean{
            if(jobTitleText.text!!.isEmpty()){
                Toast.makeText(view.context, "Provide Job title", Toast.LENGTH_SHORT).show()
                return false
            }
            if(jobSalaryText.text!!.isEmpty()){
                Toast.makeText(view.context, "Provide Job Salary", Toast.LENGTH_SHORT).show()
                return false
            }
            if(jobDescrrText.text!!.isEmpty()){
                Toast.makeText(view.context, "Provide Job description!", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }





        var database : FirebaseDatabase = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/")


        placeJobButton.setOnClickListener{



            if(isFormValid()){
                val progressBar = ProgressDialog(activity)
                progressBar.setMessage("Updating...")
                progressBar.setCancelable(false)
                progressBar.show()

                var skills: ArrayList<Any> = arrayListOf()
                for(child in skillsLayout.children){
                    var textView = child as TextInputEditText
                    skills.add(textView.text.toString())
                }

                var job_edited: Job = Job(checkButton(), jobTitleText.text.toString(),selectedCompany?.name.toString(), selectedCompany?.location, jobPeriodDuration.text.toString() +" "+selectedDurationPeriod,
                jobSalaryText.text.toString().toLong(), FirebaseAuth.getInstance().uid, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) , jobDescrrText.text.toString(), if(jobVaccination.isChecked) 1 else 0, skills, null, null)                                                                                                    //Unix time
                job.id?.let {
                    it1 ->
                    database.getReference("Jobs").child(it1).setValue(job_edited).addOnSuccessListener {
                    progressBar.hide()

                        val jobDetailedFragment = jobDetailedFragment()
                        val bundle = Bundle()
                        bundle.putParcelable("Job", job_edited)
                        bundle.putBoolean("isEdit", true)

                        jobDetailedFragment.arguments = bundle
                        val transaction = fragmentManager?.beginTransaction()
                        transaction?.replace(R.id.fragmentContainer, jobDetailedFragment)
                        transaction?.commit()


                    }
                }
            }



        }
    }




    public fun checkButton(): String{
        var radioId =  radioGroup.checkedRadioButtonId
        jobTypeRadioButton = requireView().findViewById(radioId)
        return jobTypeRadioButton.text.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            job = it.getParcelable("Job")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_job, container, false)
    }
    companion object {

        @JvmStatic
        fun newInstance() =
            EditJobFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}