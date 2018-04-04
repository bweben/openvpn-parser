package li.naw.openvpnparser.parsing

import li.naw.openvpnparser.model.OpenVPNClientEntity
import li.naw.openvpnparser.model.OpenVPNRoutingTableEntity
import li.naw.openvpnparser.model.OpenVPNStat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class ParserTest {
    private var openVPNStat: OpenVPNStat? = null

    @BeforeEach
    fun setUpDataBefore() {
        val strToParse = """OpenVPN CLIENT LIST
Updated,Mon Apr  2 15:53:10 2018
Common Name,Real Address,Bytes Received,Bytes Sent,Connected Since
PegNu VPN LP-2 Client,194.230.159.153:60196,8633,6066,Mon Apr  2 15:52:59 2018
ROUTING TABLE
Virtual Address,Common Name,Real Address,Last Ref
2a02:168:a807:babe::1000,PegNu VPN LP-2 Client,194.230.159.153:60196,Mon Apr  2 15:53:02 2018
10.99.98.2,PegNu VPN LP-2 Client,194.230.159.153:60196,Mon Apr  2 15:53:08 2018
GLOBAL STATS
Max bcast/mcast queue length,1
END"""
        openVPNStat = Parser().parse(strToParse)
    }

    @Test
    fun `returns null when invalid input is given`() {
        assertThat(Parser().parse("somethininvalid"))
                .isNull()
    }

    @Test
    fun `returns notNull when valid input is given`() {
        assertThat(openVPNStat)
                .isNotNull()
    }

    @Test
    fun `returns updatedTime when valid input is given`() {
        assertThat(openVPNStat?.clientStats?.updated)
                .isEqualTo(LocalDateTime.of(2018, 4, 2, 15, 53, 10))
    }

    @Test
    fun `returns clientEntities when valid input is given`() {
        assertThat(openVPNStat?.clientStats?.clientEntities).isEqualTo(listOf(
                OpenVPNClientEntity(
                        "PegNu VPN LP-2 Client",
                        "194.230.159.153:60196",
                        8633,
                        6066,
                        LocalDateTime.of(2018, 4, 2, 15, 52, 59)
                )
        ))
    }

    @Test
    fun `returns routingTableEntities when valid input is given`() {
        assertThat(openVPNStat?.routingTableEntities).isEqualTo(listOf(
                OpenVPNRoutingTableEntity(
                        "2a02:168:a807:babe::1000",
                        "PegNu VPN LP-2 Client",
                        "194.230.159.153:60196",
                        LocalDateTime.of(2018, 4, 2, 15, 53, 2)
                ),
                OpenVPNRoutingTableEntity(
                        "10.99.98.2",
                        "PegNu VPN LP-2 Client",
                        "194.230.159.153:60196",
                        LocalDateTime.of(2018, 4, 2, 15, 53, 8)
                )
        ))
    }

    @Test
    fun `returns maxBcastMcastQueueLength when valid input is given`() {
        assertThat(openVPNStat?.maxBcastMcastQueueLength).isEqualTo(1)
    }
}