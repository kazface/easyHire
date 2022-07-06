package com.example.fasthire

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [jobDetailedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class jobDetailedFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var job: Job? = null
    private var isEdit: Boolean? = false
    private var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            job = it.getParcelable("Job")
            isEdit = it.getBoolean("isEdit")
            user = it.getSerializable("User") as User?
        }



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var jobDescrText = view.findViewById<TextView>(R.id.jobDescrText)
        var jobPositionNameText = view.findViewById<TextView>(R.id.jobPositionNameText)
        var companyNameText = view.findViewById<TextView>(R.id.companyName)
        var jobLocation = view.findViewById<TextView>(R.id.jobLocation)
        var vaccinationInfoLayout = view.findViewById<LinearLayout>(R.id.vaccinationInfoLayout)
        var skillsNeededText = view.findViewById<TextView>(R.id.skillsNeededText)
        var applyButton = view.findViewById<Button>(R.id.applyButton)

        var deleteJob = view.findViewById<AppCompatButton>(R.id.deleteJob)
        var editJob = view.findViewById<AppCompatButton>(R.id.editJob)



        fun Int.toBoolean() = this == 1
        var companyLogoImage = view.findViewById<ImageView>(R.id.companyLogoImage);

        //companyLogos/${job?.companyName}.png

        val storage = FirebaseStorage.getInstance()
        val companyLogoRef = storage.getReferenceFromUrl("gs://fasthire-ae6c0.appspot.com/companyLogos/${job?.companyName}.png")


        GlideApp.with(view.context)
            .load(companyLogoRef)
            .placeholder(R.drawable.ic_baseline_font_download_24)
            .into(companyLogoImage)



        vaccinationInfoLayout.visibility = if(job?.vaccination!!.toBoolean()) View.VISIBLE else View.INVISIBLE
        skillsNeededText.text = job?.skills?.joinToString(separator = "\n")
        jobLocation.text = job?.location
        companyNameText.text = job?.companyName
        jobDescrText.text = job?.jobDescription!!.replace("\\n", "\n")
        jobPositionNameText.text = job?.title
        Log.d("isApply", isEdit.toString())
//        applyButton.visibility = if(isApply) View.VISIBLE else View.INVISIBLE

        if(isEdit == true){
            applyButton.visibility = View.INVISIBLE
        }
        if(isEdit == false){
            deleteJob.visibility = View.INVISIBLE
            editJob.visibility = View.INVISIBLE

        }


        editJob.setOnClickListener{
            val editJobFragment = EditJobFragment();
            val bundle: Bundle = Bundle();
            bundle.putParcelable("Job", job)
            editJobFragment.arguments = bundle;
            fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer, editJobFragment)
                ?.addToBackStack(null)?.commit()
        }


        deleteJob.setOnClickListener{
            val builder = AlertDialog.Builder(view.context)
            builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    val progressBar = ProgressDialog(activity)
                    progressBar.setMessage("Deleting...")
                    progressBar.setCancelable(false)
                    progressBar.show()
                    var database = job!!.id?.let { it1 ->
                        Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("Jobs")
                            .child(it1).removeValue().addOnSuccessListener {
                                progressBar.hide()
                            }
                    }
                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()


        }

        applyButton.setOnClickListener{
            val selectApplicantCvsFragment = SelectApplicantCvsFragment()
            val transaction = fragmentManager?.beginTransaction()
            val bundle = Bundle()
            bundle.putSerializable("User", user)
            bundle.putParcelable("Job", job)
            selectApplicantCvsFragment.arguments = bundle
            transaction?.replace(R.id.fragmentContainer, selectApplicantCvsFragment)?.addToBackStack(null)
            transaction?.commit()


        }




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_detailed, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment jobDetailedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            jobDetailedFragment().apply {
                arguments = Bundle().apply {


                }
            }
    }
}

fun getDrawable(context: Context, ImageName: String?): Int {
    return context.getResources().getIdentifier(ImageName, "drawable", context.getPackageName())
}
