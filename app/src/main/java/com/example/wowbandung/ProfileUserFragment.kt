package com.example.wowbandung

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.wowbandung.databinding.FragmentProfileUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileUserFragment : Fragment() {
    private var _binding: FragmentProfileUserBinding?= null
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog:ProgressDialog
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val profilePicture = currentUser?.photoUrl
        binding?.apply {
            Glide.with(this@ProfileUserFragment)
                .load(profilePicture)
                .centerCrop()
                .into(ivProfile)
            logoutButton.setOnClickListener {
                auth.signOut()
                startActivity(Intent(requireActivity(),WelcomeActivity::class.java))
                activity?.finish()
            }
            editButton.setOnClickListener {
                startActivity(Intent(requireActivity(),EditProfileActivity::class.java))
            }
            resetPassword.setOnClickListener{
                startActivity(Intent(requireActivity(),ResetPasswordActivity::class.java))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =FragmentProfileUserBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding?.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        const val NAMA = "name"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}