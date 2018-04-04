package li.naw.openvpnparser.model

import java.time.LocalDateTime

data class OpenVPNClientProperties(var updated: LocalDateTime?,
                                   var clientEntities: List<OpenVPNClientEntity>)