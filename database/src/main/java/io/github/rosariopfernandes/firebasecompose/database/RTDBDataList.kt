package io.github.rosariopfernandes.firebasecompose.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

sealed class RTDBDataList {
    data class SnapshotList(val snapshot: List<DataSnapshot>) : RTDBDataList()
    data class Error(val databaseError: DatabaseError) : RTDBDataList()
    object Loading : RTDBDataList()
}