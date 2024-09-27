package com.jcortes.deco.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("\${app.base-path}")
class ViewController {

    @GetMapping("/", "")
    fun home(model: Model): String {
        model.addAttribute("message", "Welcome to the home page!")
        return "deco-blog/index-two"
    }

    @GetMapping("/images/preview")
    fun previewImages(model: Model): String {
        return "search-images"
    }

    @GetMapping("/scraped-documents/preview")
    fun previewDocuments(model: Model): String {
        return "search-scraped-documents"
    }

}
