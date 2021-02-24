package io.github.rosariopfernandes.firebasecompose.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

sealed class RTDBData {
    data class Snapshot(val snapshot: DataSnapshot) : RTDBData()
    data class Error(val databaseError: DatabaseError) : RTDBData()
    object Loading : RTDBData()
}