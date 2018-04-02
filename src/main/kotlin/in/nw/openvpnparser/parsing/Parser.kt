package `in`.nw.openvpnparser.parsing

import `in`.nw.openvpnparser.model.*
import `in`.nw.openvpnparser.util.OpenVPNParseType
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class Parser {
    private val pattern = "EE MMM  d HH:mm:ss yyyy"

    fun parse(str: String): OpenVPNStat? {
        var lines = str.split("\n")
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
        var split = str.split(",")
        if (split[0].toLowerCase() == "max bcast/mcast queue length") {
            return split[1].toInt()
        }
        return -1
    }

    private fun getRoutingProperties(str: String, routingTableEntities: MutableList<OpenVPNRoutingTableEntity>): MutableList<OpenVPNRoutingTableEntity> {
        var split = str.split(",")
        if (split[0].toLowerCase() != "virtual address") {

            var date = LocalDateTime.parse(split[3], DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH))
            routingTableEntities.add(OpenVPNRoutingTableEntity(split[0], split[1], split[2], Date.from(date.atZone(ZoneId.systemDefault()).toInstant())));
        }
        return routingTableEntities;
    }

    private fun getClientProperties(str: String, clientProperties: OpenVPNClientProperties): OpenVPNClientProperties {
        var split = str.split(",")
        if (split[0].toLowerCase() == "updated") {
            var date = LocalDateTime.parse(split[1], DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH))
            clientProperties.updated = Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
        } else if (split[0].toLowerCase() != "common name") {
            var date = LocalDateTime.parse(split[4], DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH))
            clientProperties.clientEntities.add(OpenVPNClientEntity(split[0], split[1], split[2].toInt(), split[3].toInt(), Date.from(date.atZone(ZoneId.systemDefault()).toInstant())))
        }
        return clientProperties
    }
}