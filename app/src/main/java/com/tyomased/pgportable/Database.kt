package com.tyomased.pgportable

import android.util.Log
import kotlinx.coroutines.*
import java.sql.Connection
import org.postgresql.Driver
import java.sql.*

fun ResultSet.toMaps(): List<Map<String, Any?>> {
    val res = mutableListOf<Map<String, Any?>>()
    while (next()) {
        res.add(
            (1..metaData.columnCount).fold(mutableMapOf())
            { acc, i ->
                acc.set(metaData.getColumnName(i), getObject(i))
                acc
            }
        )
    }
    return res
}

fun ResultSet.toLists(): List<List<Any?>> {
    val res = mutableListOf<List<Any?>>()
    while (next()) {
        res.add(
            (1..metaData.columnCount).fold(mutableListOf())
            { acc, i ->
                acc.add(getObject(i))
                acc
            }
        )
    }
    return res
}

fun ResultSet.succeed(): Boolean = next()

class Database {

    data class Credentials(
        val host: String,
        val port: String,
        val username: String?,
        val password: String?,
        val dbName: String
    ) {
        companion object {
            fun from(data: Map<String, String>) = data.run {
                Credentials(
                    (get("host") ?: "localhost").toString(),
                    (get("port") ?: "5432").toString(),
                    (get("username") ?: "pg-user").toString(),
                    (get("password") ?: "123").toString(),
                    (get("dbName") ?: "postgres").toString()
                )
            }

            fun from(data: List<String>) = data.run {
                Credentials(get(0), get(1), get(2), get(3), get(4))
            }
        }
    }

    var connection: Connection? = null

    init {
        val driver = Driver()
        DriverManager.registerDriver(driver)
        Log.i("DatabaseMgr", "Driver registered")
    }

    companion object {
        var currentTable: String? = null
        var credentials: Credentials? = null
        private fun prepareQueryList(query: String, l: List<Any> = listOf()) =
            l.foldIndexed(query)
            { i, acc, c -> acc.replace("$${i + 1}", c.toString()) }

        private fun prepareQueryList(
            query: String,
            map: Map<String, Any> = mutableMapOf()
        ) =
            map.entries.fold(query)
            { acc, c -> acc.replace("$${c.key}", c.value.toString()) }

        fun connect(): Deferred<Connection?> =
            GlobalScope.async(Dispatchers.IO) {
                instance.connection?.run { if (!isClosed) close() }
                val creds = this@Companion.credentials

                if (creds == null) throw SQLException("Empty credentials.")
                val connString = creds.run { "jdbc:postgresql://${host}:${port}/${dbName}" }
                try {
                    val connection = DriverManager.getConnection(
                        connString, creds.username, creds.password
                    )
                    instance.connection = connection
                    connection

                } catch (e: SQLException) {
                    null
                }
            }

        fun query(statement: String): Deferred<ResultSet> =
            GlobalScope.async(Dispatchers.IO) {
                instance.connection!!
                    .createStatement()
                    .executeQuery(statement)
            }

        fun update(statement: String): Deferred<Int> =
            GlobalScope.async(Dispatchers.IO) {
                instance.connection!!
                    .createStatement()
                    .executeUpdate(statement)
            }

        fun query(statement: String, values: List<Any>): Deferred<ResultSet> {
            val prepared = prepareQueryList(statement, values)
            return GlobalScope.async {
                instance.connection!!
                    .createStatement()
                    .executeQuery(prepared)
            }
        }

        fun query(
            statement: String,
            values: Map<String, Any>
        ): Deferred<ResultSet> {
            val prepared = prepareQueryList(statement, values)
            return GlobalScope.async {
                instance.connection!!.createStatement().executeQuery(prepared)
            }
        }

        fun disconnect() {
            instance.connection!!.close()
        }

        val instance = Database()
    }
}
