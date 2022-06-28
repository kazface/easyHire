package com.example.fasthire
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.children
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [CreateApplicantCvPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateApplicantCvPage : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("User") as User
        }
    }
    private var database : FirebaseDatabase = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var close = view.findViewById<AppCompatButton>(R.id.closeFragment)
        var addSkillButton = view.findViewById<MaterialButton>(R.id.addSkillButton)
        val skillsLayout = view.findViewById<LinearLayout>(R.id.skillsLayout)
        var deleteSkillButton = view.findViewById<MaterialButton>(R.id.deleteSkillButton)

        var createCvButton = view.findViewById<MaterialButton>(R.id.createCvButton)

        var cvTitle = view.findViewById<TextInputEditText>(R.id.cvTitle)

        var cvLocation = view.findViewById<TextInputEditText>(R.id.cvLocation)

        var cvVaccination = view.findViewById<SwitchMaterial>(R.id.cvVaccination)

        var cvDescriptionText = view.findViewById<TextInputEditText>(R.id.cvDescriptionText)

        fun isFormValid():Boolean{
            if(cvTitle.text!!.isEmpty()
                || cvLocation.text!!.isEmpty()
                || cvDescriptionText.text!!.isEmpty()
            ){
                return false
            }

            return true
        }


        createCvButton.setOnClickListener{
            if(isFormValid()){
                val progressBar = ProgressDialog(activity)

                progressBar.setMessage("Uploading your CV...")
                progressBar.setCancelable(false)
                progressBar.show()

                var skills: ArrayList<Any> = arrayListOf()
                for(child in skillsLayout.children){
                    var textView = child as TextInputEditText
                    skills.add(textView.text.toString())
                }
                var cv: Cv = Cv(user.email, cvDescriptionText.text.toString(), user.fullName, cvLocation.text.toString(), user.phone, skills, cvTitle.text.toString(), null, if (cvVaccination.isChecked)1 else 0)
                Log.d("CV", cv.skills.toString())
                FirebaseAuth.getInstance().uid?.let { it1 -> database.getReference("Cvs").child(it1).push().setValue(cv).addOnSuccessListener {
                    progressBar.hide()
                } }

            }

        }


        addSkillButton.setOnClickListener{
            val et = TextInputEditText(view.context)
            et.hint = "Skill ${skillsLayout.childCount+1}"
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            skillsLayout.addView(et, lp)
        }


        deleteSkillButton.setOnClickListener{
            skillsLayout.removeViewAt(skillsLayout.childCount-1)
        }


        close.setOnClickListener{
            val fm: FragmentManager = requireActivity().supportFragmentManager
            fm.popBackStack()
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_applicant_cv_page, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateApplicantCvPage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CreateApplicantCvPage().apply {
                arguments = Bundle().apply {
                }
            }
    }
}