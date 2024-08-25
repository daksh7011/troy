package troy.data.models

data class WarningLogs(
    val warnedUserId: String,
    val warnedUser: String,
    val reason: String,
    val warnedAt: String,
    val warningIssuedBy: String
)
