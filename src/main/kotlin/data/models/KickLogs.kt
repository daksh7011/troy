package data.models

data class KickLogs(
    val userId: String,
    val userName: String,
    val reason: String,
    val kickedAt: String,
    val kickedBy: String,
)
