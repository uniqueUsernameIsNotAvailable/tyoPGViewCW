package com.tyomased.pgportable

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tyomased.pgportable.fragments.Cell
import com.tyomased.pgportable.fragments.ColumnHeader
import com.tyomased.pgportable.viewmodels.TableViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SQLGenTest {
    val headerMockData = (0..4).map { ColumnHeader("H:$it") }

    // 5x5 mock table
    val cellMockData = (0..4).map { row ->
        headerMockData.foldIndexed(
            mutableMapOf<String, Cell>(),
            { idx, acc, h ->
                acc.set(h.data, Cell("$row:$idx"))
                acc
            })
    }

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    val TABLE_NAME = "test"
    val vm = TableViewModel(TABLE_NAME)

    @Before
    fun onBefore() {
        vm.header.value = headerMockData.map { it.data }
        vm.rows.value = cellMockData
        vm.primaryKeys.value = listOf("H:2", "H:4")
    }

    @Test
    fun testSQLGenInsert() {
        val username = "kraftwerk28"
        vm.newRow()
        vm.modifyCell(0, 2, Cell(username))
        val expected = "INSERT INTO $TABLE_NAME " +
                "(H:0, H:1, H:2, H:3, H:4) " +
                "VALUES (null, null, '$username', null, null)"
        assertEquals(vm.produceInsertQuery(), expected)
    }

    @Test
    fun testSQLGenDelete() {
        val row = 4
        vm.deleteRow(row)
        val expected = "DELETE FROM $TABLE_NAME " +
                "WHERE H:2 IN ('$row:2') AND H:4 IN ('$row:4')"
        assertEquals(vm.produceDeleteQuery(), expected)
    }

    @Test
    fun testSQLGenUpdate() {
        val row = 2
        val col = 2
        val username = "kraftwerk28"
        vm.modifyCell(row, col, Cell(username))
        val expected = listOf(
            "UPDATE $TABLE_NAME SET H:2 = '$username' " +
                    "WHERE H:2 = '$row:2' AND H:4 = '$row:4'"
        )
        assertEquals(vm.produceUpdateQueries(), expected)
    }
}