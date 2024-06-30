package com.bitbyter.twodoo.repository

import com.bitbyter.twodoo.data.ToDoDataItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val dataCollection = db.collection("toDo-Data")

    suspend fun addToDoDataItem(toDoDataItem: ToDoDataItem) {
        dataCollection.add(toDoDataItem).await()
    }

    suspend fun addReminderData(id: String, reminderTime: String) {
        val reminderMap = mapOf("reminderTime" to reminderTime)
        dataCollection.document(id).update(reminderMap).await()
    }

    suspend fun getToDoDataItems(): List<ToDoDataItem> {
        return try {
            val snapshot = dataCollection.get().await()
            snapshot.documents.map { document ->
                document.toObject(ToDoDataItem::class.java)!!.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateToDoItem(updatedItem: ToDoDataItem) {
//        val updatedDataMap = mapOf(
//            "title" to updatedItem.title,
//            "description" to updatedItem.description,
//            "reminderTime" to updatedItem.reminderTime,
//            "isChecked" to updatedItem.isChecked
//        )
        dataCollection.document(updatedItem.id).set(updatedItem).await()
    }

    suspend fun deleteDataItem(id: String) {
        dataCollection.document(id).delete().await()
    }
}
