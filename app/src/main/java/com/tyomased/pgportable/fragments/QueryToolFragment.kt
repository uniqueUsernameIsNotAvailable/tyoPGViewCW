package com.tyomased.pgportable.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.tyomased.pgportable.Database
import com.tyomased.pgportable.R
import com.tyomased.pgportable.databinding.FragmentQuerytoolBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class QueryToolFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentQuerytoolBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_querytool,
            container,
            false
        )

        binding.run {
            lifecycleOwner = this@QueryToolFragment

            toolbar2.setOnClickListener {
                findNavController().navigateUp()
            }

            sendQuery.setOnClickListener {
                var que = queryText.getText().toString();
                Database.query(que)

                // Display the text using Toast (you can use it as per your requirements)
                Toast.makeText(context, "Entered text: " + que, Toast.LENGTH_SHORT).show();

            }
        }



        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QueryToolFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}