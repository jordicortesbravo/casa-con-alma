package com.jcortes.deco.web.templates

import com.jcortes.deco.content.model.ScrapedDocument

abstract class BaseTemplate {

    private val headerTemplate = HeaderTemplate()
    private val footerTemplate = FooterTemplate()

    protected abstract fun getContent(doc: ScrapedDocument): String

    fun print(doc: ScrapedDocument): String {
        return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${doc.title}</title>
            <link rel="stylesheet" href="./static/styles.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
        </head>
        <body>
            ${headerTemplate.print()}
            ${getContent(doc)}
            ${footerTemplate.print()}
            <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
            <script src="./static/detail..js"></script>
        </body>
        </html>
        """.trimIndent()
    }
}