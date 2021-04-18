package com.honey_bear.honeybear_matchmaker.data.service

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.honey_bear.honeybear_matchmaker.data.model.User
import com.honey_bear.honeybear_matchmaker.utils.AppUtils
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object UserRepository {
    private val userRef = FirebaseDatabase.getInstance().reference
    private val storageRef = FirebaseStorage.getInstance().reference
    private var job: CompletableJob? = null

    /*
        Get all users from database
     */
    fun getUsers() : LiveData<ArrayList<User>>{
        val userList = ArrayList<User>()
        job= Job()

        return object : LiveData<ArrayList<User>>() {
            override fun onActive() {
                super.onActive()
                job?.let{
                    CoroutineScope(Dispatchers.IO + it).launch {
                        val query: Query = userRef.child("User")
                        userList.clear()
                        val dataListener = object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (d:DataSnapshot in snapshot.children){
                                    d.getValue(User::class.java)?.let { user ->
                                        userList.add(user)
                                    }
                                }
                                value = userList
                                it.complete()
                            }
                            override fun onCancelled(error: DatabaseError) {
                                //TODO
                            }
                        }
                        query.addListenerForSingleValueEvent(dataListener)
                    }
                }
            }
        }
    }

    /*
        Get current user from database
     */
    fun getCurrentUser(currentUserId:String) : LiveData<User>{
        job = Job()

        return object : LiveData<User>() {
            override fun onActive() {
                super.onActive()
                job?.let{
                    CoroutineScope(Dispatchers.IO + it).launch {
                        val query:Query = userRef.child("User").orderByChild("userID").equalTo(currentUserId)
                        val dataListener = object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(d:DataSnapshot in snapshot.children){
                                    d.getValue(User::class.java)?.let{ user->
                                        if(user.userID.equals(currentUserId)){
                                            value=user
                                            it.complete()
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        }
                        query.addListenerForSingleValueEvent(dataListener)
                    }
                }
            }
        }
    }

    /*
        Get users that included current users LikesFromUsers list
     */
    fun getBeingLikedUsers(currentUserId:String) : LiveData<ArrayList<User>>{
        val likesFromUsersIdList=ArrayList<String>()
        val likesFromUsersList=ArrayList<User>()
        job = Job()

        return object : LiveData<ArrayList<User>>(){
            override fun onActive() {
                super.onActive()
                job?.let{
                    CoroutineScope(Dispatchers.IO+it).launch {
                        val query:Query = userRef.child("User").orderByChild("userID").equalTo(currentUserId)
                        val dataListener = object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(d:DataSnapshot in snapshot.children){
                                    d.getValue(User::class.java)?.let{ user->
                                        if(user.userID.equals(currentUserId)){
                                            d.key?.let{ key->
                                                val queryBeingLikedIds:Query = userRef.child("User").child(key).child("LikesFromUsers")
                                                val beingLikedListener = object : ValueEventListener{
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        for(d2:DataSnapshot in snapshot.children){
                                                            d2.getValue(String::class.java)?.let{ userId->
                                                                if(userId != "empty_space"){
                                                                    likesFromUsersIdList.add(userId)
                                                                }
                                                            }
                                                        }
                                                        val beingLikedUsers:Query = userRef.child("User")
                                                        val beingLikedUsersListener = object : ValueEventListener{
                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                for(d3:DataSnapshot in snapshot.children){
                                                                    d3.getValue(User::class.java)?.let{user->
                                                                        if(likesFromUsersIdList.contains(user.userID))
                                                                        likesFromUsersList.add(user)
                                                                    }
                                                                }
                                                                value=likesFromUsersList
                                                                it.complete()
                                                            }
                                                            override fun onCancelled(error: DatabaseError) {}
                                                        }
                                                        beingLikedUsers.addListenerForSingleValueEvent(beingLikedUsersListener)
                                                    }
                                                    override fun onCancelled(error: DatabaseError) {}
                                                }
                                                queryBeingLikedIds.addListenerForSingleValueEvent(beingLikedListener)
                                            }
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        }
                        query.addListenerForSingleValueEvent(dataListener)
                    }
                }
            }
        }
    }

    /*
        Get users that included current users Likes list
     */
    fun getLikedUsers(currentUserId:String) : LiveData<ArrayList<User>>{
        val likedUsersIdList=ArrayList<String>()
        val likedUsersList=ArrayList<User>()
        job = Job()

        return object : LiveData<ArrayList<User>>(){
            override fun onActive() {
                super.onActive()
                job?.let{
                    CoroutineScope(Dispatchers.IO+it).launch {
                        val query:Query = userRef.child("User").orderByChild("userID").equalTo(currentUserId)
                        val dataListener = object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(d:DataSnapshot in snapshot.children){
                                    d.getValue(User::class.java)?.let{ user->
                                        if(user.userID.equals(currentUserId)){
                                            d.key?.let{ key->
                                                val queryBeingLikedIds:Query = userRef.child("User").child(key).child("Likes")
                                                val beingLikedListener = object : ValueEventListener{
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        for(d2:DataSnapshot in snapshot.children){
                                                            d2.getValue(String::class.java)?.let{ userId->
                                                                if(userId != "empty_space"){
                                                                    likedUsersIdList.add(userId)
                                                                }
                                                            }
                                                        }
                                                        val beingLikedUsers:Query = userRef.child("User")
                                                        val beingLikedUsersListener = object : ValueEventListener{
                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                for(d3:DataSnapshot in snapshot.children){
                                                                    d3.getValue(User::class.java)?.let{user->
                                                                        if(likedUsersIdList.contains(user.userID))
                                                                            likedUsersList.add(user)
                                                                    }
                                                                }
                                                                value=likedUsersList
                                                                it.complete()
                                                            }
                                                            override fun onCancelled(error: DatabaseError) {}
                                                        }
                                                        beingLikedUsers.addListenerForSingleValueEvent(beingLikedUsersListener)
                                                    }
                                                    override fun onCancelled(error: DatabaseError) {}
                                                }
                                                queryBeingLikedIds.addListenerForSingleValueEvent(beingLikedListener)
                                            }
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        }
                        query.addListenerForSingleValueEvent(dataListener)
                    }
                }
            }
        }
    }

    /*
        Get users that mutual on current users Likes and LikesFromUsers list
     */
    fun getMutualLikes(currentUserId:String) : LiveData<ArrayList<User>>{
        val likedUsersIds = ArrayList<String>()
        val likesFromUsersIds = ArrayList<String>()
        val mutualLikes = ArrayList<User>()
        job = Job()

        return object : LiveData<ArrayList<User>>(){
            override fun onActive() {
                super.onActive()
                job?.let{
                    CoroutineScope(Dispatchers.IO+it).launch{
                        val query:Query = userRef.child("User").orderByChild("userID").equalTo(currentUserId)
                        val dataListener = object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(d:DataSnapshot in snapshot.children){
                                    d.getValue(User::class.java)?.let{ user->
                                        if(user.userID.equals(currentUserId)){
                                            d.key?.let { key->
                                                val likedUsersIdsQuery:Query = userRef.child("User").child(key).child("Likes")
                                                val likedUsersIdsListener = object : ValueEventListener{
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        for (d2:DataSnapshot in snapshot.children){
                                                            d2.getValue(String::class.java)?.let{ userId->
                                                                likedUsersIds.add(userId)
                                                            }
                                                        }
                                                        val likesFromUsersIdsQuery:Query = userRef.child("User").child(key).child("LikesFromUsers")
                                                        val likesFromUsersIdsListener = object : ValueEventListener{
                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                for (d3:DataSnapshot in snapshot.children){
                                                                    d3.getValue(String::class.java)?.let{ userId->
                                                                        likesFromUsersIds.add(userId)
                                                                    }
                                                                }
                                                                val mutualIds:ArrayList<String> = AppUtils.getMutualIdList(likedUsersIds,likesFromUsersIds)
                                                                val mutualLikesQuery:Query = userRef.child("User")
                                                                val mutualLikesListener = object : ValueEventListener{
                                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                                        for(d4:DataSnapshot in snapshot.children){
                                                                            d4.getValue(User::class.java)?.let{ user->
                                                                                if(mutualIds.contains(user.userID)){
                                                                                    mutualLikes.add(user)
                                                                                }
                                                                            }
                                                                        }
                                                                        value=mutualLikes
                                                                        it.complete()
                                                                    }
                                                                    override fun onCancelled(error: DatabaseError) {}
                                                                }
                                                                mutualLikesQuery.addListenerForSingleValueEvent(mutualLikesListener)
                                                            }
                                                            override fun onCancelled(error: DatabaseError) {}
                                                        }
                                                        likesFromUsersIdsQuery.addListenerForSingleValueEvent(likesFromUsersIdsListener)
                                                    }
                                                    override fun onCancelled(error: DatabaseError) {}
                                                }
                                                likedUsersIdsQuery.addListenerForSingleValueEvent(likedUsersIdsListener)
                                            }
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        }
                        query.addListenerForSingleValueEvent(dataListener)
                    }
                }
            }
        }
    }

    fun cancelJobs(){
        job?.cancel()
    }

    /*
        Insert user into database
        With image or without image
        While inserting new user info creates likesFromUser child and likedUsers
     */
    fun insertUser(user: User, imageUri: Uri?){
        val uniqueName = UUID.randomUUID().toString()
        val dbRef = userRef.child("User")
        val imageFile:StorageReference = storageRef.child("profile_images/$uniqueName")
        if(imageUri==null){
            user.userImageUrl = "no_image"
            user.userImageName = "no_image"
            dbRef.push().setValue(user)
            createLikesChild(user.userID)
        }else{
            imageUri?.let{
                imageFile.putFile(imageUri).addOnSuccessListener {
                    imageFile.downloadUrl.addOnSuccessListener {
                        user.userImageUrl = it.toString()
                        user.userImageName = uniqueName
                        dbRef.push().setValue(user)
                        createLikesChild(user.userID)
                    }
                }
            }
        }
    }
    fun insertFacebookUser(user: User){
        val dbRef = userRef.child("User")
        dbRef.push().setValue(user)
        createLikesChild(user.userID)
    }
    private fun createLikesChild(userID: String?){
        val query:Query = userRef.child("User").orderByChild("userID").equalTo(userID)
        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (d:DataSnapshot in snapshot.children){
                    d.getValue(User::class.java)?.let { user->
                        if(user.userID.equals(userID)){
                            d.key?.let {
                                var tempRef = userRef.child("User").child(it).child("Likes")
                                tempRef.push().setValue("empty_space")
                                tempRef = userRef.child("User").child(it).child("LikesFromUsers")
                                tempRef.push().setValue("empty_space")
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        }
        query.addListenerForSingleValueEvent(dataListener)
    }

    /*
        Update user info
        If no image just updates info
        If there is an image checks if already a image
        If there was already images puts new one into database and deletes old one
        If there was not a image just insert new images and updates info
     */
    fun updateUser(user: User, imageUri: Uri?){
        val uniqueName = UUID.randomUUID().toString()
        val imageFile:StorageReference = storageRef.child("profile_images/$uniqueName")

        if(imageUri==null){
            update(user)
        }else{
            if(user.userImageName.equals("no_image") || user.userImageUrl.equals("no_image")){
                imageUri?.let{
                    imageFile.putFile(imageUri).addOnSuccessListener {
                        imageFile.downloadUrl.addOnSuccessListener {
                            user.userImageUrl = it.toString()
                            user.userImageName = uniqueName
                            update(user)
                        }
                    }
                }
            }else{
                val oldImageName = user.userImageName
                imageUri?.let{
                    imageFile.putFile(imageUri).addOnSuccessListener {
                        imageFile.downloadUrl.addOnSuccessListener {
                            user.userImageUrl = it.toString()
                            user.userImageName = uniqueName
                            update(user)
                        }
                    }
                }
                deleteOldImage(oldImageName)
            }
        }
    }
    private fun update(user:User){
        val mapList = HashMap<String,Any>()
        mapList["userImageName"]=user.userImageName!!
        mapList["userImageUrl"]=user.userImageUrl!!
        mapList["userName"]=user.userName!!
        mapList["userSurname"]=user.userSurname!!
        mapList["userZodiac"]=user.userZodiac!!
        mapList["userGender"]=user.userGender!!
        mapList["userBirthDate"]=user.userBirthDate!!
        mapList["userCity"]=user.userCity!!
        mapList["userDescription"]=user.userDescription!!
        mapList["userAgeRate"]=user.userAgeRate!!
        mapList["userInterestsGender"]=user.userInterestsGender!!
        mapList["userInterestsSeason"]=user.userInterestsSeason!!
        mapList["userInterestsWeather"]=user.userInterestsWeather!!
        mapList["userInterestsMusicType1"]=user.userInterestsMusicType1!!
        mapList["userInterestsMusicType2"]=user.userInterestsMusicType2!!
        mapList["userInterestsSportType1"]=user.userInterestsSportType1!!
        mapList["userInterestsSportType2"]=user.userInterestsSportType2!!
        mapList["userInterestsBookType1"]=user.userInterestsBookType1!!
        mapList["userInterestsBookType2"]=user.userInterestsBookType2!!
        mapList["userInterestsHobby1"]=user.userInterestsHobby1!!
        mapList["userInterestsHobby2"]=user.userInterestsHobby2!!
        mapList["userInterestsMovieType1"]=user.userInterestsMovieType1!!
        mapList["userInterestsMovieType2"]=user.userInterestsMovieType2!!

        val query:Query = userRef.child("User").orderByChild("userID").equalTo(user.userID)
        val userListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (d:DataSnapshot in snapshot.children){
                    d.getValue(User::class.java)?.let {
                        d.key?.let {
                            userRef.child("User").child(d.key!!).updateChildren(mapList)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        }
        query.addListenerForSingleValueEvent(userListener)
    }
    private fun deleteOldImage(oldImageName:String?) {
        oldImageName?.let {
            val imageFile:StorageReference = storageRef.child("profile_images/$oldImageName")
            imageFile.delete()
        }
    }

    /*
        Firstly gets current user and creates a references with key of current user
        After that checks if that users liked list contains that likedUser's ID
        If there is no ID for likedUser's, add it.

        Then gets liked user's info for adding current user's ID into liked user's likesFromUsers
        Creates a reference into likedUsers likesFromUsers.
        After that checks if liked user's likedFromUsers child contains current user's ID
        If doesn't contains adds current user's id into liked user's likedFromUsers child.

     */
    fun addLikedUser(currentUserId:String,likedUserId:String){
        val query:Query = userRef.child("User").orderByChild("userID").equalTo(currentUserId)
        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(d:DataSnapshot in snapshot.children){
                    d.getValue(User::class.java)?.let { user->
                        if(user.userID.equals(currentUserId)){
                            d.key?.let{
                                val dbRef = userRef.child("User").child(it).child("Likes")
                                addLikedUserIfNotExists(dbRef,likedUserId)
                                addLikesFromUser(currentUserId,likedUserId)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        }
        query.addListenerForSingleValueEvent(dataListener)
    }
    private fun addLikedUserIfNotExists(dbRef:DatabaseReference,likedUserId:String){
        var isAlreadyExists = false
        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(d:DataSnapshot in snapshot.children){
                    d.getValue(String::class.java)?.let{
                        if(it == likedUserId){
                            isAlreadyExists = true
                        }
                    }
                }
                if(!isAlreadyExists){
                    dbRef.push().setValue(likedUserId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        }
        dbRef.addListenerForSingleValueEvent(dataListener)
    }
    private fun addLikesFromUser(currentUserId:String,likedUserId:String){
        val query:Query = userRef.child("User").orderByChild("userID").equalTo(likedUserId)
        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(d:DataSnapshot in snapshot.children){
                    d.getValue(User::class.java)?.let { user->
                        if(user.userID == likedUserId){
                            d.key?.let{
                                val dbRef = userRef.child("User").child(it).child("LikesFromUsers")
                                addLikesFromUserIfNotExists(dbRef,currentUserId)
                            }
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        }
        query.addListenerForSingleValueEvent(dataListener)
    }
    private fun addLikesFromUserIfNotExists(dbRef:DatabaseReference,currentUserId:String){
        var isAlreadyExists = false
        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(d:DataSnapshot in snapshot.children){
                    d.getValue(String::class.java)?.let{
                        if(it == currentUserId){
                            isAlreadyExists = true
                        }
                    }
                }
                if(!isAlreadyExists){
                    dbRef.push().setValue(currentUserId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        }
        dbRef.addListenerForSingleValueEvent(dataListener)
    }

    /*
        Firstly gets current user and creates a references with key of current user
        After that checks if that users liked list contains that likedUser's ID
        If there is ID for likedUser's, deletes it.

        Then gets liked user's info for deleting current user's ID from liked user's likesFromUsers
        Creates a reference into likedUsers likesFromUsers.
        After that checks if liked user's likedFromUsers child contains current user's ID
        If it does contains deletes current user's id from liked user's likedFromUsers child.

     */
    fun removeLikedUser(currentUserId:String,likedUserId:String){
        val query:Query = userRef.child("User").orderByChild("userID").equalTo(currentUserId)
        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(d:DataSnapshot in snapshot.children){
                    d.getValue(User::class.java)?.let{ user->
                        if(user.userID.equals(currentUserId)){
                            d.key?.let{
                                val dbRef = userRef.child("User").child(it).child("Likes")
                                removeLikedUserIfExists(dbRef,likedUserId)
                                removeLikesFromUser(currentUserId,likedUserId)
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        }
        query.addListenerForSingleValueEvent(dataListener)
    }
    private fun removeLikedUserIfExists(dbRef:DatabaseReference,likedUserId:String){
        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(d:DataSnapshot in snapshot.children){
                    d.getValue(String::class.java)?.let{
                        if(it == likedUserId){
                            d.key?.let{ key->
                                dbRef.child(key).removeValue()
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        }
        dbRef.addListenerForSingleValueEvent(dataListener)
    }
    private fun removeLikesFromUser(currentUserId:String,likedUserId: String){
        val query:Query = userRef.child("User").orderByChild("userID").equalTo(likedUserId)
        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(d:DataSnapshot in snapshot.children){
                    d.getValue(User::class.java)?.let{ user->
                        if(user.userID.equals(likedUserId)){
                            d.key?.let{
                                val dbRef = userRef.child("User").child(it).child("LikesFromUsers")
                                removeLikesFromUserIfExists(dbRef,currentUserId)
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        }
        query.addListenerForSingleValueEvent(dataListener)
    }
    private fun removeLikesFromUserIfExists(dbRef:DatabaseReference,currentUserId: String){
        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (d:DataSnapshot in snapshot.children){
                    d.getValue(String::class.java)?.let{
                        if(it == currentUserId){
                            d.key?.let{ key->
                                dbRef.child(key).removeValue()
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        }
        dbRef.addListenerForSingleValueEvent(dataListener)
    }

}