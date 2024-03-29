package com.example.fasthire

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.storage.FirebaseStorage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [CvDetailedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CvDetailedFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var cv: Cv
    private var cvPhoto: Bitmap? = null

    private  var isOffer = false
    private  var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cv = it.getParcelable<Cv>("CV")!!
            cvPhoto = it.getParcelable<Bitmap>("CVPhoto")
            isOffer = it.getBoolean("isOffer")
            user = it.getSerializable("User") as User?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cv_detailed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var cvPictureImage = view.findViewById<ImageView>(R.id.cvPictureImage)
        var userFullName = view.findViewById<TextView>(R.id.userFullName)
        var cvTitle = view.findViewById<TextView>(R.id.cvTitle)
        var vaccinationInfoLayout = view.findViewById<LinearLayout>(R.id.vaccinationInfoLayout)
        var cvLocation = view.findViewById<TextView>(R.id.cvLocation)
        var cvPhone = view.findViewById<TextView>(R.id.cvPhone)
        var cvDescriptionText = view.findViewById<TextView>(R.id.cvDescriptionText)
        var cvSkills = view.findViewById<TextView>(R.id.cvSkills)
        var offerButton = view.findViewById<AppCompatButton>(R.id.offerButton)

        if(!isOffer){
            offerButton.visibility = View.INVISIBLE
        }



        userFullName.text = cv.fullName
        cvTitle.text = cv.title
        vaccinationInfoLayout.visibility = if (cv.vaccination == 1) View.VISIBLE else View.INVISIBLE
        cvLocation.text = cv.location
        cvPhone.text = cv.phone
        cvDescriptionText.text = cv.description
        var skills = cv.skills?.joinToString(separator = "\n")
        cvSkills.text = skills

        offerButton.setOnClickListener{

            val selectJob = SelectJob();
            val bundle: Bundle = Bundle();
            bundle.putSerializable("User", user)
            bundle.putParcelable("Cv", cv)

            selectJob.arguments = bundle;
            fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer, selectJob)
                ?.addToBackStack(null)?.commit()



        }

        if(cvPhoto == null){
            val storage = FirebaseStorage.getInstance()
            var profilePhotoRef = storage.getReferenceFromUrl("gs://fasthire-ae6c0.appspot.com/profilePhotos/${cv.email}")
            GlideApp.with(view.context)
                .load(profilePhotoRef)
                .placeholder(R.drawable.ic_baseline_font_download_24)
                .into(cvPictureImage)

        }else{
            cvPictureImage.setImageBitmap(cvPhoto)
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CvDetailedFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}