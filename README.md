# Extemp Cutter
A large, well-organized, and robustly-designed JavaFX program that lets High School Extemp teams programmatically find and save ("cut") articles from a variety of sources to the online Prepd Database through intelligent manipulation of the GUI. Due to the use of intelligent screen image detection, it is able to speed up cutting significantly, cutting one article in approximately 26 seconds (most of which is taken by the Prepd Chrome Extension in validating and saving the data).

The basic files in this program are:

`Main.java` - the entry point into the program. It sets up the GUI and lets the user make multiple-criteria selections in order to cut optimally.

`Cutter.java` - called from `Main.java` in order to interact with the cutter.

`Source.java` - the parent class that all `Source*.java` classes inherit from. Designed carefully to factor out as much code as possible and make maintaining and extending the program's capabilities as easy as possible.

`Source*.java` - each of the child classes of `Source.java` that each handle the unique responsibilies in order to cut articles from that source.

`Article.java` - encapsultes the article information and handles the tag detection for each article.

`Hack.java` - a custom class written specifically for the program to interact with and gather information from the GUI.
