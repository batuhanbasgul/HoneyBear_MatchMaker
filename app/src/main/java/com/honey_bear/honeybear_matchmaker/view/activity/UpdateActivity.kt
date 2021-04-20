package com.honey_bear.honeybear_matchmaker.view.activity

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.data.model.User
import com.honey_bear.honeybear_matchmaker.view_model.AuthViewModel
import com.honey_bear.honeybear_matchmaker.view_model.UserViewModel
import kotlinx.android.synthetic.main.activity_set_info.*
import kotlinx.android.synthetic.main.activity_update.*
import java.util.*

class UpdateActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var adapter: ArrayAdapter<CharSequence>
    private lateinit var currentUser: User

    private val pickImageRequest = 71
    private var imageUri: Uri? = null
    private val minBirthDateYear = 1900
    private val maxBirthDateYear = 2010
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        //Setting service parameters and variables
        setServiceVariables()

        //Toolbar
        toolbarUpdate.title = resources.getString(R.string.update)
        toolbarUpdate.setLogo(R.drawable.ic_profile_64)
        setSupportActionBar(toolbarUpdate)

        setListeners()
        getInfo()
    }

    private fun setServiceVariables() {
        userViewModel = ViewModelProvider(this@UpdateActivity).get(UserViewModel::class.java)
        authViewModel = ViewModelProvider(this@UpdateActivity).get(AuthViewModel::class.java)
        authViewModel.currentFirebaseUser.observe(this@UpdateActivity, Observer {
            userViewModel.setCurrentUserId(it.uid)
            authViewModel.cancelJobs()
        })
    }

    private fun setListeners() {
        //PICK IMAGE
        imageViewUpdate.setOnClickListener{
            pickImage()
        }

        //PICK DATE
        editTextUpdateBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                    this@UpdateActivity,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        when {
                            year > maxBirthDateYear -> {
                                editTextUpdateBirthDate.setError(resources.getString(R.string.info_birth_date_year_error));
                                Snackbar.make(
                                        toolbarUpdate,
                                        R.string.info_birth_date_year_error,
                                        Snackbar.LENGTH_LONG
                                ).show();
                            }
                            year < minBirthDateYear -> {
                                editTextUpdateBirthDate.setError(resources.getString(R.string.info_birth_date_year_error));
                                Snackbar.make(
                                        toolbarUpdate,
                                        R.string.info_birth_date_year_error,
                                        Snackbar.LENGTH_LONG
                                ).show();
                            }
                            else -> {
                                editTextUpdateBirthDate.setText("$dayOfMonth-${month + 1}-$year")
                            }
                        }
                    },
                    year,
                    month,
                    day
            )

            datePickerDialog.setTitle(resources.getString(R.string.set_info_pick_date))
            datePickerDialog.setButton(
                    DialogInterface.BUTTON_POSITIVE,
                    resources.getString(R.string.set_info_pick),
                    datePickerDialog
            )
            datePickerDialog.setButton(
                    DialogInterface.BUTTON_NEGATIVE,
                    resources.getString(R.string.set_info_cancel),
                    datePickerDialog
            )
            datePickerDialog.show()
        }

        //FINISH
        buttonUpdateFinish.setOnClickListener {
            if(checkValidation()){
                val userData = User(currentUser.userID,
                        currentUser.userImageName,
                        currentUser.userImageUrl,
                        editTextUpdateName.text.toString(),
                        editTextUpdateSurname.text.toString(),
                        currentUser.userMail,
                        spinnerUpdateGender.selectedItem.toString(),
                        editTextUpdateBirthDate.text.toString(),
                        spinnerUpdateCity.selectedItem.toString(),
                        spinnerUpdateZodiac.selectedItem.toString(),
                        editTextUpdateDescription.text.toString(),
                        spinnerUpdateAgeRate.selectedItem.toString(),
                        spinnerUpdateGenderInterest.selectedItem.toString(),
                        spinnerUpdateSeason.selectedItem.toString(),
                        spinnerUpdateWeather.selectedItem.toString(),
                        spinnerUpdateMusic1.selectedItem.toString(),
                        spinnerUpdateMusic2.selectedItem.toString(),
                        spinnerUpdateSport1.selectedItem.toString(),
                        spinnerUpdateSport2.selectedItem.toString(),
                        spinnerUpdateBook1.selectedItem.toString(),
                        spinnerUpdateBook2.selectedItem.toString(),
                        spinnerUpdateHobby1.selectedItem.toString(),
                        spinnerUpdateHobby2.selectedItem.toString(),
                        spinnerUpdateMovie1.selectedItem.toString(),
                        spinnerUpdateMovie2.selectedItem.toString())
                userViewModel.updateUser(userData,imageUri)
                startActivity(Intent(this@UpdateActivity,MainActivity::class.java))
                finish()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        resources.getString(R.string.set_info_pick_image)
                ), pickImageRequest
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageViewUpdate.setImageURI(imageUri)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@UpdateActivity,MainActivity::class.java))
        finish()
        super.onBackPressed()
    }

    private fun getInfo() {
        userViewModel.currentUser.observe(this@UpdateActivity, Observer {
            currentUser=it
            userViewModel.cancelJobs()
            setInfo()
        })
    }

    private fun setInfo() {
        if(currentUser.userImageUrl != "no_image"){
            Glide.with(imageViewUpdate).load(currentUser.userImageUrl).into(imageViewUpdate)
        }

        editTextUpdateName.setText(currentUser.userName)
        editTextUpdateSurname.setText(currentUser.userSurname)
        editTextUpdateBirthDate.setText(currentUser.userBirthDate)
        editTextUpdateDescription.setText(currentUser.userDescription)

        adapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateGender.adapter = adapter
        if (currentUser.userGender != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userGender)
            spinnerUpdateGender.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateCity.adapter = adapter
        if (currentUser.userCity != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userCity)
            spinnerUpdateCity.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_zodiac, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateZodiac.adapter = adapter
        if (currentUser.userZodiac != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userZodiac)
            spinnerUpdateZodiac.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_age_rate, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateAgeRate.adapter = adapter
        if (currentUser.userAgeRate != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userAgeRate)
            spinnerUpdateAgeRate.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_gender, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateGenderInterest.adapter = adapter
        if (currentUser.userInterestsGender != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsGender)
            spinnerUpdateGenderInterest.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_season, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateSeason.adapter = adapter
        if (currentUser.userInterestsSeason != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsSeason)
            spinnerUpdateSeason.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_weather, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateWeather.adapter = adapter
        if (currentUser.userInterestsWeather != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsWeather)
            spinnerUpdateWeather.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_music_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateMusic1.adapter = adapter
        if (currentUser.userInterestsMusicType1 != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsMusicType1)
            spinnerUpdateMusic1.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_music_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateMusic2.adapter = adapter
        if (currentUser.userInterestsMusicType2 != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsMusicType2)
            spinnerUpdateMusic2.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_sport_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateSport1.adapter = adapter
        if (currentUser.userInterestsSportType1 != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsSportType1)
            spinnerUpdateSport1.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_sport_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateSport2.adapter = adapter
        if (currentUser.userInterestsSportType2 != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsSportType2)
            spinnerUpdateSport2.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_book_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateBook1.adapter = adapter
        if (currentUser.userInterestsBookType1 != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsBookType1)
            spinnerUpdateBook1.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_book_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateBook2.adapter = adapter
        if (currentUser.userInterestsBookType2 != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsBookType2)
            spinnerUpdateBook2.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_hobby, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateHobby1.adapter = adapter
        if (currentUser.userInterestsHobby1 != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsHobby1)
            spinnerUpdateHobby1.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_hobby, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateHobby2.adapter = adapter
        if (currentUser.userInterestsHobby2 != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsHobby2)
            spinnerUpdateHobby2.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_movie_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateMovie1.adapter = adapter
        if (currentUser.userInterestsMovieType1 != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsMovieType1)
            spinnerUpdateMovie1.setSelection(spinnerPosition)
        }

        adapter = ArrayAdapter.createFromResource(this, R.array.interests_movie_type, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateMovie2.adapter = adapter
        if (currentUser.userInterestsMovieType2 != null) {
            val spinnerPosition = adapter.getPosition(currentUser.userInterestsMovieType2)
            spinnerUpdateMovie2.setSelection(spinnerPosition)
        }
    }

    private fun checkValidation(): Boolean {
        if (TextUtils.isEmpty(editTextUpdateName.text.toString())) {
            Snackbar.make(toolbarSetInfo, R.string.info_name_empty_error, Snackbar.LENGTH_LONG).show()
            editTextUpdateName.error = resources.getString(R.string.info_name_empty_error)
            return false
        } else if (TextUtils.isEmpty(editTextUpdateSurname.text.toString())) {
            Snackbar.make(toolbarSetInfo, R.string.info_surname_empty_error, Snackbar.LENGTH_LONG).show()
            editTextUpdateSurname.error = resources.getString(R.string.info_surname_empty_error)
            return false
        } else if (TextUtils.isEmpty(editTextUpdateBirthDate.text.toString())) {
            Snackbar.make(toolbarSetInfo, R.string.info_birth_date_empty_error, Snackbar.LENGTH_LONG).show()
            editTextUpdateBirthDate.error = resources.getString(R.string.info_birth_date_empty_error)
            return false
        } else if (spinnerUpdateGender.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_gender_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateCity.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_city_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateZodiac.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_zodiac_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateAgeRate.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_age_rate_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateGenderInterest.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_interest_gender_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateSeason.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_season_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateWeather.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_weather_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateMusic1.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_music1_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateMusic2.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_music2_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateMusic1.selectedItem == spinnerUpdateMusic2.selectedItem) {
            Snackbar.make(toolbarSetInfo, R.string.info_music_match_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateSport1.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_sport1_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateSport2.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_sport2_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateSport1.selectedItem == spinnerUpdateSport2.selectedItem) {
            Snackbar.make(toolbarSetInfo, R.string.info_sport_match_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateBook1.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_book1_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateBook2.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_book2_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateBook1.selectedItem == spinnerUpdateBook2.selectedItem) {
            Snackbar.make(toolbarSetInfo, R.string.info_book_match_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateHobby1.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_hobby1_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateHobby2.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_hobby2_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateHobby1.selectedItem == spinnerUpdateHobby2.selectedItem) {
            Snackbar.make(toolbarSetInfo, R.string.info_hobby_match_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateMovie1.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_movie1_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateMovie2.selectedItem == "...") {
            Snackbar.make(toolbarSetInfo, R.string.info_movie2_error, Snackbar.LENGTH_LONG).show()
            return false
        } else if (spinnerUpdateMovie1.selectedItem == spinnerUpdateMovie2.selectedItem) {
            Snackbar.make(toolbarSetInfo, R.string.info_movie_match_error, Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }


}