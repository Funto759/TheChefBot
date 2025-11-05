package com.example.thechefbot.presentation.SettingsFeat.model


import com.example.thechefbot.presentation.SettingsFeat.data.AppUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject


class UserRepository (
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    private fun doc(uid: String) = db.collection("users").document(uid)

    /** Create or merge user doc (call after sign up / first sign in) */
    fun upsertCurrentUser(extra: AppUser? = null, onDone: (Boolean, String?) -> Unit) {
        val u = auth.currentUser ?: return onDone(false, "Not signed in")

        val base = AppUser(
            uid = u.uid,
            email = u.email,
            photoUrl = u.photoUrl?.toString(),
        )
        val toSave = if (extra != null) base.copy(
            bio = extra.bio,
            full_name = extra.full_name,
            updatedAt = System.currentTimeMillis(),
            phone_number = extra.phone_number
        ) else base.copy(updatedAt = System.currentTimeMillis())

        doc(u.uid).set(toSave, SetOptions.merge())
            .addOnCompleteListener { onDone(it.isSuccessful, it.exception?.message) }
    }

    /** Update a subset of fields */
    fun updateCurrentUser(fields: Map<String, Any?>, onDone: (Boolean, String?) -> Unit) {
        val u = auth.currentUser ?: return onDone(false, "Not signed in")
        val payload = fields.filterValues { it != null } + ("updatedAt" to System.currentTimeMillis())
        doc(u.uid).set(payload, SetOptions.merge())
            .addOnCompleteListener { onDone(it.isSuccessful, it.exception?.message) }
    }

    /** One-shot fetch */
    fun getCurrentUserOnce(onResult: (AppUser?) -> Unit) {
        val u = auth.currentUser ?: return onResult(null)
        doc(u.uid).get().addOnSuccessListener { snap ->
            onResult(snap.toObject(AppUser::class.java))
        }.addOnFailureListener { onResult(null) }
    }

    /** Live updates (remember to remove listener) */
    fun listenCurrentUser(onUpdate: (AppUser?) -> Unit): ListenerRegistration? {
        val u = auth.currentUser ?: return null
        return doc(u.uid).addSnapshotListener { snap, _ ->
            onUpdate(snap?.toObject(AppUser::class.java))
        }
    }


}