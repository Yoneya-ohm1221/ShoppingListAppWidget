package com.example.shoppinglist

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.roundToInt
import android.util.Log
import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import com.mikhaellopez.circularprogressbar.CircularProgressBar


class Bubbles: Service() {

    /**Solution for handle layout flag because that devices whom Build version is
     * greater then Oreo that don't support WindowManager.LayoutParams.TYPE_PHONE
     * in that case we use WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY*/

    var LAYOUT_FLAG: Int = 0

    lateinit var floatingView: View
    lateinit var manager: WindowManager
    lateinit var params: WindowManager.LayoutParams
    var color:String?=null
    var max:String?=null
    var checked:String?=null
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        val extras = intent!!.extras
        color = extras!!["color"] as String?
        max = extras!!["max"] as String?
        checked = extras!!["checked"] as String?
        Log.d("color",color!!)
        this.params = params
        //Specify the view position
        params.gravity =
            Gravity.TOP or Gravity.LEFT //Initially view will be added to top-left corner
        params.x = 0
        params.y = 100

        manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.bb, null)
        var con:ConstraintLayout = floatingView.findViewById(R.id.conbb)
        var imgbb: ImageView = floatingView.findViewById(R.id.imgbb)
        var imgclosebb:ImageView = floatingView.findViewById(R.id.imgclosebb)
        var chartprocess:CircularProgressBar = floatingView.findViewById(R.id.circularProgressBar2)
        var numchart:TextView= floatingView.findViewById(R.id.num2)


        manager.addView(floatingView, params)
        /////imgbb
        numchart.text = "${checked}/${max!!.toFloat().toInt().toString()}"
        set(color!!.toInt(),imgbb,chartprocess, max!!.toFloat(),checked!!.toFloat())
        con.visibility =View.GONE
        imgclosebb.setOnClickListener {
            con.visibility =View.GONE
        }

            floatingView.findViewById<View>(R.id.imgopen)?.setOnTouchListener(object :
                View.OnTouchListener {
                var initialX: Int? = null
                var initialY: Int? = null
                var initialTouchX: Float? = null
                var initialTouchY: Float? = null

                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                    when (motionEvent!!.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = motionEvent!!.getRawX()
                            initialTouchY = motionEvent!!.getRawY()
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                           openApp(motionEvent,initialTouchX!!,initialTouchY!!)
                            return true
                        }

                    }
                    return false
                }
            })

        floatingView.findViewById<View>(R.id.imgbb)?.setOnTouchListener(object :
            View.OnTouchListener {

            var initialX: Int? = null
            var initialY: Int? = null
            var initialTouchX: Float? = null
            var initialTouchY: Float? = null

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                when (motionEvent!!.action) {

                    MotionEvent.ACTION_DOWN -> {
                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y

                        //get the touch location
                        initialTouchX = motionEvent!!.getRawX()
                        initialTouchY = motionEvent!!.getRawY()
                        return true
                    }
                    MotionEvent.ACTION_UP-> {
                        openDetail(motionEvent,initialTouchX!!,initialTouchY!!,con)

                        return false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX!!.plus((motionEvent.getRawX() - initialTouchX!!)).roundToInt()
                        params.y = initialY!!.plus((motionEvent.getRawY() - initialTouchY!!).roundToInt())
                        manager.updateViewLayout(floatingView, params)
                        return true

                    }

                }
                return false
            }
        })



        return START_NOT_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()
        manager.removeView(floatingView)
    }

    fun set(color: Int,imageView: ImageView,circularProgressBar:CircularProgressBar,max:Float,checked:Float){
        when(color){
            1 -> {
                imageView.setImageResource(R.drawable.item_background1_circle)
                chartprocess(max,checked,circularProgressBar,R.color.color1_1)
            }
            2 -> {
                imageView.setImageResource(R.drawable.item_background2_circle)
                chartprocess(max,checked,circularProgressBar,R.color.color2_1)
            }
            3 -> {
                imageView.setImageResource(R.drawable.item_background3_circle)
                chartprocess(max,checked,circularProgressBar,R.color.color3_1)
            }
            4 -> {
                imageView.setImageResource(R.drawable.item_background4_circle)
                chartprocess(max,checked,circularProgressBar,R.color.color4_1)
            }
            5 -> {
                imageView.setImageResource(R.drawable.item_background5_circle)
                chartprocess(max,checked,circularProgressBar,R.color.color5_1)
            }
            6 -> {
                imageView.setImageResource(R.drawable.item_background6_circle)
                chartprocess(max,checked,circularProgressBar,R.color.color6_1)
            }
            else -> imageView.setImageResource(R.drawable.item_background1)
        }
    }

    private fun openApp(event: MotionEvent,initialTouchX:Float,initialTouchY:Float): Boolean {
        val diffPosicaoX = (event.rawX - initialTouchX).toInt()
        val diffPosicaoY = (event.rawY - initialTouchY).toInt()

        val singleClick: Boolean = diffPosicaoX < 5 && diffPosicaoY < 5

        if (singleClick) {
            val intent = Intent(this@Bubbles, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            stopSelf()
        }
        return true
    }

    private fun openDetail(event: MotionEvent,initialTouchX:Float,initialTouchY:Float,con:ConstraintLayout): Boolean {
        val diffPosicaoX = (event.rawX - initialTouchX).toInt()
        val diffPosicaoY = (event.rawY - initialTouchY).toInt()

        val singleClick: Boolean = diffPosicaoX < 5 && diffPosicaoY < 5

        if (singleClick) {
            con.visibility =View.VISIBLE
        }
        return true
    }

    fun chartprocess(pro:Float,checked: Float,chart:CircularProgressBar,color: Int) {

        chart?.apply {
            // Set Progress
            progress = 0f
            // or with animation
            setProgressWithAnimation(checked, 1000) // =1s

            // Set Progress Max
            if (pro != 0f) {
                progressMax = pro
            }


            // Set ProgressBar Color
            progressBarColor = resources.getColor(color)
            // or with gradient


            progressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM


            backgroundProgressBarColor = resources.getColor(R.color.theam)
            // or with gradient
            backgroundProgressBarColorStart = Color.GRAY
            backgroundProgressBarColorEnd = Color.GRAY
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

}