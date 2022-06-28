package com.example.fasthire

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ApplicantProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ApplicantProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var user: User? = null
    private val PICK_IMAGE_REQUEST = 71
    lateinit var imageUri: Uri
    lateinit var profilePhotoRef: StorageReference

    private lateinit var userProfilePic: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("User") as User?
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userFullNameText = view.findViewById<TextView>(R.id.userFullName)
        var userEmail = view.findViewById<TextView>(R.id.userEmail)
        var userPhone = view.findViewById<TextView>(R.id.userPhone)
        var addImageButton = view.findViewById<Button>(R.id.addImageButton)
        var progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        val storage = FirebaseStorage.getInstance()

        var exitButton = view.findViewById<AppCompatButton>(R.id.exitButton)

        var myCvButton = view.findViewById<AppCompatButton>(R.id.myCvButton)
        var jobResponsesButton = view.findViewById<AppCompatButton>(R.id.jobResponsesButton)



        profilePhotoRef = storage.getReferenceFromUrl("gs://fasthire-ae6c0.appspot.com/profilePhotos/${user!!.email}")
        userProfilePic = view.findViewById<ImageView>(R.id.userProfilePic)
        progressBar.visibility = View.VISIBLE
        userProfilePic.visibility = View.INVISIBLE

        GlideApp.with(requireView().context)
            .load(profilePhotoRef)
            .apply(RequestOptions())
            .dontTransform()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.profile_pic_gray)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    userProfilePic.visibility = View.VISIBLE
                    return false;
                }
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    userProfilePic.visibility = View.VISIBLE
                    return false;
                }
            })
            .into(userProfilePic)


        userFullNameText.text = user!!.fullName
        userEmail.text = user!!.email
        userPhone.text = user!!.phone

        addImageButton.setOnClickListener{

            launchGallery()
        }

        myCvButton.setOnClickListener{
            val myCvFragmentTransaction = ApplicantCvsFragment()
            val transaction = fragmentManager?.beginTransaction()
            val bundle = Bundle()
            bundle.putSerializable("User", user)
            myCvFragmentTransaction.arguments = bundle
            transaction?.replace(R.id.fragmentContainer, myCvFragmentTransaction)?.addToBackStack(null)
            transaction?.commit()
        }

        exitButton.setOnClickListener{
            val intent = Intent(activity, SignInActivity::class.java)

            startActivity(intent)

            FirebaseAuth.getInstance().signOut()
        }

    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode ==  RESULT_OK){

            imageUri = data?.data!!
            uploadToFirebaseStorage()
            userProfilePic.setImageURI(imageUri)
        }


    }



    private fun uploadToFirebaseStorage(){
        val progressBar = ProgressDialog(activity)
        progressBar.setMessage("Uploading...")
        progressBar.setCancelable(false)
        progressBar.show()

        profilePhotoRef.putFile(imageUri).addOnSuccessListener {
            progressBar.hide()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_applicant_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment applicantProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ApplicantProfileFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}