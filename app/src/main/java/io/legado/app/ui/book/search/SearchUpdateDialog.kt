package io.legado.app.ui.book.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import io.legado.app.R
import io.legado.app.base.BaseDialogFragment
import io.legado.app.constant.AppConst
import io.legado.app.databinding.DialogLatestChapterBinding
import io.legado.app.lib.theme.primaryColor
import io.legado.app.model.Download
import io.legado.app.utils.setLayout
import io.legado.app.utils.toastOnUi
import io.legado.app.utils.viewbindingdelegate.viewBinding
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin

class SearchUpdateDialog() : BaseDialogFragment(R.layout.dialog_latest_chapter) {
    constructor(bookInfo: SearchUpdate.BookInfo) : this() {
        arguments = Bundle().apply {
            putString("author", bookInfo.author)
            putString("name", bookInfo.name)
            putString("coverUrl", bookInfo.coverUrl)
            putString("bookUrl", bookInfo.bookUrl)
            putString("latestChapterTitle", bookInfo.latestChapterTitle)
        }
    }

    val binding by viewBinding(DialogLatestChapterBinding::bind)
    override fun onStart() {
        super.onStart()
        setLayout(0.9f, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolBar.setBackgroundColor(primaryColor)
        binding.toolBar.title = "阅读提示"
        val name = arguments?.getString("name")
        val latestChapterTitle = arguments?.getString("latestChapterTitle")
        val bookUrl = arguments?.getString("bookUrl")
        val info = """
            源网站目前更新至${latestChapterTitle}
        """.trimIndent()
        if (name == null) {
            toastOnUi("没有数据")
            dismiss()
            return
        }
        binding.textView.post {
            Markwon.builder(requireContext())
                .usePlugin(GlideImagesPlugin.create(requireContext()))
                .usePlugin(HtmlPlugin.create())
                .usePlugin(TablePlugin.create(requireContext()))
                .build()
                .setMarkdown(binding.textView, info)
        }
        binding.btnJump.setOnClickListener {
            // 当 button 被点击时，跳转到指定页面
            val packageName = requireContext().packageName
            val jumpUrl = "$bookUrl.html?_trace=$packageName"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(jumpUrl))
            startActivity(intent)
        }
    }

}
