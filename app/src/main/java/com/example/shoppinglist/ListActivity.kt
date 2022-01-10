package com.example.shoppinglist

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.database.SqlHelper

class ListActivity : AppCompatActivity() {
    var data = ArrayList<Data>()
    var recyclerView:RecyclerView?=null
    var backgound:ConstraintLayout?=null
    var btnadd: Button?=null
    var back:ImageView?=null
    var con:ConstraintLayout?=null
    var txttitle:TextView?=null
    var imgclose:ImageView?=null
    var imgadd:ImageView?=null
    var edittitle:EditText?=null
    var idlist=""


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        recyclerView = findViewById(R.id.recyclerView2)
        btnadd = findViewById(R.id.btnaddlist)
        back=findViewById(R.id.imageView)
        con = findViewById(R.id.conadd)
        txttitle = findViewById(R.id.txttitlelist)
        imgadd = findViewById(R.id.imgadd)
        imgclose = findViewById(R.id.imgclose)
        edittitle = findViewById(R.id.edittitlelist)
        backgound = findViewById(R.id.bg)


        idlist = intent.getStringExtra("idlist").toString()
        txttitle?.text = intent.getStringExtra("txttitle").toString()
        val color = intent.getStringExtra("color").toString()
        var colorset=0
        Log.d("sfdsf",color)
        con?.visibility = View.GONE
        when(color.toInt()){
            1 ->  colorset=(R.color.color1)
            2 ->  colorset=(R.color.color2)
            3 ->  colorset=(R.color.color3)
            4 ->  colorset=(R.color.color4)
            5 ->  colorset=(R.color.color5)
            6 ->  colorset=(R.color.color6)
            else -> colorset=(R.color.color1)
        }
        backgound?.setBackgroundColor(resources.getColor(colorset))
        setTransparentStatusBar(colorset)
        getdata(idlist)
        btnadd?.setOnClickListener {
            con?.visibility = View.VISIBLE
            btnadd?.visibility = View.GONE
            edittitle?.requestFocus()
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edittitle, InputMethodManager.SHOW_IMPLICIT)
        }
        imgclose?.setOnClickListener {
            con?.visibility = View.GONE
            btnadd?.visibility = View.VISIBLE
            edittitle?.setText("")
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
        imgadd?.setOnClickListener {
            val db = SqlHelper(this)
            db.add_todetail(edittitle?.text.toString(),idlist)
            getdata(idlist)
            con?.visibility = View.GONE
            btnadd?.visibility = View.VISIBLE
            edittitle?.setText("")
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        back?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun Activity.setTransparentStatusBar(colorset:Int) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor =  resources.getColor(colorset)



    }
    @SuppressLint("Range")
    fun getdata(idlist:String){
        data.clear()
        val db= SqlHelper(this)
        val datasms =db.getAlldetail(idlist)
        while (datasms.moveToNext()){
            val id = datasms.getString(datasms.getColumnIndex("id_detail"))
            val title = datasms.getString(datasms.getColumnIndex("title"))
            val status = datasms.getString(datasms.getColumnIndex("status"))
            data.add((Data(id, title,status)))
            Log.d("dsfsf",title+id+idlist)
        }
        recyclerView!!.adapter = DataAdapter(data)

    }

    class Data(
        var id: String,
        var title: String,
        var status:String
        )

    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_listdetail,
                parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            holder.txtdetail.text=data.title
            if(data.status=="1"){
                holder.ck.isChecked =true
            }
            holder.ck.setOnCheckedChangeListener { compoundButton, b ->
                 updateCheckBox(data.id,b)
            }
            holder.imgoptionlist.setOnClickListener {
                options(holder.imgoptionlist,data.id)
            }


        }

        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

            var data: Data? = null
            val txtdetail:TextView = itemView.findViewById(R.id.txttitledetail)
            val ck:CheckBox = itemView.findViewById(R.id.checkBox)
            val imgoptionlist:ImageView = itemView.findViewById(R.id.imgoptionlist)

        }
    }
    fun updateCheckBox(id:String,boolean: Boolean){
        val db= SqlHelper(this)
        if(boolean){
            db.updateCheckbok(id,"1")
        }else{
            db.updateCheckbok(id,"2")
        }
    }
    fun options(img:ImageView,id:String){
        val popup = PopupMenu(this, img)
        popup.menuInflater.inflate(R.menu.delete_options, popup.menu)
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                val i: Int = item.getItemId()
                return when (i) {
                    R.id.menu_delete -> {
                        val db = SqlHelper(this@ListActivity)
                        db.deletedetail(id)
                        getdata(idlist)
                        true
                    }
                    else -> {
                        onMenuItemClick(item)
                    }
                }
            }
        })

        popup.show()
    }
}