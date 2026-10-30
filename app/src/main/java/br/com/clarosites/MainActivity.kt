package br.com.clarosites

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.text.Normalizer

class MainActivity : Activity() {
    private data class Site(
        val station: String, val type: String, val streetType: String, val street: String,
        val number: String, val neighborhood: String, val zip: String, val city: String,
        val latitude: Double?, val longitude: Double?, val address: String
    )
    private val sites = mutableListOf<Site>()
    private lateinit var search: EditText
    private lateinit var list: ListView
    private lateinit var count: TextView
    private lateinit var adapter: ArrayAdapter<String>
    private var filtered = listOf<Site>()
    private val green = Color.rgb(0, 145, 75)
    private val labels = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = green
        window.navigationBarColor = Color.BLACK
        loadSites()
        buildUi()
        filter("")
    }

    private fun loadSites() {
        val text = assets.open("sites.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        val arr = JSONArray(text)
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            sites.add(Site(
                o.optString("station"), o.optString("type"), o.optString("streetType"),
                o.optString("street"), o.optString("number"), o.optString("neighborhood"),
                o.optString("zip"), o.optString("city"),
                if (o.isNull("latitude")) null else o.optDouble("latitude"),
                if (o.isNull("longitude")) null else o.optDouble("longitude"),
                o.optString("address")
            ))
        }
        sites.sortBy { it.station.lowercase(Locale.ROOT) }
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(246, 248, 247))
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 22, 20, 18)
            setBackgroundColor(green)
        }
        header.addView(TextView(this).apply {
            text = "ESTAÇÕES CLARO SMARTPLAN W30"
            textSize = 23f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        })
        header.addView(TextView(this).apply {
            text = "Pesquise por código, endereço ou cidade"
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(0, 5, 0, 0)
        })
        root.addView(header)
        search = EditText(this).apply {
            hint = "Código, rua, bairro, CEP ou cidade"
            isSingleLine = true
            textSize = 16f
            setPadding(14, 8, 14, 8)
            setBackgroundColor(Color.WHITE)
        }
        val searchWrap = LinearLayout(this).apply {
            setPadding(14, 12, 14, 4)
            addView(search, LinearLayout.LayoutParams(-1, -2))
        }
        root.addView(searchWrap)
        count = TextView(this).apply {
            setTextColor(Color.DKGRAY); textSize = 13f
            setPadding(18, 6, 18, 8)
        }
        root.addView(count)
        list = ListView(this).apply {
            divider = android.graphics.drawable.ColorDrawable(Color.LTGRAY)
            dividerHeight = 1
        }
        root.addView(list, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)

        adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, labels) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                val s = filtered[position]
                val title = v.findViewById<TextView>(android.R.id.text1)
                val subtitle = v.findViewById<TextView>(android.R.id.text2)
                title.text = s.station + "  •  " + s.type
                title.setTypeface(null, Typeface.BOLD)
                title.setTextColor(Color.rgb(35, 45, 40))
                subtitle.text = s.address
                subtitle.setTextColor(Color.DKGRAY)
                subtitle.maxLines = 2
                v.setPadding(16, 8, 12, 8)
                return v
            }
        }
        list.adapter = adapter
        search.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { filter(s?.toString() ?: "") }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        list.setOnItemClickListener { _, _, position, _ -> showSite(filtered[position]) }
    }

    private fun normalize(value: String): String {
        return Normalizer.normalize(value.lowercase(Locale.ROOT), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .trim()
    }

    private fun filter(query: String) {
        val q = normalize(query)
        filtered = if (q.isEmpty()) {
            sites.take(150)
        } else {
            sites.filter { site ->
                val searchable = listOf(
                    site.station, site.type, site.streetType, site.street, site.number,
                    site.neighborhood, site.zip, site.city, site.address
                ).joinToString(" ")
                normalize(searchable).contains(q)
            }.take(500)
        }
        labels.clear()
        labels.addAll(filtered.map { it.station })
        if (::adapter.isInitialized) adapter.notifyDataSetChanged()
        if (::count.isInitialized) count.text = when {
            q.isEmpty() -> "Mostrando ${filtered.size} estações de ${sites.size}. Pesquise por código, endereço ou cidade."
            filtered.isEmpty() -> "Nenhuma estação encontrada para “$query”. Tente outro termo."
            else -> "${filtered.size} resultado(s) para “$query”" +
                if (filtered.size == 500) " (limite da lista)" else ""
        }
    }

    private fun showSite(s: Site) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 12, 24, 4)
        }
        box.addView(TextView(this).apply {
            text = s.station
            textSize = 23f; typeface = Typeface.DEFAULT_BOLD; setTextColor(green)
        })
        box.addView(TextView(this).apply {
            text = "Tipo de infraestrutura: ${s.type}\n\nEndereço: ${s.address}\n\nCEP: ${s.zip.ifBlank { "Não informado" }}\nCoordenadas: " +
                if (s.latitude != null && s.longitude != null) "${s.latitude}, ${s.longitude}" else "Não informadas"
            textSize = 16f; setTextColor(Color.DKGRAY); setPadding(0, 14, 0, 18)
        })
        val container = FrameLayout(this)
        container.addView(box)
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Detalhes da estação")
            .setView(container)
            .setNegativeButton("FECHAR", null)
            .setPositiveButton("IR PARA O SITE", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(green)
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                openMaps(s); dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun openMaps(s: Site) {
        val destination = if (s.latitude != null && s.longitude != null)
            "${s.latitude},${s.longitude}" else s.address
        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" +
            Uri.encode(destination) + "&travelmode=driving")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}
