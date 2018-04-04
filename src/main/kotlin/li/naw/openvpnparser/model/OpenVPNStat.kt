package li.naw.openvpnparser.model

data class OpenVPNStat(var clientStats: OpenVPNClientProperties,
                       var routingTableEntities: List<OpenVPNRoutingTableEntity>,
                       var maxBcastMcastQueueLength: Int)