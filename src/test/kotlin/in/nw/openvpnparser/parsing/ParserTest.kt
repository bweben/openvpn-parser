package `in`.nw.openvpnparser.parsing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ParserTest {
    @Test
    fun `returns null when invalid input is given`() {
        assertThat(Parser().parse("somethininvalid")).isNull()
    }
}