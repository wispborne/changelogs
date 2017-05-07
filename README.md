[![BuddyBuild](https://dashboard.buddybuild.com/api/statusImage?appID=590f293f598da90001a1d73f&branch=master&build=latest)](https://dashboard.buddybuild.com/apps/590f293f598da90001a1d73f/build/latest?branch=master)
[![codecov](https://codecov.io/gh/davidwhitman/changelogs/branch/master/graph/badge.svg)](https://codecov.io/gh/davidwhitman/changelogs)

# Changelogs

## What is it?

Changelogs is an Android app dedicated to showing changelogs for any app in an easy-to-read list. 

[Version 1 is already available on the Play Store](https://play.google.com/store/apps/details?id=com.thunderclouddev.changelogs&hl=en) but was written when I was pretty new to Android development. It has issues and is due for a rewrite. Version 2 will be more stable, will find all apps, search more quickly, keep a history of changelogs, and more. 

This app is a **work in progress**, developed in my free time. 

There are currently two flavors; *Pure* and *Dist*. The Pure version does not contain Crashlytics or Stetho, whereas the Dist version does. If I add more "invasive" dependencies, such as ads, they will be added only to the Dist flavor. Think of it like the difference between Chromium and Chrome, except they're both open-source.

## Questions

### When will it be finished?

No idea.

### What will it cost?

The current plan is for the base app to stay free. I would like to introduce a paid Pro version as well and I will evaluate having opt-out ads in the free version.
The entire source code will stay available, though, so compiling the Pro version instead of paying would be an option.

### Why open source?

Version 1 was closed-source. The decision to open-source version 2 has been one that I've struggled with, but at the end of the day, I've benefitted greatly from open-source software and I believe in it as a movement. This is my way of giving back. 

Being able to put it on my resume is nice as well ;)

However, the fear that somebody will clone the app and I will lose control of it as an identity is real. Licensing it under the GPL should help, but ultimately it's out of my control. 

### Can I help?

At the moment, no. At some point down the road, I will need help with localization.
Feel free to report bugs and request features, but keep in mind that this is a project in its very early stages still. 

### Why Kotlin?

Ok, nobody actually asked that, but I will answer it anyways. Kotlin is incredible. Our Russian friends over at JetBrains created and maintain it, freeing Java developers everywhere from the verbose syntax, slow development, and meager standard library that is Java. I owe them many hours of nerdy joy. 

## License

Changelogs is licensed under the GPLv3, a copy of which is available [here](https://www.gnu.org/licenses/gpl-3.0.txt).

An abbreviated version is available [here](https://tldrlegal.com/license/gnu-general-public-license-v3-(gpl-3)#summary).

>You may copy, distribute and modify the software as long as you track changes/dates in source files. Any modifications to or software including (via compiler) GPL-licensed code **must also be made available under the GPL** along with build & install instructions.