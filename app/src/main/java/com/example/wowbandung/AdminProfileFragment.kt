package com.example.wowbandung

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.wowbandung.databinding.FragmentAdminProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AdminProfileFragment : Fragment() {
    private var _binding: FragmentAdminProfileBinding?= null
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val profilePicture = currentUser?.photoUrl
        binding?.apply {
            logoutButton.setOnClickListener{
                auth.signOut()
                startActivity(Intent(requireActivity(),WelcomeActivity::class.java))
                activity?.finish()
            }
            editButton.setOnClickListener {
                startActivity(Intent(requireActivity(),EditProfileActivity::class.java))
            }
            addWisataButton.setOnClickListener {
                startActivity(Intent(requireActivity(),AddWisataActivity::class.java))
            }
            deleteWisataButton.setOnClickListener {
                startActivity(Intent(requireActivity(),RemoveActivity::class.java))
            }
            editWisataButton.setOnClickListener {
                startActivity(Intent(requireActivity(),EditActivity::class.java))
            }
            resetPassword.setOnClickListener {
                startActivity(Intent(requireActivity(),ResetPasswordActivity::class.java))
            }
            Glide.with(requireActivity())
                .load(profilePicture)
                .centerCrop()
                .into(ivProfile)
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminProfileBinding.inflate(inflater,container,false)
        return binding?.root
    }
}