package com.example.fasthire

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class CreateJobFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var user: User? = null



    var selectedCompany: String? = null
    var selectedDurationPeriod: String? = null

    lateinit var radioGroup: RadioGroup
    lateinit var jobTypeRadioButton: RadioButton
    lateinit var database: FirebaseDatabase



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        var selectCompanySpinner = view.findViewById<Spinner>(R.id.selectCompanySpinner)
        var selectDurationSpinner = view.findViewById<Spinner>(R.id.jobPeriodType)

        radioGroup = view.findViewById<RadioGroup>(R.id.jobTypesGroup)
        database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/")
        var companyArray = arrayListOf("Select company")

        var durationArray = arrayListOf("Select period", "day","month")

        FirebaseAuth.getInstance().uid?.let {
            database.getReference("Companies").child(it).addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (companySnapshot in snapshot.children){
                            var company = companySnapshot.getValue<Company>()
                            companyArray.add(company?.name.toString())
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }

        var companiesAdapter = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_spinner_dropdown_item, companyArray
        )
        selectCompanySpinner.adapter = companiesAdapter
        selectCompanySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCompany = if(p2 != 0) companyArray[p2] else null
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
                selectedDurationPeriod = if(p2 != 0) companyArray[p2] else null
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
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
            user = it.getSerializable("User") as User
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_job, container, false)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateJobFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CreateJobFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}