package com.example.testtubesmanual

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testtubesmanual.databinding.FragmentAdminProfileBinding
import com.example.testtubesmanual.databinding.FragmentProfileUserBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminProfileFragment : Fragment() {
    private var _binding: FragmentAdminProfileBinding?= null
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        binding?.logoutButton?.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireActivity(),WelcomeActivity::class.java))
            activity?.finish()
        }
        binding?.editButton?.setOnClickListener {
            startActivity(Intent(requireActivity(),EditProfileActivity::class.java))
        }
        binding?.addWisataButton?.setOnClickListener {
            startActivity(Intent(requireActivity(),AddWisataActivity::class.java))
        }
        binding?.deleteWisataButton?.setOnClickListener {
            startActivity(Intent(requireActivity(),RemoveActivity::class.java))
        }
        binding?.editWisataButton?.setOnClickListener {
            startActivity(Intent(requireActivity(),EditWisataActivity::class.java))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminProfileBinding.inflate(inflater,container,false)
        return binding?.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}