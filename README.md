## Inspiration
Many people struggle with public speaking, and this app is perfect for self-reflection and improvement... but it doesn't stop there!

## What it does
SmoothTalk is an app that uses Natural Language Processing to transcribe speech to text and keeps statistics and on the frequency of keywords.  These keywords are user-inputted and can be anything from speech placeholder words like "um" and "like", to profanity and generally negative words. The user can access a log with a graph showing said keywords and their frequencies--and don't worry! None of the transcribed logs are kept in the database, just the results from the tallying of the words out of the transcribed content!

## How we built it
The app was built using Java in Android Studio, and the natural language processing was done by the PocketSphynx APIwhich enabled us to do continuous detection of custom words. Our data is stored and accessed via MySQL, and the UI was built from scratch.

## Challenges we ran into
None of us had ever developed a mobile app before, and to integrate databases from scratch and an API we were not familiar with was very daunting.

## Accomplishments that we're proud of
Being able to create a running, user-inputted list of words to look for was challenging and rewarding. Most apps that aim to recognize keywords in speech only have a set wordbank, and thus very limiting in the experience and practicality.

## What we learned
The experience getting our hands dirty in Android Studio, and, in general, starting an application from scratch, gave us a first-person understanding of the app-building pipeline.

## What's next for SmoothTalk
We would like to improve the app so that it runs in the background, like "hey Siri" and "OK Google". This would create a consistent and holistic graph compared to logging only when the app is on. The UI is to be improved as well, especially the results screen, where the results would be divided by date via calendar form for a smoother UX.
