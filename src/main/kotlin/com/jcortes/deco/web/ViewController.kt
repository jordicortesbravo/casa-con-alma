package com.jcortes.deco.web

import com.jcortes.deco.content.ArticleService
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.SiteCategory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("\${app.base-path}")
class ViewController(
    private val articleService: ArticleService
) {

    @GetMapping("/", "")
    fun home(model: Model): String {
        return "public/index-two"
    }
}
