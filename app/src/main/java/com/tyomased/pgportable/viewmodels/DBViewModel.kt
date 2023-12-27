package com.tyomased.pgportable.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tyomased.pgportable.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> MutableLiveData<T>.notify(v: T? = null) {
    value = v ?: value
}

class DBViewModel : ViewModel() {
    val dbList = MutableLiveData<MutableList<String>>(mutableListOf())
    var loading = MutableLiveData<Boolean>(false)

    fun fetchDbList() = viewModelScope.launch {
        loading.value = true
        delay(1000)
        val dbs = Database
            .query("SELECT datname FROM pg_database WHERE datname !~* '^template' ORDER BY datname")
            .await()
        dbList.value?.clear()
        dbList.value?.addAll(dbs.toMaps().map { it.get("datname").toString() })
        loading.value = false
        dbList.notify()
    }

    fun removeDB(dbName: String) = viewModelScope.launch {
        Database.update("DROP DATABASE $dbName").await()
        fetchDbList()
    }

    fun addDB(dbName: String) = viewModelScope.launch {
        Database.update("CREATE DATABASE $dbName").await()
        fetchDbList()
    }
}
