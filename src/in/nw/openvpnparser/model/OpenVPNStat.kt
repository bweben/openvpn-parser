package `in`.nw.openvpnparser.model

import java.util.*

data class OpenVPNStat(var clientStats: OpenVPNClientProperties,
                       var routingTableEntities: List<OpenVPNRoutingTableEntity>,
                       var maxBcastMcastQueueLength: Int)