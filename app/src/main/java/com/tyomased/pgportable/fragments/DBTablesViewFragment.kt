package com.tyomased.pgportable.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.tyomased.pgportable.dialogs.ConfirmAction
import com.tyomased.pgportable.Database
import com.tyomased.pgportable.ItemSwipeHelper
import com.tyomased.pgportable.R
import com.tyomased.pgportable.adapters.DBTablesAdapter
import com.tyomased.pgportable.dialogs.CreateTableDialog
import com.tyomased.pgportable.dialogs.MessageType
import com.tyomased.pgportable.dialogs.TextDialog
import com.tyomased.pgportable.viewmodels.DBTablesViewModel
import kotlinx.android.synthetic.main.dbview_item.view.*
import kotlinx.android.synthetic.main.fragment_dbtables_view.view.*

class DBTablesViewFragment : Fragment() {

    private lateinit var adapter: DBTablesAdapter
    lateinit var viewModel: DBTablesViewModel
    private val itemTouchHelperCallback = ItemSwipeHelper {
        val tableName = it.itemView.dbnameText.text.toString()
        val pos = viewModel.tables.value?.indexOf(tableName) ?: -1
        if (pos == -1) return@ItemSwipeHelper
        ConfirmAction(
            requireContext(),
            "Remove table $tableName?",
            {
                viewModel.removeTable(tableName)
            },
            {
                adapter.notifyItemChanged(pos)
            }
        ).invoke()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inflated = inflater.inflate(
            R.layout.fragment_dbtables_view,
            container,
            false
        )

        viewModel = DBTablesViewModel()
        adapter = DBTablesAdapter(
            viewModel,
            onTableItemClick = {
                Database.currentTable = it
                findNavController()
                    .navigate(R.id.action_DBTables_to_TableView)
            }
        )

        requireActivity().run {
            onBackPressedDispatcher.addCallback {
                findNavController()
                    .navigate(R.id.action_DBTables_to_DBConnect)
            }
        }

        inflated.run {
            dbTablesView.adapter = adapter
            ItemTouchHelper(itemTouchHelperCallback)
                .attachToRecyclerView(dbTablesView)

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.fetchTables()
            }

            viewModel.run {
                errorText.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        TextDialog(context, MessageType.ERROR, it).invoke()
                        viewModel.errorText.value = null
                    }
                })
                tables.observe(
                    viewLifecycleOwner,
                    Observer {
                        adapter.submitList(it)
                    }
                )
                isLoading.observe(viewLifecycleOwner, Observer {
                    swipeRefreshLayout.isRefreshing = it
                })
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            addTableFAB.setOnClickListener {
                CreateTableDialog(context) { tableName, columns ->
                    viewModel.createTable(tableName, columns)
                }.invoke()
            }
        }

        return inflated
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchTables()
    }
}
