#!/bin/sh -x
#
# Create a new Minecraft mod workspace
#

if [ $# -lt 1 ]
then
	echo "Usage: $0 NAME"
	exit 1
fi

# Config
SRC=ExampleMod
MCVER=1.12.2
FORGEVER=14.23.4.2705
MAPPINGVER=snapshot_20180531
GITHUBUSER=Stormwind99
BINTRAYUSER=stormwind

MDK=forge-${MCVER}-${FORGEVER}-mdk

NAME=$1

WORKSPACE=${NAME}Workspace

DIR=`dirname $0`
DIR=`realpath ${DIR}`

SRCLOWER=${SRC,,}
NAMELOWER=${NAME,,}

TEST=

$TEST cd $DIR
if [ -d "${WORKSPACE}" ]
then
	echo "${WORKSPACE} already exists!"
	exit 2
fi

# Copy template and change names
echo "Copy template and change names..."

# MAYBE TODO get from repo on Github

$TEST cp -a ${SRC}Workspace ${WORKSPACE}
$TEST mv ${WORKSPACE}/${SRC} ${WORKSPACE}/${NAME}
if [ -f private/private.properties ]
then
	$TEST cp private/private.properties ${WORKSPACE}/${NAME}
fi
$TEST cd ${WORKSPACE}/${NAME}

$TEST mv src/main/java/com/wumple/${SRCLOWER}/${SRC}.java  src/main/java/com/wumple/${SRCLOWER}/${NAME}.java
$TEST mv src/main/java/com/wumple/${SRCLOWER}  src/main/java/com/wumple/${NAMELOWER}

$TEST sed -e "s/${SRC}/${NAME}/g" -e "s/${SRCLOWER}/${NAMELOWER}/g" -i README.md update.json build.properties src/main/java/com/wumple/${NAMELOWER}/*.java
$TEST sed -e "s/GITHUBUSER/${GITHUBUSER}/g" \
	-e "s/MCVER/${MCVER}/g" \
	-e "s/FORGEVER/${FORGEVER}/g" \
	-e "s/MAPPINGVER/${MAPPINGVER}/g" \
	-i build.properties

# copy MDK files
echo "Copy MDK files..."

# MAYBE TODO automatically download MDK files if missing

$TEST cp -a ${DIR}/${MDK}/gradle* .
#$TEST cp -a ${DIR}/${MDK}/eclipse .

# set up git remote
echo "Set up git remote..."

$TEST git remote rm origin
$TEST git remote add origin git@github.com:${GITHUBUSER}/${NAME}.git

# run forgegradle steps
echo "Run forgegradle steps..."

./gradlew.bat setupDecompWorkspace
./gradlew.bat eclipse

# Done

echo
echo Now change more settings in build.properties and private.properties
echo
