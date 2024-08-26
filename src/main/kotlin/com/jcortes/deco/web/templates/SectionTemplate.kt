package com.jcortes.deco.web.templates

import com.jcortes.deco.content.model.ScrapedDocument

class SectionTemplate: BaseTemplate() {

    override fun getContent(doc: ScrapedDocument): String {
        return """
        <div class="container">
                ${doc.content}
            </div>
        """.trimIndent()
    }
}