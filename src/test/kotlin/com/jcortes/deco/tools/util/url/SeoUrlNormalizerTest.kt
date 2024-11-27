package com.jcortes.deco.tools.util.url

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SeoUrlNormalizerTest {

    @Test
    fun `replace ñ by n`() {
        val url = "baño-elegante-luminoso-bañera-detalles-madera"
        val normalizedUrl = SeoUrlNormalizer.normalize(url)
        assertEquals("bano-elegante-luminoso-banera-detalles-madera", normalizedUrl)
    }

    @Test
    fun `replace special characters by hyphen`() {
        val url = "baño-elegante-luminoso-bañera-detalles-madera"
        val normalizedUrl = SeoUrlNormalizer.normalize(url)
        assertEquals("bano-elegante-luminoso-banera-detalles-madera", normalizedUrl)
    }

    @Test
    fun `replace multiple hyphens by one`() {
        val url = "baño---elegante-luminoso-bañera-detalles-madera"
        val normalizedUrl = SeoUrlNormalizer.normalize(url)
        assertEquals("bano-elegante-luminoso-banera-detalles-madera", normalizedUrl)
    }

    @Test
    fun `remove hyphens at the beginning and end`() {
        val url = "-baño-elegante-luminoso-bañera-detalles-madera-"
        val normalizedUrl = SeoUrlNormalizer.normalize(url)
        assertEquals("bano-elegante-luminoso-banera-detalles-madera", normalizedUrl)
    }

    @Test
    fun `replace special characters by hyphen and lowercase`() {
        val url = "Baño Elegante Luminoso Bañera Detalles Madera"
        val normalizedUrl = SeoUrlNormalizer.normalize(url)
        assertEquals("bano-elegante-luminoso-banera-detalles-madera", normalizedUrl)
    }

    @Test
    fun `replace special characters by hyphen and lowercase with numbers`() {
        val url = "Baño Elegante Luminoso Bañera Detalles Madera 123"
        val normalizedUrl = SeoUrlNormalizer.normalize(url)
        assertEquals("bano-elegante-luminoso-banera-detalles-madera-123", normalizedUrl)
    }

    @Test
    fun `replace accents by vowels without accents`() {
        val url = "bañó èlégänte lumíìïnósò bàñerá detalles madera"
        val normalizedUrl = SeoUrlNormalizer.normalize(url)
        assertEquals("bano-elegante-lumiiinoso-banera-detalles-madera", normalizedUrl)
    }

    @Test
    fun `keep slash`() {
        val url = "baño/elegante/luminoso/bañera/detalles/madera"
        val normalizedUrl = SeoUrlNormalizer.normalize(url)
        assertEquals("bano/elegante/luminoso/banera/detalles/madera", normalizedUrl)
    }
}