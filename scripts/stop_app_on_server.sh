#!/usr/bin/env bash
echo 'Start stopping...'
ssh -i $PUB_RSI_ID_PATH -t root@$LF_BOT_SERVER_IP << EOF
pgrep java | xargs kill -9
EOF
echo "Bye"