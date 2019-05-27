# TwitterApp

## Requirements :

- Android API 17+ -> Lollipop and later
- Android Studio 3.0+ preferred (as it has integrated Kotlin support)
- Kotlin 1.3+ (Currently using 1.3.31)

## App Details :

1. MainActivity : Provides two buttons to open views for *Section A* and *Section B*
2. MapsActivity : Shows a map with tweet markers on the map. if you click on the marker info window. it would open the map screen.
    - *BONUS* : for bonus part, MapsActivity pulls tweets every 5 seconds and it removes old tweets.
3. TweetDetailActivity : Shows the detil of the tweet with user information and tweet details.
4. TweetSearchActivity : Provides a search bar on top for the tweet details with image and video.
    - *QUERY* : Search with query inside the search bar
    - *HASHTAG* : Search with hashtag by start typeing with *#*

## External Libraries :

1. `de.hdodenhof:circleimageview:2.2.0` for circular image view.
2. `com.github.bumptech.glide:glide:4.9.0` for image viewing.
3. `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1` for kotlin coroutins.