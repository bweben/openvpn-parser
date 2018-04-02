package `in`.nw.openvpnparser.model

data class OpenVPNStat(var clientStats: OpenVPNClientProperties,
                       var routingTableEntities: List<OpenVPNRoutingTableEntity>,
                       var maxBcastMcastQueueLength: Int)