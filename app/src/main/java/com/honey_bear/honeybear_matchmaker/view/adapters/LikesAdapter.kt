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
import com.honey_bear.honeybear_matchmaker.utils.AppUtils
import com.honey_bear.honeybear_matchmaker.view_model.UserViewModel

class LikesAdapter(
        private val mContext: Context,
        private val userList: ArrayList<User>
) : RecyclerView.Adapter<LikesAdapter.CardViewHolder>() {

    inner class CardViewHolder(view: View):RecyclerView.ViewHolder(view){
        var cardViewUser: CardView = view.findViewById(R.id.cardViewUser)
        var imageViewCardView: ImageView = view.findViewById(R.id.imageViewCardView)
        var imageViewLike: ImageView = view.findViewById(R.id.imageViewLike)
        var textViewPercent: TextView = view.findViewById(R.id.textViewPercent)
        fun bindUI(user: User) {
            textViewPercent.visibility=View.INVISIBLE
            if(user.userImageUrl!="no_image"){
                Glide.with(imageViewCardView).load(user.userImageUrl).into(imageViewCardView)
            }
            imageViewLike.setImageResource(R.drawable.ic_messages_64)

            imageViewLike.setOnClickListener{
                val animBounce = AnimationUtils.loadAnimation(it.context, R.anim.anim_bounce)
                imageViewLike.startAnimation(animBounce)
            }
            imageViewCardView.setOnClickListener {
                showAlertDialog(user)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.card_view, null)
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
        val imageViewAlertProfile:ImageView = view.findViewById(R.id.imageViewAlertProfile)
        val imageViewAlertLike:ImageView = view.findViewById(R.id.imageViewAlertLike)
        val textViewAlertName:TextView = view.findViewById(R.id.textViewAlertName)
        val textViewAlertCity:TextView = view.findViewById(R.id.textViewAlertCity)
        val textViewAlertAge:TextView = view.findViewById(R.id.textViewAlertAge)
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

        imageViewAlertLike.setImageResource(R.drawable.ic_messages_64)

        imageViewAlertLike.setOnClickListener{
            val animBounce = AnimationUtils.loadAnimation(it.context, R.anim.anim_bounce)
            imageViewAlertLike.startAnimation(animBounce)
        }
        alertDialogBuilder.create().show()
    }
}