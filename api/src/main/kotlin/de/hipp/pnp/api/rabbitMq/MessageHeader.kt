package de.hipp.pnp.api.rabbitMq

import org.springframework.stereotype.Component

@Component
class MessageHeader {
    var externalId: String = ""
    var roles: Array<String> = emptyArray()
}
