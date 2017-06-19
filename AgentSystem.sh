#!/bin/bash

if [ $# -eq 1 ]
then
	java -cp bin adam.agent.AgentSystem $1
else
	echo "Sample usage: ./AgentSystem.sh <agent class>"
	exit 1
fi
