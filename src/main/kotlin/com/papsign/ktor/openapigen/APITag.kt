package com.papsign.ktor.openapigen

import com.papsign.ktor.openapigen.model.info.ExternalDocumentationModel
import com.papsign.ktor.openapigen.model.info.TagModel


/**
 * This interface is used to define tags to classify endpoints.
 * It needs to be implemented using an enum so that the processor properly detects equality.
 *
 * This is assigned to a service using [com.papsign.ktor.openapigen.route.tag].
 *
 * Implementation example:
 *
 *      enum class Tags(override val description: String) : APITag {
 *          EXAMPLE("Wow this is a tag?!")
 *      }
 *
 * @see [com.papsign.ktor.openapigen.route.tag]
 */
interface APITag {
    val name: String
    val description: String
    val externalDocs: ExternalDocumentationModel?
            get() = null

    fun toTag(): TagModel {
        return TagModel(name, description, externalDocs)
    }
}
