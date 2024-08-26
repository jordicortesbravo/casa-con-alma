package com.jcortes.deco.web.templates

import com.jcortes.deco.content.model.ScrapedDocument

class HomeTemplate : BaseTemplate() {

    override fun getContent(doc: ScrapedDocument): String {
        return """
        <div class="container">
                ${doc.content}
            </div>
        """.trimIndent()
    }
}