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

    suspend fun deleteDataItem(id: String) {
        dataCollection.document(id).delete().await()
    }
}
