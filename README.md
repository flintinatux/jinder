# Jinder - like Tinder, but for Java!

A Campfire API client library for Java. Heavily inspired by Tinder.

## Installation

Just add the [latest jinder-x.y.z.jar](https://github.com/flintinatux/jinder/tree/master/gen) file to your build path. Eclipse users will also want to set the "Source attachment" to its corresponding "-sources" jar to aid in development. 

Jinder uses the [google-http-java-client library](https://code.google.com/p/google-http-java-client/), so you'll also need to add those jars as necessary.

## Usage

To get started, create a new `Campfire` instance, request the `Room` list, and then play around:

```java
Campfire campfire = Campfire.new("subdomain", "username", "password");
Room room = campfire.rooms().get(0);
room.join();
room.speak("Jinder is my new best friend!");
room.speak("Goodbye.");
room.leave();
```

For a slightly longer example of how Jinder can make your life easier, go read [LiveTest.java](https://github.com/flintinatux/jinder/blob/master/test/com/madhackerdesigns/jinder/LiveTest.java).

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Added some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request