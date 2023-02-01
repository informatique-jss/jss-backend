cd ..
rm build/libs/*
gradle bootJar
ssh -t osiris@app-rec1.osiris.jss.fr 'sudo  /usr/bin/systemctl stop osiris.service;exit'
scp build/libs/workspace-osiris-*.jar osiris@app-rec1.osiris.jss.fr:/appli/osiris/osiris.jar
ssh -t osiris@app-rec1.osiris.jss.fr 'sudo  /usr/bin/systemctl start osiris.service;exit'
ssh -t osiris@app-rec2.osiris.jss.fr 'sudo  /usr/bin/systemctl stop osiris.service;exit'
scp build/libs/workspace-osiris-*.jar osiris@app-rec2.osiris.jss.fr:/appli/osiris/osiris.jar
ssh -t osiris@app-rec2.osiris.jss.fr 'sudo  /usr/bin/systemctl start osiris.service;exit'

cd client
ng build --configuration recette
ssh -t osiris@rec.osiris.jss.fr 'rm -R /appli/osiris/*;exit'
scp -r dist/osiris/* osiris@rec.osiris.jss.fr:/appli/osiris/.
ssh -t osiris@rec.osiris.jss.fr 'chown osiris:appli /appli/osiris/*;exit'
 