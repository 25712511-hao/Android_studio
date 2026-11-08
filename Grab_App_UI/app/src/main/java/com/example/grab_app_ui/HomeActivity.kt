package com.example.grab_app_ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.core.text.scale
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        setupServices()
        setupPromos()
        setupActions()
        setupBottomNav()
    }

    private fun setupServices() {
        val services = listOf(
            ServiceItem("Food", R.drawable.ic_home_food),
            ServiceItem("Mart", R.drawable.ic_home_mark),
            ServiceItem("Express", R.drawable.ic_home_express),
            ServiceItem("Transport", R.drawable.ic_home_transport),
            ServiceItem("Shopping", R.drawable.ic_home_bag),
            ServiceItem("Offers", R.drawable.ic_home_offer),
            ServiceItem("Gift Cards", R.drawable.ic_home_gift),
            ServiceItem("More", R.drawable.ic_home_more)
        )

        findViewById<RecyclerView>(R.id.rvServices).adapter =
            ServiceAdapter(services) { item -> toast(item.name) }
    }

    private fun setupPromos() {
        val promos = listOf(
            PromoItem(
                title = "Order mooncakes to gift & to enjoy",
                date = "Until 21 Sep",
                imageRes = R.drawable.bg_home_banner1,
                overlay = buildSpannedString {
                    bold { append("Fruity, tea-flavoured,\n") }
                    bold { append("alcoholic or ice cream\n") }
                    inSpans(
                        TypefaceSpan("sans-serif-light"),
                        RelativeSizeSpan(0.85f),
                        ForegroundColorSpan(Color.WHITE)
                    ) {
                        append("- we've got them all.")
                    }
                }
            ),
            PromoItem(
                title = "Plus an EXTRA \$20 OFF on groceries",
                date = "Until 31 Aug",
                imageRes = R.drawable.bg_home_banner2,
                badge = "Free gift",
                overlay = buildSpannedString {
                    bold { append("Get a DIY lantern with\n") }
                    scale(0.85f) { append("a min. spend of \$80") }
                }
            )
        )

        findViewById<RecyclerView>(R.id.rvPromos).adapter = PromoAdapter(promos) { item ->
            toast(item.title)
        }
    }

    private fun setupActions() {
        findViewById<View>(R.id.btnScan).setOnClickListener {
            CameraPermissionDialog().show(supportFragmentManager, "camera")
        }

        supportFragmentManager.setFragmentResultListener(
            CameraPermissionDialog.RESULT_KEY, this
        ) { _, result ->
            if (result.getBoolean(CameraPermissionDialog.ALLOWED)) {
                startActivity(Intent(this, QrCodeActivity::class.java))
            }
        }

        findViewById<View>(R.id.tvSwipeToPay).setOnClickListener {
            startActivity(Intent(this, HowToPayActivity::class.java))
        }

        findViewById<View>(R.id.btnActivateGrabPay).setOnClickListener {
            toast("Activate GrabPay")
        }

        findViewById<View>(R.id.btnUsePoints).setOnClickListener {
            toast("Use Points")
        }
    }

    private fun setupBottomNav() {
        findViewById<BottomNavigationView>(R.id.bottomNav)
            .setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_payment -> {
                        startActivity(Intent(this, EnterAmountActivity::class.java))
                        true
                    }
                    else -> true
                }
            }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

data class ServiceItem(val name: String, val iconRes: Int)

data class PromoItem(
    val title: String,
    val date: String,
    val imageRes: Int = 0,
    val badge: String? = null,
    val overlay: CharSequence? = null,
    val decorRes: Int = 0
)

class ServiceAdapter(
    private val items: List<ServiceItem>,
    private val onClick: (ServiceItem) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.ivServiceIcon)
        val name: TextView = view.findViewById(R.id.tvServiceName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return VH(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.iconRes)
        holder.name.text = item.name
        holder.itemView.setOnClickListener { onClick(item) }
    }
}

class PromoAdapter(
    private val items: List<PromoItem>,
    private val onClick: (PromoItem) -> Unit = {}
) : RecyclerView.Adapter<PromoAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivPromo)
        val decor: ImageView = view.findViewById(R.id.ivPromoDecor)
        val overlay: TextView = view.findViewById(R.id.tvPromoOverlay)
        val badge: TextView = view.findViewById(R.id.tvPromoBadge)
        val title: TextView = view.findViewById(R.id.tvPromoTitle)
        val date: TextView = view.findViewById(R.id.tvPromoDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promo, parent, false)
        return VH(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.title.text = item.title
        holder.date.text = item.date

        if (item.imageRes != 0) holder.image.setImageResource(item.imageRes)

        holder.badge.visibility = if (item.badge == null) View.GONE else View.VISIBLE
        holder.badge.text = item.badge

        holder.overlay.visibility = if (item.overlay == null) View.GONE else View.VISIBLE
        holder.overlay.text = item.overlay

        if (item.decorRes != 0) {
            holder.decor.setImageResource(item.decorRes)
            holder.decor.visibility = View.VISIBLE
        } else {
            holder.decor.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }
}