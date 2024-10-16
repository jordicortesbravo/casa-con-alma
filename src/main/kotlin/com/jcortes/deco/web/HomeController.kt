package com.jcortes.deco.web

import com.jcortes.deco.content.ArticleService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("\${app.base-path}")
class HomeController(
    private val articleService: ArticleService
) {

    @GetMapping("/", "")
    fun home(model: Model): String {
        return "public/index-two"
    }
}
