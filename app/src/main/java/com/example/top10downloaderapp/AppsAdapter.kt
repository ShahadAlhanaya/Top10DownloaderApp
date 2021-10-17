import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.top10downloaderapp.App
import com.example.top10downloaderapp.R
import kotlinx.android.synthetic.main.app_item.view.*

import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.text.method.ScrollingMovementMethod


class AppsAdapter(private val appsList: List<App>, private val context: Context) :
    RecyclerView.Adapter<AppsAdapter.UserViewHolder>() {
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.tv_title
        val priceTextView: TextView = itemView.tv_author
        val appItemLinearLayout: LinearLayout = itemView.ll_appItem
        val appImageView: ImageView = itemView.img_app

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.app_item,
            parent,
            false
        )
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val title = appsList[position].title!!
        val summary = appsList[position].summary!!
        val link = appsList[position].id!!
        val img = appsList[position].imgUrl!!
        val price = appsList[position].price!!
        holder.titleTextView.text = title
        holder.priceTextView.text = price
        Glide.with(context).load(img).into(holder.appImageView)
        holder.appItemLinearLayout.setOnClickListener {
            showBottomSheetDialog(title,summary,img,link)

        }

    }

    override fun getItemCount() = appsList.size

    private fun showBottomSheetDialog(title: String, summary: String, img: String, link: String) {
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(R.layout.app_details_bottom_sheet)
        val sheetTitleTextView = bottomSheetDialog.findViewById<TextView>(R.id.tv_sheetTitle)
        val sheetSummaryTextView = bottomSheetDialog.findViewById<TextView>(R.id.tv_sheetSummary)
        sheetSummaryTextView!!.movementMethod = ScrollingMovementMethod()
        val sheetAppImageView = bottomSheetDialog.findViewById<ImageView>(R.id.sheetImg_app)
        val getButton = bottomSheetDialog.findViewById<Button>(R.id.btn_sheetGet)
        sheetTitleTextView!!.text = title
        sheetSummaryTextView!!.text = summary
        Glide.with(context).load(img).into(sheetAppImageView!!)
        getButton!!.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            context.startActivity(browserIntent)
        }
        bottomSheetDialog.show()
    }
}