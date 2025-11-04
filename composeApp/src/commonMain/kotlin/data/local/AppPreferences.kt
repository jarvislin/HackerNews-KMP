package data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class AppPreferences(private val dataStore: DataStore<Preferences>) {
    val seenItemList: Flow<Set<String>> =
        dataStore.data.map { preferences ->
            preferences[SEEN_ITEMS] ?: emptySet()
        }

    suspend fun markItemAsSeen(itemId: String) {
        dataStore.edit { preferences ->
            val currentSeenItems = preferences[SEEN_ITEMS] ?: emptySet()
            preferences[SEEN_ITEMS] = currentSeenItems + itemId
        }
    }

    companion object {
        val SEEN_ITEMS = stringSetPreferencesKey("seen_items")
    }
}
