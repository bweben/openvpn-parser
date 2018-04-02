package `in`.nw.openvpnparser.model

import java.util.*

data class OpenVPNClientEntity(var commonName: String,
                               var realAddress: String,
                               var bytesReceived: Int,
                               var bytesSent: Int,
                               var ConnectedSince: Date)