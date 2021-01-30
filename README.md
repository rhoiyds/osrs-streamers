# OSRS Streamers  [![Plugin Installs](http://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/installs/plugin/osrs-streamers)](https://runelite.net/plugin-hub/Rhoiyds) [![Plugin Rank](http://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/rank/plugin/osrs-streamers)](https://runelite.net/plugin-hub)
_Highlight characters that are used by Twitch streamers, link to their streams, and show if they're live - all in real time!_

## Features

### Highlighting streamers
Streamers who are registered as a verified streamer by OSRS Streamers will have their character tile highlighted, as well as their Twitch URL displayed above their head.
\
\
![Highlighted streamer](resources/streamer.png?raw=true "Highlighted streamer")

### Right click -> Open Stream
For all verified characters of a streamer, a 'Twitch [Player name]' option will be added the to right click menu, which will take you directly to their Twitch stream.
\
\
![Menu](resources/menu.png?raw=true "menu")

### Highlighting streamers that are live
The 'Check if live' option must be turned on in the plug-in settings.
Additionally, you must [use the Twitch token generator](https://rhoiyds.github.io/osrs-streamers) to get a User Access token and add it into the 'Token' field of the plug-in settings.
Runescape characters of OSRS Streamers verified streamers that are currently being streamed on Twitch will now be highlighted in the iconic Twitch purple.
\
\
![Live streamer](resources/live.png?raw=true "Live streamer")

## Becoming a Verified Streamer
Streamers wishing to have their characters identified by the plug-in should fill out the following verification [Google form](https://docs.google.com/forms/d/e/1FAIpQLSeRW3-etHyTj1JaAwO1PoRbd_SQr1TulpEZMOo5cdYQxwt14A/viewform?usp=sf_link). A streamer can have multiple Runescape character names associated with their Twitch stream. If possible, for each character provide a separate link to a Twitch VOD of the streamer using this character (Not 100% necessary due to Twitch's current VOD situation - but it will help speed up the process). This is needed for proof of character ownership. You can edit your response if you ever need to add new character names/name changes for verification in the future. Please use the following format:

    Character name 1 (i.e WhiteCat213)
    (Link to video of streamer playing on WhiteCat213)

    Character name 2
    (Link to video of streamer playing on Character 2)
    
Additionally, the machine learning powered [OSRS Streamers Backend](https://rhoiyds.github.io/osrs-streamers-backend) project is crawling Twitch once every hour. If the bot detects a Twitch streamer playing with a character not verified by OSRS Streamers, it will be queued for manual review and likely added a few days later manually.

## Issues and feature requests
Please use the GitHub Issues tab to log any bugs you encounter. Additionally, if you have any ideas or improvements please also log it as a feature request in GitHub Issues - I’m open to any improvements.
