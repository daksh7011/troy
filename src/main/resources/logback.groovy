// Do not replace the full qualifier names with imports sometimes IDE removes it because it is somehow unused.

def environment = System.getenv("ENVIRONMENT") ?: "dev"

def defaultLevel = INFO
def defaultTarget = ch.qos.logback.core.joran.spi.ConsoleTarget.SystemErr

if (environment == "dev") {
    defaultLevel = DEBUG
    defaultTarget = ch.qos.logback.core.joran.spi.ConsoleTarget.SystemOut
    // Silence warning about missing native PRNG
    logger("io.ktor.util.random", ERROR)
}

appender("CONSOLE", ch.qos.logback.core.ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%boldGreen(%d{yyyy-MM-dd}) %boldYellow(%d{HH:mm:ss}) %gray(|) %highlight(%5level) %gray(|) %boldMagenta(%40.40logger{40}) %gray(|) %msg%n"
        withJansi = true
    }
    target = defaultTarget
}

root(defaultLevel, ["CONSOLE"])
