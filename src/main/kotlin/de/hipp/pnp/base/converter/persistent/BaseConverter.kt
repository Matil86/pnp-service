package de.hipp.pnp.base.converter.persistent

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import org.apache.logging.log4j.util.Strings
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.IOException

abstract class BaseConverter<Y> : AttributeConverter<Y, String> {

    private val objectMapper = ObjectMapper()
    protected val logger = KotlinLogging.logger {}

    protected var type: Y? = null

    override fun convertToDatabaseColumn(customerInfo: Y?): String? {
        var customerInfoJson: String? = null
        try {
            customerInfoJson = objectMapper.writeValueAsString(customerInfo)
        } catch (e: JsonProcessingException) {
            logger.error(e) { "JSON writing error" }
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
                valueInfo = objectMapper.readValue(
                    customerInfoJSON,
                    objectMapper.typeFactory.constructType(it::class.java)
                )
            }
        } catch (e: IOException) {
            logger.error(e) { "JSON reading error" }
        }
        return valueInfo
    }

    fun setType(type: Y): BaseConverter<Y> {
        this.type = type
        return this
    }
}