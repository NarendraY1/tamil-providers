package com.narendray1

import android.util.Log
import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.lagradost.cloudstream3.mvvm.safeApiCall
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.jsoup.nodes.Element
import com.lagradost.nicehttp.NiceResponse
import okhttp3.FormBody

class EinthusanProvider : MainAPI() { // all providers must be an instance of MainAPI
    override var mainUrl = "https://einthusan.tv"
    override var name = "Einthusan"
    override val hasMainPage = true
    override var lang = "ta"
    override val hasDownloadSupport = true
    override var sequentialMainPage = true
    override var sequentialMainPageDelay: Long = 100
    override val supportedTypes = setOf(
        TvType.Movie
    )

    override val mainPage = mainPageOf(
        "$mainUrl/movie/results/?find=Recent&lang=tamil" to "Tamil Movies",
        "$mainUrl/movie/results/?find=Recent&lang=hindi" to "Hindi Movies",
        "$mainUrl/movie/results/?find=Recent&lang=telugu" to "Telugu Movies",
        "$mainUrl/movie/results/?find=Recent&lang=malayalam" to "Malayalam Movies",
        "$mainUrl/movie/results/?find=Recent&lang=kannada" to "Kannada Movies",
        "$mainUrl/movie/results/?find=Recent&lang=bengali" to "Bengali Movies",
        "$mainUrl/movie/results/?find=Recent&lang=marathi" to "Marathi Movies",
        "$mainUrl/movie/results/?find=Recent&lang=punjabi" to "Punjabi Movies"
    )

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val document = if (page == 1) {
            app.get(request.data).document
        } else {
            app.get(request.data + "&page=$page").document
        }

        //Log.d("Document", document.toString())
        val home = document.select("#UIMovieSummary > ul > li").mapNotNull {
            it.toSearchResult()
        }

        return HomePageResponse(arrayListOf(HomePageList(request.name, home)), hasNext = true)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("div.block2 > a.title > h3")?.text()?.toString()?.trim() ?: return null
        //Log.d("title", title)
        val href = fixUrl(mainUrl + this.selectFirst("div.block2 > a.title")?.attr("href").toString())
        //Log.d("href", href)
        val posterUrl = fixUrlNull("https:${this.selectFirst("div.block1 > a > img")?.attr("src")}")
        //Log.d("posterUrl", posterUrl.toString())
        return newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = posterUrl
                this.quality = SearchQuality.HD
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val fixedQuery = query.replace(" ", "+")
        val resultTamil = app.get("$mainUrl/movie/results/?lang=tamil&query=$fixedQuery").document.select("#UIMovieSummary > ul > li").mapNotNull {
            it.toSearchResult()
        }
        val resultHindi = app.get("$mainUrl/movie/results/?lang=hindi&query=$fixedQuery").document.select("#UIMovieSummary > ul > li").mapNotNull {
            it.toSearchResult()
        }
        val resultMalayalam = app.get("$mainUrl/movie/results/?lang=malayalam&query=$fixedQuery").document.select("#UIMovieSummary > ul > li").mapNotNull {
            it.toSearchResult()
        }
        val resultTelugu = app.get("$mainUrl/movie/results/?lang=telugu&query=$fixedQuery").document.select("#UIMovieSummary > ul > li").mapNotNull {
            it.toSearchResult()
        }
        val resultKannada = app.get("$mainUrl/movie/results/?lang=kannada&query=$fixedQuery").document.select("#UIMovieSummary > ul > li").mapNotNull {
            it.toSearchResult()
        }
        val resultBengali = app.get("$mainUrl/movie/results/?lang=bengali&query=$fixedQuery").document.select("#UIMovieSummary > ul > li").mapNotNull {
            it.toSearchResult()
        }
        val resultMarathi = app.get("$mainUrl/movie/results/?lang=marathi&query=$fixedQuery").document.select("#UIMovieSummary > ul > li").mapNotNull {
            it.toSearchResult()
        }
        val resultPunjabi = app.get("$mainUrl/movie/results/?lang=punjabi&query=$fixedQuery").document.select("#UIMovieSummary > ul > li").mapNotNull {
            it.toSearchResult()
        }
        val merge = resultTamil + resultHindi + resultMalayalam + resultTelugu + resultKannada + resultBengali + resultMarathi + resultPunjabi

        return merge.sortedBy { -FuzzySearch.partialRatio(it.name.replace("(\\()+(.*)+(\\))".toRegex(), "").lowercase(), query.lowercase()) }
    }

    override suspend fun load(url: String): LoadResponse? {
        val doc = app.get(url).document
        //Log.d("Doc", doc.toString())
        val title = doc.select("#UIMovieSummary > ul > li > div.block2 > a.title > h3").text().toString().trim() ?: return null
        //Log.d("title", title)
        val href = fixUrl(mainUrl + doc.select("#UIMovieSummary > ul > li > div.block2 > a.title").attr("href").toString())
        //Log.d("href", href)
        val poster = fixUrlNull("https:${doc.select("#UIMovieSummary > ul > li > div.block1 > a > img").attr("src")}")
        //Log.d("poster", poster.toString())
        val tags = doc.select("ul.average-rating > li").map { it.select("label").text() }
        val year =
            doc.selectFirst("div.block2 > div.info > p")?.ownText()?.trim()?.toInt()
        //Log.d("year", year.toString())
        val description = doc.selectFirst("p.synopsis")?.text()?.trim()
        val rating = doc.select("ul.average-rating > li > p[data-value]").toString().toRatingInt()
        //Log.d("rating", rating.toString())
        val actors =
            doc.select("div.professionals > div").map {
                ActorData(
                    Actor(
                        it.select("div.prof > p").text().toString(),
                        "https:" + it.select("div.imgwrap img").attr("src").toString()
                    ),
                    roleString = it.select("div.prof > label").text().toString(),
                )
            }
        val mp4link = doc.select("#UIVideoPlayer").attr("data-mp4-link")
        val m3u8link = doc.select("#UIVideoPlayer").attr("data-hls-link")

        return newMovieLoadResponse(title, href, TvType.Movie, "$mp4link,$m3u8link") {
                this.posterUrl = poster?.trim()
                this.year = year
                this.plot = description
                this.tags = tags
                this.rating = rating
                this.actors = actors
            }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val mp4link = data.substringBefore(",")
        val m3u8link = data.substringAfter(",")

        val ipfind = Regex("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b")
        val Fixedmp4link = ipfind.replace(mp4link, "cdn1.einthusan.io")
        val Fixedm3u8link = ipfind.replace(m3u8link, "cdn1.einthusan.io")

        safeApiCall {
            callback.invoke(
                ExtractorLink(
                    "$name-MP4",
                    "$name-MP4",
                    Fixedmp4link,
                    "$mainUrl/",
                    Qualities.Unknown.value,
                    false
                )
            )
            callback.invoke(
                ExtractorLink(
                    "$name-M3U8",
                    "$name-M3U8",
                    Fixedm3u8link,
                    "$mainUrl/",
                    Qualities.Unknown.value,
                    true
                )
            )
        }

        return true
    }
}
