#!/bin/bash
USER=$(whoami)
CONFIGFILE="/home/$USER/.local/share/robocode/1.9.3.9/config/robocode.properties"
echo "Dieses Skript erstellt dir automatisch deine Konfiguration für Intellij, damit du gleich los coden kannst!"
echo "Überleg dir dazu einen Namen für deinen Roboter (am besten ohne Leerzeichen)."
read -p "Name:" NAME
echo "Eine Sekunde... Noch kurz robocode initialisieren"
robocode &> /dev/null &
sleep 3
echo "Bin dran :)"
while [ ! -f "$CONFIGFILE" ]
do
    echo "Robocode brauch wohl ein bisschen..."
    sleep 1
done;
echo "Du kannst robocode nun schließen! Oder ich mach das für dich"
killall java >/dev/null
OUTPATH="$(pwd)/out/production/$NAME"
sed -i "s/placeholder/$USER/g" src/infovk/placeholder/MyFirstBehavior.java
sed -i "s/placeholder/$USER/g" src/infovk/placeholder/MyFirstRobot.java
sed -i "s/placeholder/$USER/g" src/infovk/placeholder/SimpleRobot.java
sed -i "s/TemplateBot/$NAME/g" .idea/modules.xml
sed -i "s/TemplateBot/$NAME/g" .idea/workspace.xml
if test -f "$CONFIGFILE"; then
    echo "robocode.options.development.path=$OUTPATH" >> $CONFIGFILE
fi;
mv src/infovk/placeholder src/infovk/$USER
mv TemplateBot.iml "$NAME.iml"
echo "Jetzt kannst du den Projektordner noch schnell umbennen und dann kann's schon losgehen."
sleep 1