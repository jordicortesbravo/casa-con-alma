package com.jcortes.deco.web.templates

import com.jcortes.deco.content.model.SiteCategory

class HeaderTemplate {

    fun print(): String {
        val categoryLinks = SiteCategory.entries.joinToString("") { category ->
            "<a href=\"#\">${category.label}</a>"
        }

        return """
            <header class="header">
                <h1 class="logo-text">Casa con Alma</h1>
                <nav class="nav">
                    <div class="hamburger-menu" id="hamburgerMenu">
                        <i class="fas fa-bars"></i>
                    </div>
                    <div class="nav-menu" id="navMenu">
                        $categoryLinks
                    </div>
                </nav>
            </header>
        """.trimIndent()
    }
}