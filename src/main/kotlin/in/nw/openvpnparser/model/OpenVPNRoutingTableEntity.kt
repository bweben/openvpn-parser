package `in`.nw.openvpnparser.model

import java.time.LocalDateTime

data class OpenVPNRoutingTableEntity(var virtualAddress: String,
                                     var commonName: String,
                                     var realAddress: String,
                                     var lastRef: LocalDateTime)