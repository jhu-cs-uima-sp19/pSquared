# pSquared
UIMA Team N

Existing usernames & passwords:
Username: talker@jhu.edu		Password: joanne
Username: listener@jhu.edu		Password: joanne
Username: counselor@jhu.edu 	Password: joanne	

General State of the App
**A more comprehensive evaluation of our progress is available on our Github project board**

In general, we have completed all of our screens and most of our aesthetic design (with the exception of the chat feature design) and have been working on the functionality. 

Our main activity begins with the login / register screen. Register will allow the user to input a username and password that will be updated on our Firebase database. Once the user has created an account, they can log in (authentication done by Firebase).

Navigation has been completed. Screens such as the About page or Settings page are very standard Android pages. The About page is finished - its purpose is to inform users about the app. The Settings page has a few updates that need to be implemented in the second sprint but are not fundamental to the app.

The Home screens for both Talkers and Listeners feature large buttons. Once clicked, the buttons change accordingly to show that they will be connected as soon as an appropriate user is available.

In our database, our json has three components: a list of all users, a list of available talkers, a list of available listeners.

The json with the list of users is saved with the following key-value pairs= {key:username of user, value: 1 if (user is listener); 0 if (user is talker)}. The 0 or 1 value that determines if the user is a listener or talker can be changed from the counselor’s “Listeners” screen. 

The list of available talkers and listeners are updated in real-time depending on whether the user has pushed the “Talk” or “Listen” buttons or not. The users disappear from the respective lists in real-time when they re-click “Talk” or “Listen”.

By using this database, we can authenticate availability of Talkers and Listeners and connect them into a chatbox (created with Firebase). These two are free to chat about any pressing issues and stresses.

Stand-Up Meetings

March 28 - Meeting with Gaurav and Joanne

April 2 - Team meeting
Discussed how to allocate responsibilities
Made changes to UI design before submitting finalized design

April 3 - Team meeting
Finished all the xml files individually
Collectively connected all the screens to complete navigation

April 4 - Meeting with Gaurav
Configured all our git branches to the same repository
Configured Firebase to our app

April 8 - Team meeting
Looked up Chat SDK, Cloud Messaging, etc. and several other chat APIs
Used Firebase to instill a messaging feature into our app - several bugs present

April 11 - Meeting with Gaurav
Talked about how to implement messenger
Created individual databases do to 1-to-1 chats instead of large group chats

April 12 - Team meeting
Finished messaging feature
Ran into one bug wherein more than two chatters can enter a chat-box if the database has not updated quickly enough

April 14 - Team meeting
Fixed bugs regarding the Listen button not disabling after Talk button
Started recording in-class demo video
Submitted assignment
