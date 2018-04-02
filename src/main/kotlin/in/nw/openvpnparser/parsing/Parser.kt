package `in`.nw.openvpnparser.parsing

import `in`.nw.openvpnparser.model.OpenVPNClientEntity
import `in`.nw.openvpnparser.model.OpenVPNClientProperties
import `in`.nw.openvpnparser.model.OpenVPNRoutingTableEntity
import `in`.nw.openvpnparser.model.OpenVPNStat
import `in`.nw.openvpnparser.util.OpenVPNParseType
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class Parser {
    companion object {
        private val DATE_TIME_FORMATTER =
                DateTimeFormatter.ofPattern("EE MMM d HH:mm:ss yyyy", Locale.ENGLISH)
    }

    fun parse(str: String): OpenVPNStat? {
        val lines = str.split("\n")
        var parseType = OpenVPNParseType.NONE

        var clientProperties = OpenVPNClientProperties(null, mutableListOf())
        var routingTableEntities: MutableList<OpenVPNRoutingTableEntity> = mutableListOf()
        var maxBcastMcastQueueLength = 0

        for (line in lines) {
            when (line.toLowerCase().trim()) {
                "openvpn client list" -> parseType = OpenVPNParseType.CLIENTLIST
                "routing table" -> parseType = OpenVPNParseType.ROUTINGTABLE
                "global stats" -> parseType = OpenVPNParseType.GLOBALSTATS
                "end" -> parseType = OpenVPNParseType.END
                else -> {
                    when (parseType) {
                        OpenVPNParseType.CLIENTLIST -> clientProperties = getClientProperties(line, clientProperties)
                        OpenVPNParseType.ROUTINGTABLE -> routingTableEntities = getRoutingProperties(line, routingTableEntities)
                        OpenVPNParseType.GLOBALSTATS -> maxBcastMcastQueueLength = getGlobalProperties(line)
                        else -> {
                        }
                    }
                }
            }
        }
        if (parseType == OpenVPNParseType.END) {
            return OpenVPNStat(clientProperties, routingTableEntities, maxBcastMcastQueueLength)
        }

        return null
    }

    private fun getGlobalProperties(str: String): Int {
        val split = str.split(",")

        if (split[0].toLowerCase() == "max bcast/mcast queue length") {
            return split[1].toInt()
        }

        return -1
    }

    private fun getRoutingProperties(str: String, routingTableEntities: MutableList<OpenVPNRoutingTableEntity>): MutableList<OpenVPNRoutingTableEntity> {
        val split = str.split(",")

        if (split[0].toLowerCase() != "virtual address") {
            val date = LocalDateTime.parse(split[3], DATE_TIME_FORMATTER)
            routingTableEntities.add(OpenVPNRoutingTableEntity(split[0], split[1], split[2], Date.from(date.atZone(ZoneId.systemDefault()).toInstant())))
        }

        return routingTableEntities
    }

    private fun getClientProperties(str: String, clientProperties: OpenVPNClientProperties): OpenVPNClientProperties {
        val split = str.split(",")

        if (split[0].toLowerCase() == "updated") {
            val date = LocalDateTime.parse(split[1], DATE_TIME_FORMATTER)
            clientProperties.updated = Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
        } else if (split[0].toLowerCase() != "common name") {
            val date = LocalDateTime.parse(split[4], DATE_TIME_FORMATTER)
            clientProperties.clientEntities.add(OpenVPNClientEntity(split[0], split[1], split[2].toInt(), split[3].toInt(), Date.from(date.atZone(ZoneId.systemDefault()).toInstant())))
        }

        return clientProperties
    }
}