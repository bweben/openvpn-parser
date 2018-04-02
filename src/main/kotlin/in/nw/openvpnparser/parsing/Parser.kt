package `in`.nw.openvpnparser.parsing

import `in`.nw.openvpnparser.model.OpenVPNClientEntity
import `in`.nw.openvpnparser.model.OpenVPNClientProperties
import `in`.nw.openvpnparser.model.OpenVPNRoutingTableEntity
import `in`.nw.openvpnparser.model.OpenVPNStat
import `in`.nw.openvpnparser.util.OpenVPNParseType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Parser {
    companion object {
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EE MMM d HH:mm:ss yyyy", Locale.ENGLISH)
        private val MULTIPLE_SPACE_REGEX = Regex("\\s+")
    }

    fun parse(str: String): OpenVPNStat? {
        val lines = str.split("\n")
        var parseType = OpenVPNParseType.NONE

        val clientPropertiesList = mutableListOf<OpenVPNClientEntity>()
        var updatedDate: LocalDateTime? = null
        val routingTableEntities: MutableList<OpenVPNRoutingTableEntity> = mutableListOf()
        var maxBcastMcastQueueLength = 0

        for (line in lines) {
            when (line.toLowerCase().trim()) {
                "openvpn client list" -> parseType = OpenVPNParseType.CLIENTLIST
                "routing table" -> parseType = OpenVPNParseType.ROUTINGTABLE
                "global stats" -> parseType = OpenVPNParseType.GLOBALSTATS
                "end" -> parseType = OpenVPNParseType.END
                else -> {
                    when (parseType) {
                        OpenVPNParseType.CLIENTLIST -> {
                            val clientUpdated = getClientUpdated(line)
                            if (clientUpdated != null) {
                                updatedDate = clientUpdated
                            }

                            val clientListProperty = getClientListProperty(line)
                            if (clientListProperty != null) {
                                clientPropertiesList.add(clientListProperty)
                            }
                        }
                        OpenVPNParseType.ROUTINGTABLE -> {
                            val routingProperty = getRoutingProperty(line)
                            if (routingProperty != null) {
                                routingTableEntities.add(routingProperty)
                            }
                        }
                        OpenVPNParseType.GLOBALSTATS -> maxBcastMcastQueueLength = getGloablProperty(line)
                        else -> {
                        }
                    }
                }
            }
        }
        if (parseType == OpenVPNParseType.END) {
            return OpenVPNStat(OpenVPNClientProperties(updatedDate, clientPropertiesList), routingTableEntities, maxBcastMcastQueueLength)
        }

        return null
    }

    private fun getGloablProperty(str: String): Int {
        val split = str.split(",")

        if (split[0].toLowerCase() == "max bcast/mcast queue length") {
            return split[1].toInt()
        }

        return -1
    }

    private fun getRoutingProperty(str: String): OpenVPNRoutingTableEntity? {
        val split = str.split(",")

        if (split[0].toLowerCase() != "virtual address") {
            val date = LocalDateTime.parse(split[3], DATE_TIME_FORMATTER)
            return OpenVPNRoutingTableEntity(split[0], split[1], split[2], date)
        }

        return null
    }

    private fun getClientUpdated(str: String): LocalDateTime? {
        val split = str.split(",")

        if (split[0].toLowerCase() == "updated" && split.size == 2) {
            return LocalDateTime.parse(split[1], DATE_TIME_FORMATTER)
        }
        return null
    }

    private fun getClientListProperty(str: String): OpenVPNClientEntity? {
        val split = str.split(",")

        if (split[0].toLowerCase() != "common name" && split.size == 5) {
            val date = LocalDateTime.parse(split[4], DATE_TIME_FORMATTER)
            return OpenVPNClientEntity(split[0], split[1], split[2].toInt(), split[3].toInt(), date)
        }

        return null
    }

    private fun fixDateStringSpaces(originalDateString: String) =
            MULTIPLE_SPACE_REGEX.replace(originalDateString, " ")
}