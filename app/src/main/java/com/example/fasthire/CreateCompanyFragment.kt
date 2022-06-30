package com.example.fasthire

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [CreateCompanyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateCompanyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var user: User? = null
    private var imageUri: Uri? = null
    private  var companyLogoImage: ImageView? = null
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("User") as User
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var addImageButton = view.findViewById<AppCompatButton>(R.id.addImageButton)
         companyLogoImage = view.findViewById<ImageView>(R.id.companyLogoImage)
        var companyName = view.findViewById<TextInputEditText>(R.id.companyName)
        var companyAddress = view.findViewById<TextInputEditText>(R.id.companyAddress)
        var database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Companies")
        var createCompanyButton = view.findViewById<AppCompatButton>(R.id.createCompanyButton)

        fun isFormValid(): Boolean{
            if(companyName.text!!.isEmpty() || companyAddress.text!!.isEmpty()){
                Toast.makeText(view.context, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }


        createCompanyButton.setOnClickListener{

            if(isFormValid()){
                val progressBar = ProgressDialog(activity)
                progressBar.setMessage("Creating...")
                progressBar.setCancelable(false)
                progressBar.show()


                var company: Company = Company(null, companyName.text.toString(), companyAddress.text.toString())


                val storage = FirebaseStorage.getInstance()
                var profilePhotoRef = storage.getReferenceFromUrl("gs://fasthire-ae6c0.appspot.com/companyLogos/${company.name}")

            FirebaseAuth.getInstance().uid?.let {
                database.child(it).push().setValue(company).addOnSuccessListener {
                    profilePhotoRef.putFile(imageUri!!).addOnSuccessListener {
                        progressBar.hide()
                        Toast.makeText(view.context, "Success", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        progressBar.hide()
                        Toast.makeText(view.context, "Failed. Please try again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            }
        }


        addImageButton.setOnClickListener{
            launchGallery()
        }
    }

    private fun launchGallery(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)


        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            imageUri = data?.data!!
            companyLogoImage!!.setImageURI(imageUri)
        }



    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_company, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateCompanyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CreateCompanyFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}