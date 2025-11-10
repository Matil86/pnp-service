package de.hipp.pnp.base.converter.persistent

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import org.apache.logging.log4j.util.Strings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

abstract class BaseConverter<Y> : AttributeConverter<Y, String> {
    private val objectMapper = ObjectMapper()
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    protected var type: Y? = null

    override fun convertToDatabaseColumn(customerInfo: Y?): String? {
        var customerInfoJson: String? = null
        try {
            customerInfoJson = objectMapper.writeValueAsString(customerInfo)
        } catch (e: JsonProcessingException) {
            logger.error("JSON writing error", e)
        }
        return customerInfoJson
    }

    override fun convertToEntityAttribute(customerInfoJSON: String?): Y? {
        var valueInfo: Y? = null
        if (Strings.isBlank(customerInfoJSON)) {
            return null
        }
        try {
            type?.let {
                valueInfo =
                    objectMapper.readValue(
                        customerInfoJSON,
                        objectMapper.typeFactory.constructType(it::class.java),
                    )
            }
        } catch (e: IOException) {
            logger.error("JSON reading error", e)
        }
        return valueInfo
    }

    fun setType(type: Y): BaseConverter<Y> {
        this.type = type
        return this
    }
}
