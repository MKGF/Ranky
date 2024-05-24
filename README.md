# Ranky

Ranky is a bot to create custom League of Legend rankings to compete with other people while playing
soloQ/duoQ. This application is inspired in the SoloQChallenge done by twitch streamer ElmiilloR and
pretends to extend the possibility to create custom challenges in smaller communities.

## Compiling the application

mvn clean package

## Launching the application

java -jar Ranky.jar

Currently, secrets are handled in profile based properties files. By default, the application will
use "secret" profile, meaning an application-secret.properties file is expected to exist in
src/main/resources/config. However, nothing can be found in the repo. You need to create it yourself
with the necessary api keys in order to run it locally. Ask the contributors for the keys in case of
need.

## Deploying the application

The deployment is automatically handled by a set of GitHub actions that will:

    1-Prepare the application-secret.properties file inside the jar file.
    2-Compile the project in a jar file named Ranky.
    3-Deploy it in AWS.