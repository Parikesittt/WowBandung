package com.example.testtubesmanual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.testtubesmanual.adapter.FavWisataAdapter
import com.example.testtubesmanual.adapter.WisataAdapter
import com.example.testtubesmanual.data.fav
import com.example.testtubesmanual.data.listWisata
import com.example.testtubesmanual.databinding.ActivityFavoriteBinding
import com.example.testtubesmanual.databinding.FragmentFavoriteBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding?=null
    private val binding get() = _binding
    private lateinit var destinationList : ArrayList<listWisata>
    private lateinit var favList : ArrayList<fav>
    private lateinit var rvAdapter: WisataAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        destinationList = arrayListOf()
        favList = arrayListOf()
        auth = Firebase.auth
        db = Firebase.database
        rvAdapter = WisataAdapter(destinationList)
        fetchData()
        binding?.rvFav?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireActivity(),2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater,container,false)
        return binding?.root
    }
    private fun fetchData(){
        val dbUsers = db.getReference("Users")
        val dbWisata = db.getReference("wisata")
        dbUsers.child(auth.currentUser?.uid.toString()).child("Favorites")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,"Error : $error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}