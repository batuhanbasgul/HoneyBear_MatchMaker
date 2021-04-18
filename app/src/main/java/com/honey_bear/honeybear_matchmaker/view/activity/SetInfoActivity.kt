package com.honey_bear.honeybear_matchmaker.view.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.data.model.User
import com.honey_bear.honeybear_matchmaker.view_model.FirebaseAuthViewModel
import com.honey_bear.honeybear_matchmaker.view_model.UserViewModel
import kotlinx.android.synthetic.main.activity_set_info.*
import java.util.*
import kotlin.collections.ArrayList

class SetInfoActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var adapter: ArrayAdapter<CharSequence>
    private val pickImageRequest = 71
    private var imageUri: Uri? = null
    private val minBirthDateYear = 1900
    private val maxBirthDateYear = 2010
    private var isProviderFacebook:Boolean = false
    private lateinit var facebookPhotoUrl:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_info)

        //Setting service parameters and variables
        setServiceVariables()
        getInfoByFacebook()

        //Toolbar
        toolbarSetInfo.title = resources.getString(R.string.set_info)
        toolbarSetInfo.setLogo(R.drawable.ic_profile_64)
        setSupportActionBar(toolbarSetInfo)

        setSpinnerListData()
        setListeners()

    }

    private fun setServiceVariables() {
        userViewModel = ViewModelProvider(this@SetInfoActivity).get(UserViewModel::class.java)
        firebaseAuthViewModel = ViewModelProvider(this@SetInfoActivity).get(FirebaseAuthViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    private fun setListeners() {
        //PICK IMAGE
        imageViewSetInfo.setOnClickListener {
            if(!isProviderFacebook){
                pickImage()
            }
        }

        //PICK DATE
        editTextSetInfoBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this@SetInfoActivity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                when {
                    year > maxBirthDateYear -> {
                        editTextSetInfoBirthDate.error = resources.getString(R.string.info_birth_date_year_error)
                        Snackbar.make(toolbarSetInfo, R.string.info_birth_date_year_error, Snackbar.LENGTH_LONG).show()
                    }
                    year < minBirthDateYear -> {
                        editTextSetInfoBirthDate.error = resources.getString(R.string.info_birth_date_year_error)
                        Snackbar.make(toolbarSetInfo, R.string.info_birth_date_year_error, Snackbar.LENGTH_LONG).show()
                    }
                    else -> {
                        editTextSetInfoBirthDate.setText("$dayOfMonth-${month + 1}-$year")
                    }
                }
            }, year, month, day)

            datePickerDialog.setTitle(resources.getString(R.string.set_info_pick_date))
            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    resources.getString(R.string.set_info_pick),
                    datePickerDialog)
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    resources.getString(R.string.set_info_cancel),
                    datePickerDialog)
            datePickerDialog.show()
        }

        //SAVE DATA
        buttonSetInfoFinish.setOnClickListener {
            if(checkValidation()){
                firebaseAuthViewModel.currentFirebaseUser.observe(this@SetInfoActivity, Observer{
                    val email:String = it.email!!
                    val uid:String = it.uid
                    if (isProviderFacebook){
                        val userData = User(uid,
                                UUID.randomUUID().toString(),
                                facebookPhotoUrl,
                                editTextSetInfoName.text.toString(),
                                editTextSetInfoSurname.text.toString(),
                                email,
                                spinnerSetInfoGender.selectedItem.toString(),
                                editTextSetInfoBirthDate.text.toString(),
                                spinnerSetInfoCity.selectedItem.toString(),
                                spinnerSetInfoZodiac.selectedItem.toString(),
                                editTextSetInfoDescription.text.toString(),
                                spinnerSetInfoAgeRate.selectedItem.toString(),
                                spinnerSetInfoGenderInterest.selectedItem.toString(),
                                spinnerSetInfoSeason.selectedItem.toString(),
                                spinnerSetInfoWeather.selectedItem.toString(),
                                spinnerSetInfoMusic1.selectedItem.toString(),
                                spinnerSetInfoMusic2.selectedItem.toString(),
                                spinnerSetInfoSport1.selectedItem.toString(),
                                spinnerSetInfoSport2.selectedItem.toString(),
                                spinnerSetInfoBook1.selectedItem.toString(),
                                spinnerSetInfoBook2.selectedItem.toString(),
                                spinnerSetInfoHobby1.selectedItem.toString(),
                                spinnerSetInfoHobby2.selectedItem.toString(),
                                spinnerSetInfoMovie1.selectedItem.toString(),
                                spinnerSetInfoMovie2.selectedItem.toString())
                        userViewModel.insertFacebookUser(userData)
                    }else{
                        val userData = User(uid,
                                "",
                                "",
                                editTextSetInfoName.text.toString(),
                                editTextSetInfoSurname.text.toString(),
                                email,
                                spinnerSetInfoGender.selectedItem.toString(),
                                editTextSetInfoBirthDate.text.toString(),
                                spinnerSetInfoCity.selectedItem.toString(),
                                spinnerSetInfoZodiac.selectedItem.toString(),
                                editTextSetInfoDescription.text.toString(),
                                spinnerSetInfoAgeRate.selectedItem.toString(),
                                spinnerSetInfoGenderInterest.selectedItem.toString(),
                                spinnerSetInfoSeason.selectedItem.toString(),
                                spinnerSetInfoWeather.selectedItem.toString(),
                                spinnerSetInfoMusic1.selectedItem.toString(),
                                spinnerSetInfoMusic2.selectedItem.toString(),
                                spinnerSetInfoSport1.selectedItem.toString(),
                                spinnerSetInfoSport2.selectedItem.toString(),
                                spinnerSetInfoBook1.selectedItem.toString(),
                                spinnerSetInfoBook2.selectedItem.toString(),
                                spinnerSetInfoHobby1.selectedItem.toString(),
                                spinnerSetInfoHobby2.selectedItem.toString(),
                                spinnerSetInfoMovie1.selectedItem.toString(),
                                spinnerSetInfoMovie2.selectedItem.toString())
                        userViewModel.insertUser(userData,imageUri)
                    }
                    firebaseAuthViewModel.cancelJobs()
                })
                firebaseAuthViewModel.signOut(this,LoginActivity())
            }
        }
    }

    private fun getInfoByFacebook(){
        firebaseAuthViewModel.currentFirebaseUser.observe(this@SetInfoActivity,Observer{
            for(userInfo in it.providerData){
                if(FacebookAuthProvider.PROVIDER_ID == userInfo.providerId){
                    facebookPhotoUrl ="https://graph.facebook.com/${userInfo.uid}/picture?height=500"
                    Glide.with(imageViewSetInfo).load(facebookPhotoUrl).into(imageViewSetInfo)

                    val splitName = ArrayList<String>()
                    userInfo.displayName?.let{ displayName->
                        for(s in displayName.split(" ")){
                            splitName.add(s)
                        }
                        var fbName = ""
                        val fbSurname = splitName[splitName.size-1]
                        for(i in 0 until splitName.size-1){
                            fbName+="${splitName[i]} "
                        }
                        fbName.trim()
                        editTextSetInfoName.setText(fbName)
                        editTextSetInfoSurname.setText(fbSurname)
                        isProviderFacebook=true
                    }
                }
            }
        })
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action =Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, resources.getString(R.string.set_info_pick_image)), pickImageRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageViewSetInfo.setImageURI(imageUri)
        }
    }

    override fun onBackPressed() {
        firebaseAuthViewModel.signOut(this,LoginActivity())
        super.onBackPressed()
    }

    private fun setSpinnerListData() {
        adapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoGender.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoCity.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_zodiac, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoZodiac.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_age_rate, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoAgeRate.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_gender, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoGenderInterest.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_season, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoSeason.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_weather, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoWeather.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_music_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoMusic1.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_music_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoMusic2.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_sport_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoSport1.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_sport_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoSport2.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_book_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoBook1.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_book_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoBook2.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_hobby, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoHobby1.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_hobby, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoHobby2.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_movie_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoMovie1.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_movie_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSetInfoMovie2.adapter = adapter
    }

    private fun checkValidation(): Boolean {
        if (TextUtils.isEmpty(editTextSetInfoName.text.toString())) {
            Snackbar.make(toolbarSetInfo, R.string.info_name_empty_error, Snackbar.LENGTH_LONG).show()
            editTextSetInfoName.error = resources.getString(R.string.info_name_empty_error)
            return false
        } else if (TextUtils.isEmpty(editTextSetInfoSurname.text.toString())) {
            Snackbar.make(toolbarSetInfo, R.string.info_surname_empty_error, Snackbar.LENGTH_LONG).show()
            editTextSetInfoSurname.error = resources.getString(R.string.info_surname_empty_error)
            return false
        } else if (TextUtils.isEmpty(editTextSetInfoBirthDate.text.toString())) {
            Snackbar.make(toolbarSetInfo, R.string.info_birth_date_empty_error, Snackbar.LENGTH_LONG).show()
            editTextSetInfoBirthDate.error = resources.getString(R.string.info_birth_date_empty_error)
            return false
        } else if (spinnerSetInfoGender.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_gender_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoCity.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_city_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoZodiac.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_zodiac_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoAgeRate.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_age_rate_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoGenderInterest.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_interest_gender_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoSeason.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_season_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoWeather.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_weather_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoMusic1.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_music1_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoMusic2.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_music2_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoMusic1.selectedItem == spinnerSetInfoMusic2.selectedItem) {
            Snackbar.make(toolbarSetInfo, R.string.info_music_match_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoSport1.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_sport1_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoSport2.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_sport2_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoSport1.selectedItem == spinnerSetInfoSport2.selectedItem) {
            Snackbar.make(toolbarSetInfo, R.string.info_sport_match_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoBook1.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_book1_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoBook2.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_book2_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoBook1.selectedItem == spinnerSetInfoBook2.selectedItem) {
            Snackbar.make(toolbarSetInfo, R.string.info_book_match_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoHobby1.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_hobby1_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoHobby2.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_hobby2_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoHobby1.selectedItem == spinnerSetInfoHobby2.selectedItem) {
            Snackbar.make(toolbarSetInfo, R.string.info_hobby_match_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoMovie1.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_movie1_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoMovie2.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_movie2_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerSetInfoMovie1.selectedItem == spinnerSetInfoMovie2.selectedItem) {
            Snackbar.make(toolbarSetInfo, R.string.info_movie_match_error, Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }
}