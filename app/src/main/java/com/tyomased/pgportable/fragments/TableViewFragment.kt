package com.tyomased.pgportable.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.tyomased.pgportable.Database
import com.tyomased.pgportable.R
import com.tyomased.pgportable.TableViewListener
import com.tyomased.pgportable.dialogs.MessageType
import com.tyomased.pgportable.dialogs.TextDialog
import com.tyomased.pgportable.viewmodels.TableViewModel
import kotlinx.android.synthetic.main.fragment_table_view.*
import kotlinx.android.synthetic.main.fragment_table_view.view.*
import writeToPDFScopedStorage
import writeToTextFileScopedStorage

class TableViewFragment : Fragment() {
    private lateinit var viewModel: TableViewModel
    private lateinit var tableAdapter: TableViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val inflated = inflater.inflate(R.layout.fragment_table_view, container, false)
        viewModel = TableViewModel(Database.currentTable!!)

        tableAdapter = TableViewAdapter(requireContext(), viewModel)
        val tableViewListener = TableViewListener(requireContext(), viewModel)

        viewModel.run {
            modifiedRows.observe(viewLifecycleOwner,
                Observer { tableAdapter.updateTableContents() })
            deletedRows.observe(viewLifecycleOwner, Observer { tableAdapter.updateTableContents() })

            newRows.observe(viewLifecycleOwner, Observer { tableAdapter.updateTableContents() })
            rows.observe(viewLifecycleOwner, Observer { tableAdapter.updateTableContents() })

            isLoading.observe(viewLifecycleOwner, Observer {
                refreshLayout.isRefreshing = it
                if (!it) tableAdapter.updateTableContents()
            })

            errorMessage.observe(viewLifecycleOwner, Observer {
                it?.let {
                    TextDialog(requireContext(), MessageType.ERROR, it).invoke()
                    viewModel.errorMessage.value = null
                }
            })

            isDiffEmpty.observe(viewLifecycleOwner, Observer {
                inflated.fabUpload.let { fab ->
                    if (it) fab.hide() else fab.show()
                }
            })
        }

        inflated.run {
            tableView.run {
                setAdapter(tableAdapter)
                setTableViewListener(tableViewListener)
            }

            toolbar.setOnClickListener { findNavController().navigateUp() }
            toolbar.setOnMenuItemClickListener { onMenuItemClick(it) }

            fabUpload.setOnClickListener { viewModel.applyPatch() }
            refreshLayout.setOnRefreshListener { onRefresh() }
        }
        return inflated
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onRefresh()
    }

    fun onRefresh() {
        viewModel.fetchTable()
    }

    fun onMenuItemClick(item: MenuItem): Boolean = when (item.itemId) {
        R.id.newRow -> {
            viewModel.newRow()
            true
        }

        R.id.reset -> {
            viewModel.reset()
            true
        }

        R.id.export -> {
            val e = tableAdapter.getExpHelp.joinToString("\n") { row ->
                row.joinToString(",")
            }
            val res = e.replace("'", "")

            writeToTextFileScopedStorage(requireContext(), "exportedTable.csv", res)
            writeToPDFScopedStorage(requireContext(), "exportedTable.pdf", res)
            true
        }

        else -> false
    }
}
