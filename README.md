
Reproduces the Gradle 9/Android lint issue found here: 
https://issuetracker.google.com/issues/441381080?pli=1

Steps to reproduce: 
- Run `./gradlew lint`; see a successful build.
- Revert the HEAD commit, which swaps the build configuration back to groovy-based scripts.
- Run `./gradlew lint`; see lint failures