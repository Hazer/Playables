package at.florianschuster.playables.core

import org.junit.Test
import org.amshove.kluent.shouldEqual

class CoreTest {

    @Test
    fun testEquals() {
        val test = true
        test shouldEqual true
    }
}
