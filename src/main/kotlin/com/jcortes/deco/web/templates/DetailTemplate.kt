package com.jcortes.deco.web.templates

import com.jcortes.deco.content.model.ScrapedDocument

class DetailTemplate() : BaseTemplate() {

    override fun getContent(doc: ScrapedDocument): String {
        return """
            
            <div class="container">
                <div class="header">
                    <h1>${doc.title}</h1>
                    <h2>${doc.subtitle}</h2>
                </div>
                ${doc.content}
            </div>
        """.trimIndent()
    }
}