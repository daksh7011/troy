package data

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

object GuildConfig : Table() {
    val guildId = long("guildId")
    val warnMode = integer("warnMode").clientDefault { 1 }
    val maxWarnings = integer("maxWarnings").clientDefault { 3 }
    val isWarningEnabled = bool("isWarningEnabled").clientDefault { true }

    override val primaryKey = PrimaryKey(guildId, name = "primaryKeyGuildId")
}

fun doesGuildExistsInDatabase(guildSnowflake: Long): Boolean {
    SchemaUtils.create(GuildConfig)
    GuildConfig.select { GuildConfig.guildId eq guildSnowflake }.toList().let {
        return it.isNotEmpty()
    }
}

fun Transaction.insertGuildConfig(guildSnowflake: Long): InsertStatement<Number> {
    addLogger(StdOutSqlLogger)
    SchemaUtils.create(GuildConfig)
    return GuildConfig.insert {
        it[guildId] = guildSnowflake
    }
}

sealed class WarnMode {
    object None : WarnMode()
    object Kick : WarnMode()
    object Ban : WarnMode()
}
