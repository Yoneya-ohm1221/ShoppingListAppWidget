package com.example.shoppinglist

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import android.view.Gravity

import android.graphics.PixelFormat
import android.net.Uri
import android.provider.Settings

import android.view.WindowManager
import android.app.ActivityManager
import android.content.Context
import com.google.android.material.navigation.NavigationView
import android.R.menu





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
        stopService(Intent(this, Bubbles::class.java))
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
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT

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
            val count = (datasms.getString(datasms.getColumnIndex("count"))).toFloat()
            val checked = (datasms.getString(datasms.getColumnIndex("checked"))).toInt()
            data.add((Data(id,title,detail,update_at,color,count,checked)))

        }
        recyclerView!!.adapter = DataAdapter(data)
        //swappisition()

    }

    class Data(
        var id: String,
        var title: String,
        var detail: String,
        var date: String,
        var color:Int,
        var count:Float,
        var checked:Int
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

        @SuppressLint("ResourceAsColor")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            holder.txttitle.text=data.title
            holder.txtdetail.text=data.detail
            holder.txtdate.text=data.date

            holder.circulNum.text = "${data.checked}/${data.count.toInt()}"
            if (data.checked == data.count.toInt() && data.count.toInt() != 0){
                holder.circulNum.setTextColor(Color.BLACK)
            }
            when(data.color){
                1 -> {
                    holder.constraintLayout.setBackgroundResource(R.drawable.item_background1)
                    chartprocess(data.count,data.checked.toFloat(),holder.circularProgressBar,R.color.color1_1)
                }
                2 -> {
                    holder.constraintLayout.setBackgroundResource(R.drawable.item_background2)
                    chartprocess(data.count,data.checked.toFloat(),holder.circularProgressBar,R.color.color2_1)
                }
                3 -> {
                    holder.constraintLayout.setBackgroundResource(R.drawable.item_background3)
                    chartprocess(data.count,data.checked.toFloat(),holder.circularProgressBar,R.color.color3_1)
                }
                4 -> {
                    holder.constraintLayout.setBackgroundResource(R.drawable.item_background4)
                    chartprocess(data.count,data.checked.toFloat(),holder.circularProgressBar,R.color.color4_1)
                }
                5 -> {
                    holder.constraintLayout.setBackgroundResource(R.drawable.item_background5)
                    chartprocess(data.count,data.checked.toFloat(),holder.circularProgressBar,R.color.color5_1)
                }
                6 -> {
                    holder.constraintLayout.setBackgroundResource(R.drawable.item_background6)
                    chartprocess(data.count,data.checked.toFloat(),holder.circularProgressBar,R.color.color6_1)
                }
                else -> holder.constraintLayout.setBackgroundResource(R.drawable.item_background1)
            }

            holder.option.setOnClickListener {
                options(holder.option,data.id,data.title,data.color.toString(),data.count.toString(),data.checked.toString())
            }

            holder.constraintLayout.setOnClickListener {
                nextPage(data.id,data.title,data.color.toString())

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
    fun nextPage(id:String,title:String,color: String){
        val intent = Intent(this, ListActivity::class.java)
        intent.putExtra("idlist", id)
        intent.putExtra("txttitle", title)
        intent.putExtra("color", color)
        startActivity(intent)
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
    fun options(img:ImageView,id:String,title:String,color: String,max: String,checked: String){

        val popup = PopupMenu(this, img)
        if(isMyServiceRunning(Bubbles::class.java)){
            popup.menuInflater.inflate(R.menu.menu_options2, popup.menu)
        }else{
            popup.menuInflater.inflate(R.menu.menu_options, popup.menu)
        }

        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                val i: Int = item.getItemId()
                return when (i) {
                    R.id.menu_delete -> {
                        val db=SqlHelper(this@MainActivity)
                        db.deletelist(id)
                        getdata()
                        true
                    }
                    R.id.menu_golist -> {
                      nextPage(id,title,color.toString())
                        true
                    }
                    R.id.menu_bb -> {
                        bb(color,max,checked,id,title)
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
    fun chartprocess(pro:Float,checked: Float,chart:CircularProgressBar,color: Int) {
        chart?.apply {
            // Set Progress
            progress = 0f
            // or with animation
            setProgressWithAnimation(checked, 1000) // =1s

            // Set Progress Max
            if (pro!=0f){
                progressMax = pro
            }


            // Set ProgressBar Color
            progressBarColor = resources.getColor(color)
            // or with gradient


            progressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM


            backgroundProgressBarColor = resources.getColor(R.color.theam)
            // or with gradient
            backgroundProgressBarColorStart =  Color.GRAY
            backgroundProgressBarColorEnd =  Color.GRAY
            backgroundProgressBarColorDirection =
                CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set Width
            progressBarWidth = 7f // in DP
            backgroundProgressBarWidth = 3f // in DP

            // Other
            roundBorder = true
            startAngle = 180f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
        }
    }
    fun bb(color:String,max:String,checked: String,id: String,title: String) {
        if(checkHasDrawOverlayPermissions()) {
            if(!isMyServiceRunning(Bubbles::class.java)){
              //  startService(Intent(this, Bubbles::class.java))
                val serviceIntent = Intent(this, Bubbles::class.java)
                serviceIntent.putExtra("color", color)
                serviceIntent.putExtra("max", max)
                serviceIntent.putExtra("checked", checked)
                serviceIntent.putExtra("id", id)
                serviceIntent.putExtra("title", title)
                startService(serviceIntent);
                finish()

            }else{
                stopService(Intent(this, Bubbles::class.java))
            }

        }else{
            navigateDrawPermissionSetting()
        }
    }
    private fun checkHasDrawOverlayPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        }else{
            true
        }
    }
    private fun navigateDrawPermissionSetting() {
        val intent1 = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName"))
        startActivityForResult(intent1, 2)
    }
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}