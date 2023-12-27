package com.tyomased.pgportable

import androidx.fragment.app.testing.launchFragment
import com.tyomased.pgportable.fragments.DBConnectFragment
import org.junit.Test

class ConnectFragmentUITest {
    @Test
    fun testConnectFragment() {
        with(launchFragment<DBConnectFragment>(null)) {
            onFragment {
                assert(
                    it.binding.connectionTitle.toString()
                        .startsWith("postgres://")
                )
            }
        }
    }
}