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

## Use of Ranky

If you want to use the bot, go to https://discord.com/oauth2/authorize?client_id=1005188427634966629&permissions=2952866832&scope=bot and add it to a Discord server. After that, the bot will explain it's usage when joining and every time the /help command is executed.

## License

This project is licensed under the [GNU Affero General Public License v3.0](https://www.gnu.org/licenses/agpl-3.0.html) for the code.

All images, documentation, and other non-code content are licensed under the [Creative Commons Attribution 4.0 International License](https://creativecommons.org/licenses/by/4.0/).
