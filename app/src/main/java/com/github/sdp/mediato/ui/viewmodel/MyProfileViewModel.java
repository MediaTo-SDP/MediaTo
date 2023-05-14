package com.github.sdp.mediato.ui.viewmodel;

import androidx.annotation.NonNull;

import com.github.sdp.mediato.data.CollectionsDatabase;
import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.Review;
import com.github.sdp.mediato.model.media.Collection;

import java.util.List;

public class MyProfileViewModel extends ReadOnlyProfileViewModel {
    /**
     * Adds a review to a collection or does nothing if the collection does not exits.
     *
     * @param review         the review to add
     * @param collectionName the name of the collection to add to
     */
    public void addReviewToCollection(@NonNull Review review, String collectionName) {
        Preconditions.checkNullOrEmptyString(collectionName, "collectionName");
        Collection collection = getCollection(collectionName);
        if (collection == null) {
            return;
        }

        collection.addReview(review);
        CollectionsDatabase.addReviewToCollection(getUsername(), collectionName, review);

        // Notify observers about the change in collections
        collectionsLiveData.setValue(getCollections());
    }

    /**
     * Adds a collection with the chosen name if no collection of that name already exists, or makes
     * no changes if such a collection already exists.
     *
     * @param collectionName name of the collection to add
     * @return true if the collection was successfully added, false otherwise
     */
    public boolean addCollection(String collectionName) {
        Preconditions.checkNullOrEmptyString(collectionName, "collectionName");
        List<Collection> collections = getCollections();

        for (Collection collection : collections) {
            if (collection.getCollectionName().equals(collectionName)) {
                return false;
            }
        }
        Collection newCollection = new Collection(collectionName);
        collections.add(newCollection);

        CollectionsDatabase.addCollection(getUsername(), newCollection);

        // Notify observers about the change in collections
        collectionsLiveData.setValue(collections);
        return true;
    }

    /**
     * Remove a collection with the given name.
     *
     * @param collectionName name of the collection to remove
     */
    public void removeCollection(String collectionName) {
        Preconditions.checkNullOrEmptyString(collectionName, "collectionName");
        List<Collection> collections = getCollections();
        collections.removeIf(collection -> collection.getCollectionName().equals(collectionName));

        CollectionsDatabase.removeCollection(getUsername(), collectionName);

        // Notify observers about the change in collections
        collectionsLiveData.setValue(collections);
    }
}
