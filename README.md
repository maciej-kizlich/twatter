# Running the app:
- To run, simply go to the root app folder and invoke:

  _mvn spring-boot:run_
 
 - by default, application listens on port 8080

# Posting a new Twatt:
- resource URL:
  - POST /twatts/post

- request parameters:
  - content - content of the Twatt, must not be longer than 140 characters
  - username - name of the user to post Twatt on behalf of. If user with
given username does not exists, it’s being created.

- example query:
  - POST /twatts/post?content=first%20post&username=user1

# Listing all Twatts posted by user:

- resource URL:
  - GET /twatts

- request parameters:
  - username - name of the user to get all Twatts from

- example query:
  - GET /twatts?username=user1

# Listing all Twatts posted by followed users:

- resource URL:
  - GET /twatts/{username}/followeesTwatts

- request parameters: 
  - username - name of the user to get all followees Twatts from

- example query:
  - GET /twatts/user1/followeesTwatts

# Following a user:

- resource URL:
  - GET /users/{follower}

- request parameters:
  - follower - name of the user to follow another user
  - followee - user to be followed

- example query:
  - GET /users/user1?followee=user2
