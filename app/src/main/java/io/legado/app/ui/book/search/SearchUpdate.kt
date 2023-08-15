package io.legado.app.ui.book.search

import android.content.Context
import android.content.Intent
import io.legado.app.data.entities.BookSource
import io.legado.app.model.webBook.WebBook
import io.legado.app.ui.book.read.ReadBookActivity
import io.legado.app.ui.widget.dialog.WaitDialog
import io.legado.app.utils.GSON
import io.legado.app.utils.fromJsonObject
import io.legado.app.utils.showDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object SearchUpdate {
    private val sourceStr: String = """
        {
          "bookSourceComment": "",
          "bookSourceGroup": "",
          "bookSourceName": "起点",
          "bookSourceType": 0,
          "bookSourceUrl": "https://www.qidian.com",
          "customOrder": 0,
          "enabled": true,
          "enabledCookieJar": true,
          "enabledExplore": true,
          "exploreUrl": "",
          "lastUpdateTime": 1691042942398,
          "loginUrl": "",
          "respondTime": 180000,
          "ruleBookInfo": {
            "author": "##作者：</aria>([^<]+)<aria##${'$'}1###",
            "coverUrl": "",
            "intro": "content@text",
            "kind": "##\"book-meta\"[^>]+>([^<]+)</p##${'$'}1###",
            "lastChapter": "##span>连载至([^<]+)##${'$'}1###",
            "name": "##\"book-title\">([^<]+)</h2##${'$'}1###",
            "tocUrl": "<js>\r\nvar id = baseUrl.match(/book\\/(\\d+)/)[1];\r\njava.put('id', id);\r\n'https://druid.if.qidian.com/argus/api/v1/chapterlist/chapterlist?bookId='+id;\r\n</js>",
            "wordCount": "##\"book-meta\"[^>]+>([^<]+)<span##${'$'}1###"
          },
          "ruleContent": {
            "content": "class.read-content@html"
          },
          "ruleExplore": {},
          "ruleReview": {},
          "ruleSearch": {
            "author": "class.author@class.name.0@text||tag.a.2@text||tag.span@text",
            "bookList": "class.book-img-text@tag.li||class.book-list-wrap@class.book-list@tag.li",
            "bookUrl": "##data-bid=\"([^\"]+)\"##https://m.qidian.com/book/${'$'}1###",
            "coverUrl": "##data-bid=\"([^\"]+)\"##https://bookcover.yuewen.com/qdbimg/349573/${'$'}1/180###",
            "intro": "class.intro@textNodes",
            "kind": "class.author@tag.a!0@text||tag.a.0@text",
            "lastChapter": "class.update@a@text##最新更新\\s",
            "name": "tag.h4@a@text||tag.a.1@text",
            "wordCount": "//*[text()=\"总字数\"]//text()##总字数##字"
          },
          "ruleToc": {
            "chapterList": ":\\{.{13}:(\\d+),\"C\":(\\d+).{29}([^\"]*)[^V]*V\":(\\d)[^\\}]*",
            "chapterName": "${'$'}4!${'$'}3@js:result.replace(/0!/, '').replace(/1!/, '💰')",
            "chapterUrl": "https://vipreader.qidian.com/chapter/@get:{id}/${'$'}2",
            "isVip": "",
            "updateTime": "<js>java.timeFormat(${'$'}1)</js>"
          },
          "searchUrl": "/search?kw={{key}}",
          "weight": 0
        }
    """.trimIndent()
    private lateinit var bookSource: BookSource

    init {
        GSON.fromJsonObject<BookSource>(sourceStr).getOrNull()?.let { source ->
            bookSource = source
        }
    }

    data class BookInfo(
        val author: String,
        val name: String,
        val coverUrl: String?,
        val bookUrl: String,
        val latestChapterTitle: String?,
    )

    fun update(context: Context, key: String) {
        WebBook.searchBook(CoroutineScope(Dispatchers.Main), bookSource, key, 1)
            .onSuccess { resultBooks ->
                if (resultBooks.size > 0) {
                    val bookInfo = resultBooks[0]
                    val intent = Intent("com.app.DIALOG_ACTION")
                    intent.putExtra("author", bookInfo.author)
                    intent.putExtra("name", bookInfo.name)
                    intent.putExtra("coverUrl", bookInfo.coverUrl)
                    intent.putExtra("bookUrl", bookInfo.bookUrl)
                    intent.putExtra("latestChapterTitle", bookInfo.latestChapterTitle)
                    context.sendBroadcast(intent)
                }
            }
    }
}