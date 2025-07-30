cd ..
cd client-jss
rm dist/* -R
npm install  
ng build --configuration production
ssh -t jss@beta.jss.fr 'sudo  /usr/bin/systemctl stop ssr.service;exit'
ssh -t jss@beta.jss.fr 'rm -R /appli/jss/*;exit'
scp -r dist/* jss@beta.jss.fr:/appli/jss/.
ssh -t jss@beta.jss.fr 'chown jss:appli /appli/jss/*;exit'
ssh -t jss@beta.jss.fr 'sudo  /usr/bin/systemctl start ssr.service;exit'