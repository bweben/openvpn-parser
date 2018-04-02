package `in`.nw.openvpnparser.model

import java.util.*

data class OpenVPNClientProperties(var updated: Date?,
                                   var clientEntities: MutableList<OpenVPNClientEntity>)