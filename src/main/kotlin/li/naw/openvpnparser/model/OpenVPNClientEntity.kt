package li.naw.openvpnparser.model

import java.time.LocalDateTime

data class OpenVPNClientEntity(var commonName: String,
                               var realAddress: String,
                               var bytesReceived: Int,
                               var bytesSent: Int,
                               var connectedSince: LocalDateTime)