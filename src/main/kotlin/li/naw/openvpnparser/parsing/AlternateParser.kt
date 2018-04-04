package li.naw.openvpnparser.parsing

import li.naw.openvpnparser.model.OpenVPNClientEntity
import li.naw.openvpnparser.model.OpenVPNClientProperties
import li.naw.openvpnparser.model.OpenVPNRoutingTableEntity
import li.naw.openvpnparser.model.OpenVPNStat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AlternateParser {
    companion object {
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EE MMM d HH:mm:ss yyyy", Locale.ENGLISH)

        private val MULTIPLE_SPACE_REGEX = Regex("\\s+")
        private val UPDATED_REGEX = Regex("^Updated,(.*)$")

        private const val ROUTING_TABLE = "ROUTING TABLE\n"
        private const val GLOBAL_STATS = "GLOBAL STATS\n"
        private const val MAX_QLENGTH_GLOBAL_STAT = "Max bcast/mcast queue length"
    }

    fun parse(str: String): OpenVPNStat? {
        try {
            val (clientPropertiesStr, routingTableAndGlobalStats) = str.split(ROUTING_TABLE)
            val (routingTableStr, globalStatsStr) = routingTableAndGlobalStats.split(GLOBAL_STATS)

            val clientProperties = parseClientProperties(clientPropertiesStr)
            val routingTableEntries = parseRoutingTable(routingTableStr)
            val globalStats = parseGlobalStats(globalStatsStr)

            return OpenVPNStat(
                    clientProperties,
                    routingTableEntries,
                    globalStats[MAX_QLENGTH_GLOBAL_STAT]?.toInt()
                            ?: 0 // For interface conformance
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun parseClientProperties(clientPropertiesStr: String): OpenVPNClientProperties {
        val clientPropertiesLines = clientPropertiesStr.split("\n")
        if (clientPropertiesLines.size < 3) {
            throw ParseException("Client properties are not parseable (less than three lines)")
        }

        val updatedLine = clientPropertiesLines[1]
        val updatedMatch = UPDATED_REGEX.matchEntire(updatedLine)
                ?: throw ParseException("Updated value could not be extracted")

        val updated = LocalDateTime.parse(fixDateStringSpaces(updatedMatch.groupValues[1]), DATE_TIME_FORMATTER)
        if (clientPropertiesLines.size < 4) {
            return OpenVPNClientProperties(updated, mutableListOf())
        }

        val clientEntities = clientPropertiesLines.asSequence()
                .drop(3)
                .map { it.split(",") }
                .filter { it.size == 5 }
                .map {
                    OpenVPNClientEntity(
                            it[0],
                            it[1],
                            it[2].toInt(),
                            it[3].toInt(),
                            parseDate(it[4])
                    )
                }.toMutableList()

        return OpenVPNClientProperties(updated, clientEntities)
    }

    private fun parseRoutingTable(routingTableStr: String): List<OpenVPNRoutingTableEntity> {
        return routingTableStr.split("\n").asSequence()
                .drop(1)
                .map { it.split(",") }
                .filter { it.size == 4 }
                .map {
                    OpenVPNRoutingTableEntity(
                            it[0],
                            it[1],
                            it[2],
                            parseDate(it[3])
                    )
                }.toList()
    }

    private fun parseGlobalStats(globalStatsStr: String): Map<String, String> {
        return globalStatsStr.split("\n").asSequence()
                .filter { it.isNotBlank() }
                .map { it.split(",") }
                .filter { it.size == 2 }
                .map { it[0] to it[1] }
                .toMap()
    }

    private fun parseDate(dateString: String) =
            LocalDateTime.parse(fixDateStringSpaces(dateString), DATE_TIME_FORMATTER)

    private fun fixDateStringSpaces(originalDateString: String) =
            MULTIPLE_SPACE_REGEX.replace(originalDateString, " ")


    class ParseException(message: String) : Exception(message)
}