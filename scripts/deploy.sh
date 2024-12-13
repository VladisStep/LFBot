#!/usr/bin/env bash
echo 'Build files...'
./gradlew clean build
echo 'Copy files...'
scp -i $PUB_RSI_ID_PATH ./build/libs/LFBot-0.0.1-SNAPSHOT-plain.jar \
   root@$LF_BOT_SERVER_IP:/home/user
echo 'Restart server...'
ssh -i $PUB_RSI_ID_PATH -t root@$LF_BOT_SERVER_IP << EOF
pgrep java | xargs kill -9
nohup java -jar /home/user/LFBot-0.0.1-SNAPSHOT-plain.jar > /home/user/log.txt &
EOF
echo

