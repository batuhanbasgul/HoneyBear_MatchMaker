package com.honey_bear.honeybear_matchmaker.view.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.data.model.User
import com.honey_bear.honeybear_matchmaker.data.model.UserAndPercent
import com.honey_bear.honeybear_matchmaker.utils.AppUtils
import com.honey_bear.honeybear_matchmaker.view_model.UserViewModel

class MatchesAdapter(
        private val currentUserId:String,
        private val mContext:Context,
        private val userList:ArrayList<UserAndPercent>,
        private val likedUserList:ArrayList<User>
        ) : RecyclerView.Adapter<MatchesAdapter.CardViewHolder>() {

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var cardViewUser: CardView = view.findViewById(R.id.cardViewUser)
        var imageViewCardView:ImageView = view.findViewById(R.id.imageViewCardView)
        var imageViewLike:ImageView = view.findViewById(R.id.imageViewLike)
        var textViewPercent:TextView = view.findViewById(R.id.textViewPercent)
        fun bindUI(user: UserAndPercent) {
            textViewPercent.text = user.percent.toString()
            if(user.user.userImageUrl != "no_image"){
                Glide.with(imageViewCardView).load(user.user.userImageUrl).placeholder(R.drawable.ic_profile_64).into(imageViewCardView)
            }
            if(isCurrentUserLikedThisUser(user.user)){
                imageViewLike.setImageResource(R.drawable.ic_like_red_64)
            }

            imageViewLike.setOnClickListener{
                if(!isCurrentUserLikedThisUser(user.user)){
                    val animBounce = AnimationUtils.loadAnimation(it.context, R.anim.anim_bounce)
                    imageViewLike.startAnimation(animBounce)
                    imageViewLike.setImageResource(R.drawable.ic_like_red_64)

                    user.user.userID?.let{ userID->
                        UserViewModel().addLikedUser(currentUserId,userID)
                    }
                }else{
                    val animBounce = AnimationUtils.loadAnimation(it.context, R.anim.anim_bounce)
                    imageViewLike.startAnimation(animBounce)
                    imageViewLike.setImageResource(R.drawable.ic_like_white_64)
                    user.user.userID?.let{ userID->
                        UserViewModel().removeLikedUser(currentUserId,userID)
                    }
                }
            }

            imageViewCardView.setOnClickListener {
                showAlertDialog(user.user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.card_view,null)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindUI(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    @SuppressLint("InflateParams")
    private fun showAlertDialog(user: User) {
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        val view:View = LayoutInflater.from(mContext).inflate(R.layout.alertview_design, null)
        val imageViewAlertProfile: ImageView = view.findViewById(R.id.imageViewAlertProfile)
        val imageViewAlertLike: ImageView = view.findViewById(R.id.imageViewAlertLike)
        val textViewAlertName: TextView = view.findViewById(R.id.textViewAlertName)
        val textViewAlertCity: TextView = view.findViewById(R.id.textViewAlertCity)
        val textViewAlertAge: TextView = view.findViewById(R.id.textViewAlertAge)
        alertDialogBuilder.setView(view)

        if(user.userImageUrl != "no_image"){
            Glide.with(imageViewAlertProfile).load(user.userImageUrl).into(imageViewAlertProfile)
        }
        val name = "${user.userName} ${user.userSurname}"
        textViewAlertName.text = name
        textViewAlertCity.text = user.userCity
        user.userBirthDate.let{
            textViewAlertAge.text = AppUtils.getAge(it!!).toString()
        }

        if(isCurrentUserLikedThisUser(user)){
            imageViewAlertLike.setImageResource(R.drawable.ic_like_red_64)
        }

        imageViewAlertLike.setOnClickListener{
            if(!isCurrentUserLikedThisUser(user)){
                val animBounce = AnimationUtils.loadAnimation(it.context, R.anim.anim_bounce)
                imageViewAlertLike.startAnimation(animBounce)
                imageViewAlertLike.setImageResource(R.drawable.ic_like_red_64)

                user.userID?.let{ userID->
                    UserViewModel().addLikedUser(currentUserId,userID)
                }
            }else{
                val animBounce = AnimationUtils.loadAnimation(it.context, R.anim.anim_bounce)
                imageViewAlertLike.startAnimation(animBounce)
                imageViewAlertLike.setImageResource(R.drawable.ic_like_white_64)
                user.userID?.let{ userID->
                    UserViewModel().removeLikedUser(currentUserId,userID)
                }
            }
        }
        alertDialogBuilder.create().show()
    }

    private fun isCurrentUserLikedThisUser(user: User): Boolean {
        for(likedUser in likedUserList){
            if(user.userID.equals(likedUser.userID)){
                return true
            }
        }
        return false
    }
}