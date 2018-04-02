package `in`.nw.openvpnparser.model

import java.util.*

data class OpenVPNRoutingTableEntity(var virtualAddress: String,
                                     var commonName: String,
                                     var realAddress: String,
                                     var lastRef: Date)