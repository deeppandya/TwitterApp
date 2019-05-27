package com.example.twitterapp.activity

import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.twitterapp.AppBarStateChangeListener
import com.example.twitterapp.R
import com.example.twitterapp.Utils
import com.example.twitterapp.model.TweetModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.hdodenhof.circleimageview.CircleImageView

import java.util.Locale

class TweetDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private var containerView: View? = null

    private var appBarLayout: AppBarLayout? = null
    private var imgUserAvatar: CircleImageView? = null
    private var tvToolbarText: TextView? = null
    private var tvTweetUserHandle: TextView? = null
    private var tvTweetDescription: TextView? = null
    private var tvTweetText: TextView? = null
    private var tvTweetDate: TextView? = null
    private var imgUserBackground: ImageView? = null
    private var space: Space? = null
    private var toolbar: Toolbar? = null

    private var appBarStateChangeListener: AppBarStateChangeListener? = null

    private val mAvatarPoint = FloatArray(2)
    private val mSpacePoint = FloatArray(2)
    private val mToolbarTextPoint = FloatArray(2)
    private val mTitleTextViewPoint = FloatArray(2)
    private var mTitleTextSize: Float = 0.toFloat()
    private var tweetObject: TweetModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet_detail)

        tweetObject = intent.getParcelableExtra(Utils.TWEET)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        findViews()
        setUpViews()
    }

    private fun findViews() {
        containerView = findViewById(R.id.view_container)
        appBarLayout = findViewById(R.id.app_bar)

        imgUserBackground = findViewById(R.id.img_user_background)

        imgUserAvatar = findViewById(R.id.user_avatar)
        tvToolbarText = findViewById(R.id.tv_toolbar_title)
        tvTweetDate = findViewById(R.id.tv_tweet_date)
        tvTweetUserHandle = findViewById(R.id.tv_tweet_user_handle)
        tvTweetDescription = findViewById(R.id.tv_tweet_description)
        tvTweetText = findViewById(R.id.tv_tweet_text)
        space = findViewById(R.id.space)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun setUpViews() {
        mTitleTextSize = tvTweetUserHandle!!.textSize
        tvTweetDate!!.text = tweetObject!!.createdAt
        if (tweetObject!!.user != null) {
            tvTweetUserHandle!!.text = tweetObject!!.user!!.screenName
            tvTweetDescription!!.text = tweetObject!!.user!!.description
            Glide.with(this).load(tweetObject!!.user!!.profileBackgroundImageUrl).error(R.color.colorAccent)
                .into(imgUserBackground!!)
            Glide.with(this).load(tweetObject!!.user!!.profileImageUrl).error(R.mipmap.ic_launcher_round)
                .into(imgUserAvatar!!)
        }
        tvTweetText!!.text = tweetObject!!.text

        setUpToolbar()
        setUpAmazingAvatar()
    }

    private fun setUpToolbar() {
        appBarLayout!!.layoutParams.height = Utils.getDisplayMetrics(this).widthPixels * 9 / 16
        appBarLayout!!.requestLayout()

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpAmazingAvatar() {
        appBarStateChangeListener = object : AppBarStateChangeListener() {

            override fun onStateChanged(
                appBarLayout: AppBarLayout,
                state: State
            ) {
            }

            override fun onOffsetChanged(state: State, offset: Float) {
                translationView(offset)
            }
        }
        appBarLayout!!.addOnOffsetChangedListener(appBarStateChangeListener)
    }

    private fun translationView(offset: Float) {
        val newAvatarSize = Utils.convertDpToPixel(
            EXPAND_AVATAR_SIZE_DP - (EXPAND_AVATAR_SIZE_DP - COLLAPSED_AVATAR_SIZE_DP) * offset,
            this
        )
        val expandAvatarSize = Utils.convertDpToPixel(EXPAND_AVATAR_SIZE_DP, this)
        val xAvatarOffset = (mSpacePoint[0] - mAvatarPoint[0] - (expandAvatarSize - newAvatarSize) / 2f) * offset
        // If avatar center in vertical, just half `(expandAvatarSize - newAvatarSize)`
        val yAvatarOffset = (mSpacePoint[1] - mAvatarPoint[1] - (expandAvatarSize - newAvatarSize)) * offset
        imgUserAvatar!!.layoutParams.width = Math.round(newAvatarSize)
        imgUserAvatar!!.layoutParams.height = Math.round(newAvatarSize)
        imgUserAvatar!!.translationX = xAvatarOffset
        imgUserAvatar!!.translationY = yAvatarOffset

        val newTextSize = mTitleTextSize - (mTitleTextSize - tvToolbarText!!.textSize) * offset
        val paint = Paint(tvTweetUserHandle!!.paint)
        paint.textSize = newTextSize
        val newTextWidth = Utils.getTextWidth(paint, tvTweetUserHandle!!.text.toString())
        paint.textSize = mTitleTextSize
        val originTextWidth = Utils.getTextWidth(paint, tvTweetUserHandle!!.text.toString())
        // If rtl should move title view to end of view.
        val isRTL =
            TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL || containerView!!.layoutDirection == View.LAYOUT_DIRECTION_RTL

        val tempOffset: Float
        tempOffset = if (tvToolbarText!!.width > newTextWidth) {
            (originTextWidth - newTextWidth) / 2
        } else {
            0f
        }

        val tempToolbarWidth: Float
        tempToolbarWidth = if (isRTL) {
            tvToolbarText!!.width.toFloat()
        } else {
            0f
        }

        val tempTweetUserWidth: Float
        tempTweetUserWidth = if (isRTL) {
            tvTweetUserHandle!!.width.toFloat()
        } else {
            0f
        }

        val xTitleOffset =
            (mToolbarTextPoint[0] + tempToolbarWidth - (mTitleTextViewPoint[0] + tempTweetUserWidth) - tempOffset) * offset
        val yTitleOffset = (mToolbarTextPoint[1] - mTitleTextViewPoint[1]) * offset
        tvTweetUserHandle!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize)
        tvTweetUserHandle!!.translationX = xTitleOffset
        tvTweetUserHandle!!.translationY = yTitleOffset
    }

    private fun resetPoints() {
        val offset = appBarStateChangeListener!!.getCurrentOffset()

        val newAvatarSize = Utils.convertDpToPixel(
            EXPAND_AVATAR_SIZE_DP - (EXPAND_AVATAR_SIZE_DP - COLLAPSED_AVATAR_SIZE_DP) * offset,
            this
        )
        val expandAvatarSize = Utils.convertDpToPixel(EXPAND_AVATAR_SIZE_DP, this)

        val avatarPoint = IntArray(2)
        imgUserAvatar!!.getLocationOnScreen(avatarPoint)
        mAvatarPoint[0] = avatarPoint[0].toFloat() - imgUserAvatar!!.translationX -
                (expandAvatarSize - newAvatarSize) / 2f
        // If avatar center in vertical, just half `(expandAvatarSize - newAvatarSize)`
        mAvatarPoint[1] = avatarPoint[1].toFloat() - imgUserAvatar!!.translationY -
                (expandAvatarSize - newAvatarSize)

        val spacePoint = IntArray(2)
        space!!.getLocationOnScreen(spacePoint)
        mSpacePoint[0] = spacePoint[0].toFloat()
        mSpacePoint[1] = spacePoint[1].toFloat()

        val toolbarTextPoint = IntArray(2)
        tvToolbarText!!.getLocationOnScreen(toolbarTextPoint)
        mToolbarTextPoint[0] = toolbarTextPoint[0].toFloat()
        mToolbarTextPoint[1] = toolbarTextPoint[1].toFloat()

        val paint = Paint(tvTweetUserHandle!!.paint)
        val newTextWidth = Utils.getTextWidth(paint, tvTweetUserHandle!!.text.toString())
        paint.textSize = mTitleTextSize
        val originTextWidth = Utils.getTextWidth(paint, tvTweetUserHandle!!.text.toString())
        val titleTextViewPoint = IntArray(2)
        tvTweetUserHandle!!.getLocationOnScreen(titleTextViewPoint)

        val tempOffset: Float
        tempOffset = if (tvToolbarText!!.width > newTextWidth) {
            (originTextWidth - newTextWidth) / 2f
        } else {
            0f
        }

        mTitleTextViewPoint[0] = titleTextViewPoint[0].toFloat() - tvTweetUserHandle!!.translationX - tempOffset
        mTitleTextViewPoint[1] = titleTextViewPoint[1] - tvTweetUserHandle!!.translationY

        Handler().post { translationView(offset) }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            return
        }
        resetPoints()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (tweetObject!!.geo != null && tweetObject!!.geo!!.coordinates != null) {
            val tweetLocation =
                LatLng(tweetObject!!.geo!!.coordinates!![0] as Double, tweetObject!!.geo!!.coordinates!![1] as Double)

            googleMap.addMarker(
                MarkerOptions().position(tweetLocation).title(tweetObject!!.text).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.twitter_icon)
                )
            )
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(tweetLocation, 12.0f)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(tweetLocation))
            googleMap.animateCamera(cameraUpdate)
        }
    }

    companion object {

        private const val EXPAND_AVATAR_SIZE_DP = 80f
        private const val COLLAPSED_AVATAR_SIZE_DP = 32f
    }
}
