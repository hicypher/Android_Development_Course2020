package com.hicypher.news.adapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.hicypher.news.MainActivity
import com.hicypher.news.R
import com.hicypher.news.webview
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File
import java.io.FileOutputStream
import java.util.*
class RecyclerAdapterItemInfo(internal var datalist: List<ItemInfo>, internal var context: Context) : RecyclerView.Adapter<RecyclerAdapterItemInfo.View_Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): View_Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_item_data_list, parent, false)

        return View_Holder(view)
    }
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "PrefExpiry"
    internal  var sPref : SharedPreferences=context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)


    override fun onBindViewHolder(holder: View_Holder, position: Int) {
        val title = datalist[position].title
        val  author= datalist[position].author
        val company = datalist[position].company
        val image = datalist[position].image
        val body = datalist[position].body
        val description = datalist[position].description
        val link = datalist[position].link
        val time = datalist[position].time
        holder.timeOne.text = time.toString()
        holder.titleOne.text = title
        holder.bodyOne.text = description
        holder.readOne.text= "By: $author"
        if (!image.isNullOrEmpty()) Picasso.get().load(image).fit().into(holder.imageOne)
        holder.readOne.setOnClickListener {
            context.startActivity(Intent(context,webview::class.java).putExtra("link",link).putExtra("isJs",false))
        }
        holder.shareOne.setOnClickListener {

            Picasso.get().load(image)
                .into(object : Target {
                    override fun onBitmapLoaded(
                        bitmap: Bitmap?,
                        from: Picasso.LoadedFrom?
                    ) {
                        val i = Intent(Intent.ACTION_SEND)
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        i.putExtra(
                            Intent.EXTRA_TEXT,
                            """NewsXS: $title
Read complete article at: $link
Download the NewsXS App: https://play.google.com/store/apps/details?id=${context.getPackageName()}
                            """.trimIndent()
                        )
                        i.type = "image/*"
                        i.putExtra(Intent.EXTRA_STREAM,
                            bitmap?.let { it1 ->getLocalBitmapUri(it1) })
                        context.startActivity(Intent.createChooser(i, "Share using"))
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    override fun onBitmapFailed(
                        e: Exception?,
                        errorDrawable: Drawable?
                    ) { e?.printStackTrace()
                    }
                })
        }

    }

    override fun getItemCount(): Int {
        return datalist.size
    }
     inner class View_Holder(view: View) : RecyclerView.ViewHolder(view) {

        internal var titleOne: TextView
        internal var bodyOne: TextView
        internal var timeOne: TextView
        internal var readOne: TextView
        internal var imageOne: ImageView
        internal var shareOne: ImageButton
        internal var item_data_list: LinearLayout
        internal var my_product_datalist_rel: RelativeLayout
        init {
            this.titleOne = view.findViewById<View>(R.id.titleOne) as TextView
            this.bodyOne = view.findViewById<View>(R.id.bodyOne) as TextView
            this.timeOne = view.findViewById<View>(R.id.timeOne) as TextView
            this.readOne = view.findViewById<View>(R.id.readOne) as TextView
            this.imageOne = view.findViewById<View>(R.id.imageOne) as ImageView
            this.shareOne = view.findViewById<View>(R.id.sharebtnone) as ImageButton
            this.item_data_list = view.findViewById<View>(R.id.item_data_list) as LinearLayout
            this.my_product_datalist_rel = view.findViewById<View>(R.id.my_product_datalist_rel) as RelativeLayout

        }
    }

   private fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(
               context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 50, out)
            out.close()

            bmpUri = if (Build.VERSION.SDK_INT < 24) {
                Uri.fromFile(file);
            } else {
                Uri.parse(file.path); // My work-around for new SDKs, doesn't work in Android 10.
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bmpUri
    }
}
