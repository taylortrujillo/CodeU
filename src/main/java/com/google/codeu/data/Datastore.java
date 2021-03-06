/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Provides access to the data stored in Datastore. */
public class Datastore {


  private DatastoreService datastore;

  public Datastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }



  /** Stores the Message in Datastore. */
  public void storeMessage(Message message) {
    Entity messageEntity = new Entity("Message", message.getId().toString());
    messageEntity.setProperty("user", message.getUser());
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());
    //Added by Nicole for Direct Message step 4
    messageEntity.setProperty("recipient", message.getRecipient());

    messageEntity.setProperty("sentimentScore", message.getSentimentScore());


    if(message.getImageUrl() != null) {
        messageEntity.setProperty("imageUrl", message.getImageUrl());
    }

    datastore.put(messageEntity);
  }



  /**
   * Gets messages posted by a specific user.
   *
   * @return a list of messages posted by the user, or empty list if user has never posted a
   *     message. List is sorted by time descending.
   */
  public List<Message> getMessages(String recipient) {
    List<Message> messages = new ArrayList<>();

    //function returns the messages where the user is the recipient instead of the author. Change in line 63 by Nicole Barra
    //Direct Messages guide part 6.
    Query query =
        new Query("Message")
            .setFilter(new Query.FilterPredicate("recipient", FilterOperator.EQUAL, recipient))
            .addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {

        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");


        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");

        double sentimentScore = (double) entity.getProperty("sentimentScore");

        //Added by Nicole Barra for Direct Message step 4
        Message message = new Message(id, user, text, timestamp, recipient, sentimentScore);

        String imageUrl = (String) entity.getProperty("imageUrl");



        message.setImageUrl(imageUrl);

        messages.add(message);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }

   }
   return messages;
 }

    /**
     * Returns the total number of messages for all users.
     */
    public int getTotalMessageCount() {
        Query query = new Query("Message");
        PreparedQuery results = datastore.prepare(query);
        return results.countEntities(FetchOptions.Builder.withLimit(1000));
    }

    


    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();

        Query query = new Query("Message")
                .addSort("timestamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);

        for (Entity entity : results.asIterable()) {
            try {
                String idString = entity.getKey().getName();
                UUID id = UUID.fromString(idString);
                String user = (String) entity.getProperty("user");
                String text = (String) entity.getProperty("text");
                long timestamp = (long) entity.getProperty("timestamp");
                String recipient = (String) entity.getProperty("recipient");

                double sentimentScore = (double) entity.getProperty("sentimentScore");

                Message message = new Message(id, user, text, timestamp,recipient,sentimentScore);

                
                String imageUrl = (String) entity.getProperty("imageUrl");
                message.setImageUrl(imageUrl);

                messages.add(message);
            } catch (Exception e) {
                System.err.println("Error reading message.");
                System.err.println(entity.toString());
                e.printStackTrace();
            }
        }
        return messages;
    }


        public void storeUser (User user){
            Entity userEntity = new Entity("User", user.getEmail());
            userEntity.setProperty("email", user.getEmail());
            userEntity.setProperty("aboutMe", user.getAboutMe());
            userEntity.setProperty("dogName", user.getDogName());
            userEntity.setProperty("breed", user.getBreed());
            userEntity.setProperty("gender", user.getGender());
            userEntity.setProperty("description", user.getDescription());

            datastore.put(userEntity);
        }

        /**
         * Returns the User owned by the email address, or
         * null if no matching User was found.
         */
        public User getUser (String email){

            Query query = new Query("User")
                    .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
            PreparedQuery results = datastore.prepare(query);
            Entity userEntity = results.asSingleEntity();
            if (userEntity == null) {
                return null;
            }
            email = (String) userEntity.getProperty("email");
            String aboutMe = (String) userEntity.getProperty("aboutMe");
            String username = (String) userEntity.getProperty("username");
            String dogName = (String) userEntity.getProperty("dogName");
            String breed = (String) userEntity.getProperty("breed");
            String gender = (String) userEntity.getProperty("gender");
            String description = (String) userEntity.getProperty("description");
            User user = new User(email, aboutMe, dogName, breed, gender, description);

            return user;
        }




	public List<UserMarker> getMarkers() {

		List<UserMarker> markers = new ArrayList<>();

		Query query = new Query("UserMarker");
		PreparedQuery results = datastore.prepare(query);

		for (Entity entity : results.asIterable()) {
			try {
				double lat = (double) entity.getProperty("lat");
				double lng = (double) entity.getProperty("lng");
				String nameEvent = (String) entity.getProperty("nameEvent");
				String timeEvent = (String) entity. getProperty("timeEvent");
				String dateEvent = (String) entity.getProperty("dateEvent");
				String content = (String) entity.getProperty("content");

				UserMarker marker = new UserMarker(lat, lng, nameEvent, timeEvent, dateEvent, content);
				markers.add(marker);
			} catch (Exception e) {
				System.err.println("Error reading marker.");
				System.err.println(entity.toString());
				e.printStackTrace();
			}
		}
		return markers;
	}

	public void storeMarker(UserMarker marker) {
		Entity markerEntity = new Entity("UserMarker");
		markerEntity.setProperty("lat", marker.getLat());
		markerEntity.setProperty("lng", marker.getLng());
		markerEntity.setProperty("content", marker.getContent());
		datastore.put(markerEntity);
	}


}



