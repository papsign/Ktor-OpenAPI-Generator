package com.papsign.ktor.openapigen.route

import com.papsign.ktor.openapigen.APITag
import com.papsign.ktor.openapigen.modules.providers.TagProviderModule

class TagModule(override val tags: Collection<APITag>): TagProviderModule

fun tags(vararg tags: APITag) = TagModule(tags.asList())
