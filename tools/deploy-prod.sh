cd ..
rm build/libs/*
gradle bootJar -PexcludeHazelcast=true
ssh -t osiris@app1.osiris.jss.fr 'sudo  /usr/bin/systemctl stop osiris.service;exit'
scp build/libs/*-*.jar osiris@app1.osiris.jss.fr:/appli/osiris/osiris.jar
ssh -t osiris@app1.osiris.jss.fr 'sudo  /usr/bin/systemctl start osiris.service;exit'
ssh -t osiris@app2.osiris.jss.fr 'sudo  /usr/bin/systemctl stop osiris.service;exit'
scp build/libs/*-*.jar osiris@app2.osiris.jss.fr:/appli/osiris/osiris.jar
ssh -t osiris@app2.osiris.jss.fr 'sudo  /usr/bin/systemctl start osiris.service;exit'
ssh -t osiris@app3.osiris.jss.fr 'sudo  /usr/bin/systemctl stop osiris.service;exit'
scp build/libs/*-*.jar osiris@app3.osiris.jss.fr:/appli/osiris/osiris.jar
ssh -t osiris@app3.osiris.jss.fr 'sudo  /usr/bin/systemctl start osiris.service;exit'
ssh -t osiris@app4.osiris.jss.fr 'sudo  /usr/bin/systemctl stop osiris.service;exit'
scp build/libs/*-*.jar osiris@app4.osiris.jss.fr:/appli/osiris/osiris.jar
ssh -t osiris@app4.osiris.jss.fr 'sudo  /usr/bin/systemctl start osiris.service;exit'

ssh -t cache@cache1.osiris.jss.fr 'sudo  /usr/bin/systemctl stop cache.service;exit'
ssh -t cache@cache2.osiris.jss.fr 'sudo  /usr/bin/systemctl stop cache.service;exit'
ssh -t cache@cache1.osiris.jss.fr 'sudo  /usr/bin/systemctl start cache.service;exit'
ssh -t cache@cache2.osiris.jss.fr 'sudo  /usr/bin/systemctl start cache.service;exit'

cd client
npm install  --legacy-peer-deps
ng build --configuration production
ssh -t osiris@osiris.jss.fr 'rm /appli/osiris/*;exit'
scp -r dist/osiris/* osiris@osiris.jss.fr:/appli/osiris/.
ssh -t osiris@osiris.jss.fr 'chown osiris:appli /appli/osiris/*;exit'