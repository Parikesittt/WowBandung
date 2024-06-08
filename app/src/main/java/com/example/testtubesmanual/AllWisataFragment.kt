package com.example.testtubesmanual

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.testtubesmanual.adapter.WisataAdapter
import com.example.testtubesmanual.data.listWisata
import com.example.testtubesmanual.databinding.FragmentAllWisataBinding
import com.example.testtubesmanual.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.snapshots

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AllWisataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllWisataFragment : Fragment() {
    private var _binding: FragmentAllWisataBinding?= null
    private val binding get() = _binding
    private lateinit var db: FirebaseDatabase
    private lateinit var destinationList : ArrayList<listWisata>
    private lateinit var adapter: WisataAdapter
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.database
        destinationList = arrayListOf()
        adapter = WisataAdapter(destinationList)
        fetchData()
        binding?.rvAllWisata?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(),2)
        }
        adapter.setOnItemClickCallback(object : WisataAdapter.OnItemClickCallback{
            override fun onItemClicked(data: listWisata) {
                val bundle = Bundle().apply {
                    putString("name", data.namalokasi)
                    putString("desc",data.deskripsi)
                    putString("photoUrl",data.photo)
                    putString("harga",data.harga)
                    putDouble("lat",data.lat)
                    putDouble("lng",data.lng)
                }
                moveToDetail(bundle)
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllWisataBinding.inflate(inflater,container,false)
        return binding?.root
    }

    private fun fetchData(){
        val ref = db.getReference("wisata")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                destinationList.clear()
                if (snapshot.exists()){
                    for (destinationSnap in snapshot.children){
                        val destination = destinationSnap.getValue(listWisata::class.java)
                        if (destination != null) {
                            destinationList.add(destination)
                        }
                    }
                }
                binding?.rvAllWisata?.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun moveToDetail(bundle: Bundle){
        Intent(requireActivity(),DetailActivity::class.java).also {
            it.putExtra(DetailActivity.DATA,bundle)
            startActivity(it)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllWisataFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllWisataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}