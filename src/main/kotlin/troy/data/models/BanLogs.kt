package troy.data.models

data class BanLogs(
    val bannedUserId: String,
    val bannedUser: String,
    val reason: String,
    val bannedAt: String,
    val banIssuedBy: String
)
