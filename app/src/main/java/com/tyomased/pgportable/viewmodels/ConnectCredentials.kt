package com.tyomased.pgportable.viewmodels

import androidx.lifecycle.*

class ConnectCredentials(
    _host: String,
    _port: String,
    _username: String,
    _password: String,
    _dbName: String
) : ViewModel() {

    val host = MutableLiveData<String>(_host)
    val port = MutableLiveData<String>(_port)
    val username = MutableLiveData<String>(_username)
    val password = MutableLiveData<String>(_password)
    val dbName = MutableLiveData<String>(_dbName)
    val connString = MediatorLiveData<String>()

    init {
        val cb = Observer<Any> {
            connString.value =
                "postgresql://${host.value}:${port.value}/${dbName.value}"
        }
        connString.run {
            addSource(host, cb)
            addSource(port, cb)
            addSource(dbName, cb)
        }
    }
}
