# videolistingmvp
Android video listing with swipe view tabs based on mvp design pattern with complete functionalities including searching, sorting, caching etc which are listed below:

# Features
1. Auto Update of video listing
2. Folder Listing
3. SwipeViewTabs interface using view pager.
4. Drawer Options Also available.
5. Code to get thumbnails
6. disk and memory caching for bitmaps used.
7. share, delete and rename functionalities
8. 6 options of sorting availble.

![videotogif_2017 03 27_00 28 28](https://cloud.githubusercontent.com/assets/7812393/24334321/da24d4d8-1285-11e7-8a0f-8ced229b224f.gif)                                 ![videotogif_2017 03 27_00 30 13](https://cloud.githubusercontent.com/assets/7812393/24334340/2a5760b0-1286-11e7-8291-136867c2d0ee.gif)                                        ![videotogif_2017 03 27_00 32 34](https://cloud.githubusercontent.com/assets/7812393/24334341/2b43db20-1286-11e7-9816-f70c3de60e13.gif)                                            ![videotogif_2017 03 27_00 35 10](https://cloud.githubusercontent.com/assets/7812393/24334342/2c56a9d4-1286-11e7-9450-79923631cbd2.gif)


## MVP
The Video listing app is based on model view presenter pattern.

###### model
The model is responsible to provide list of videos as per the contract(through the interface) between the model and presenter.

###### presenter
The presenter has all the business logic and is also responsible for the communication between the model and the view such as the any video addition or deletion

###### view
The view is the layout view of the video listing.

any of three component are easy replacable, also any change in one component of the application does not impact any other component in the application due to clean separation of roles.

# For Detailed Explaination Check
<a href="https://medium.com/@nitinagg.nitkkr/android-mvp-for-beginners-25889c500443">android MVP explained for beginners</a>


## Visit Website
<a href="https://androidvideoplayer.com/">Video Slow Motion Zoom Player</a>
Player with Best Zoom And Pan Functionality.

## PlayStore link
<a href="https://play.google.com/store/apps/details?id=com.mn2square.slowmotionplayer">Best Android Video Player with Zoom And Pan</a>

## Important Note
Please Provide Read Storage Permission to the Application before Running
