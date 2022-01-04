package com.example.shoppinglist

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.database.SqlHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var data = ArrayList<Data>()
    var recyclerView:RecyclerView?=null
    var btnadd:FloatingActionButton?=null
    var searchView:SearchView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTransparentStatusBar()
        handle()
        getdata()
        btnadd?.setOnClickListener {
            dialog()
            Log.d("twett",R.color.color1.toString()+"5555")
        }

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                var title = newText//คำที่ค้นหา
                if (!title.isNullOrEmpty()){
                    recyclerView!!.adapter = DataAdapter(data.filter { it.title.contains(title)} )
                }else{
                    recyclerView!!.adapter = DataAdapter(data)
                }
                return false
            }
        })

    }

    fun dialog(){
        val btnsheet = layoutInflater.inflate(R.layout.popupaddlist, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(btnsheet)
        var btnclose: ImageView? = dialog.findViewById(R.id.btnclose)
        var btnok:ImageView?=dialog.findViewById(R.id.btnok)
        var edittitle:TextView?=dialog.findViewById(R.id.edittitle)
        var editdetail:TextView?=dialog.findViewById(R.id.editdetail)
        var con:ConstraintLayout?=dialog.findViewById(R.id.consta)

        var priority_indicator1:CardView?=dialog.findViewById(R.id.priority_indicator1)
        var priority_indicator2:CardView?=dialog.findViewById(R.id.priority_indicator2)
        var priority_indicator3:CardView?=dialog.findViewById(R.id.priority_indicator3)
        var priority_indicator4:CardView?=dialog.findViewById(R.id.priority_indicator4)
        var priority_indicator5:CardView?=dialog.findViewById(R.id.priority_indicator5)
        var priority_indicator6:CardView?=dialog.findViewById(R.id.priority_indicator6)

        var color = "1"

        priority_indicator1?.setOnClickListener {
            con?.setBackgroundColor(resources.getColor(R.color.color1))
            color = "1"
        }
        priority_indicator2?.setOnClickListener {
            con?.setBackgroundColor(resources.getColor(R.color.color2))
            color = "2"
        }
        priority_indicator3?.setOnClickListener {
            con?.setBackgroundColor(resources.getColor(R.color.color3))
            color = "3"
        }
        priority_indicator4?.setOnClickListener {
            con?.setBackgroundColor(resources.getColor(R.color.color4))
            color = "4"
        }
        priority_indicator5?.setOnClickListener {
            con?.setBackgroundColor(resources.getColor(R.color.color5))
            color = "5"
        }
        priority_indicator6?.setOnClickListener {
            con?.setBackgroundColor(resources.getColor(R.color.color6))
            color = "6"
        }

        btnclose?.setOnClickListener {
            dialog.dismiss()
        }
        btnok?.setOnClickListener {
            var title = edittitle?.text.toString()
            var detail = editdetail?.text.toString()
            val db = SqlHelper(this)
            db.add_tomylist(title,detail,color)
            getdata()
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("CutPasteId")
    private fun handle(){
        recyclerView = findViewById(R.id.recyclerView)
        btnadd = findViewById(R.id.btnadd)
        searchView=findViewById(R.id.search)
    }

    fun Activity.setTransparentStatusBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;//
        }
    }

    @SuppressLint("Range")
    fun getdata(){
        data.clear()
        val db=SqlHelper(this)
       val datasms =db.getAllmylist()
        while (datasms.moveToNext()){
            val id = datasms.getString(datasms.getColumnIndex("id_mylist"))
            val title = datasms.getString(datasms.getColumnIndex("title"))
            val detail = datasms.getString(datasms.getColumnIndex("detail"))
            val update_at = datasms.getString(datasms.getColumnIndex("update_at"))
            val color = (datasms.getString(datasms.getColumnIndex("color"))).toInt()
            data.add((Data(id,title,detail,update_at,color)))

        }
        recyclerView!!.adapter = DataAdapter(data)
        //swappisition()

    }

    class Data(
        var id: String,
        var title: String,
        var detail: String,
        var date: String,
        var color:Int
    )
    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_mylist,
                parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            holder.txttitle.text=data.title
            holder.txtdetail.text=data.detail
            holder.txtdate.text=data.date
            when(data.color){
                1 ->  holder.constraintLayout.setBackgroundResource(R.drawable.item_background1)
                2 ->  holder.constraintLayout.setBackgroundResource(R.drawable.item_background2)
                3 ->  holder.constraintLayout.setBackgroundResource(R.drawable.item_background3)
                4 ->  holder.constraintLayout.setBackgroundResource(R.drawable.item_background4)
                5 ->  holder.constraintLayout.setBackgroundResource(R.drawable.item_background5)
                6 ->  holder.constraintLayout.setBackgroundResource(R.drawable.item_background6)
                else -> holder.constraintLayout.setBackgroundResource(R.drawable.item_background1)
            }


        }

        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

            var data: Data? = null
            val circularProgressBar:CircularProgressBar= itemView.findViewById(R.id.circularProgressBar)
            val circulNum:TextView=itemView.findViewById(R.id.num)
            val txttitle:TextView=itemView.findViewById(R.id.txttitle)
            val txtdetail:TextView=itemView.findViewById(R.id.txtdetail)
            val txtdate:TextView=itemView.findViewById(R.id.txtdate)
            val option:ImageView=itemView.findViewById(R.id.imgoption)
            val constraintLayout:ConstraintLayout=itemView.findViewById(R.id.con)

        }
    }

    fun swappisition(){
        val  touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(
                p0: RecyclerView,
                p1: RecyclerView.ViewHolder,
                p2: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = p1.adapterPosition
                val targetPosition = p2.adapterPosition
                Collections.swap(data,sourcePosition,targetPosition)
                recyclerView!!.adapter?.notifyItemMoved(sourcePosition,targetPosition)


                return true
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {


            }

        })

        touchHelper?.attachToRecyclerView(recyclerView)
        recyclerView!!.adapter = DataAdapter(data)

    }

}