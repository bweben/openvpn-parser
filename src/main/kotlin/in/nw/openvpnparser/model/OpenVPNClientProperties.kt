package `in`.nw.openvpnparser.model

import java.time.LocalDateTime

data class OpenVPNClientProperties(var updated: LocalDateTime?,
                                   var clientEntities: MutableList<OpenVPNClientEntity>)