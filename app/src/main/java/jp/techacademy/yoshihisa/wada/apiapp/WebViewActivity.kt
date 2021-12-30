package jp.techacademy.yoshihisa.wada.apiapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity: AppCompatActivity() {
    // 一覧画面から登録するときのコールバック（FavoriteFragmentへ通知するメソッド)
    var onClickAddFavorite: ((Shop) -> Unit)? = null
    // 一覧画面から削除するときのコールバック（ApiFragmentへ通知するメソッド)
    var onClickDeleteFavorite: ((Shop) -> Unit)? = null

    var isFavorite:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        isFavorite = FavoriteShop.findBy(data!!.id) != null
        favoriteImageView2.apply {
            setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
            setOnClickListener {
                if (isFavorite) {
                    onClickDeleteFavorite?.invoke(data!!)
                } else {
                    onClickAddFavorite?.invoke(data!!)
                }
            }
        }
        this.apply {
            onClickAddFavorite = {
                FavoriteShop.insert(FavoriteShop().apply {
                    id = data!!.id
                    address = data!!.address
                    name = data!!.name
                    imageUrl = data!!.logoImage
                    url = if (data!!.couponUrls.sp.isNotEmpty()) data!!.couponUrls.sp else data!!.couponUrls.pc
                })
                favoriteImageView2.setImageResource(R.drawable.ic_star)
                isFavorite = FavoriteShop.findBy(data!!.id) != null
            }
            onClickDeleteFavorite = {
                AlertDialog.Builder(this)
                    .setTitle(R.string.delete_favorite_dialog_title)
                    .setMessage(R.string.delete_favorite_dialog_message)
                    .setPositiveButton(android.R.string.ok) {_, _ ->
                        FavoriteShop.delete(it.id)
                        favoriteImageView2.setImageResource(R.drawable.ic_star_border)
                        isFavorite = FavoriteShop.findBy(data!!.id) != null
                    }
                    .setNegativeButton(android.R.string.cancel){ _, _->}
                    .create()
                    .show()
            }
        }
        webView.loadUrl(if (data!!.couponUrls.sp.isNotEmpty()) data!!.couponUrls.sp else data!!.couponUrls.pc)
        //webView.loadUrl(intent.getStringExtra(KEY_URL).toString())
    }

    companion object {
       private var data: Shop? = null
        fun start(activity: Activity, shop: Shop) {
            activity.startActivity(Intent(activity, WebViewActivity::class.java))
            data = shop
        }
    }
}

