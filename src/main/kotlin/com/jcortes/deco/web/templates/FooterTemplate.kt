package com.jcortes.deco.web.templates

class FooterTemplate {

    fun print(): String {
        return """
        <footer class="footer">
            <p>&copy; 2024 Casa con alma. Todos los derechos reservados.</p>
        </footer>
        """.trimIndent()
    }
}