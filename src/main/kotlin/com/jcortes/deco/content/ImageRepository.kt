package com.jcortes.deco.content

import com.jcortes.deco.content.model.Image
import com.jcortes.deco.util.Pageable

interface ImageRepository {

    fun get(id: Long): Image?
    fun get(sourceId: String): Image?
    fun list(ids: List<Long>): List<Image>
    fun search(searchText: String? = null, keywords: List<String>, pageable: Pageable): List<Image>
    fun listSourceIds(): List<String>
    fun iterate(maxElements: Int = 1000, keyword: String, embedding: List<Float>? = null): Iterator<Image>
    fun save(image: Image)
    fun save(images: List<Image>)
}