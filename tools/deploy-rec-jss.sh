cd ..
cd client-jss
rm dist/* -R
npm install  
ng build --configuration recette
ssh -t jss@rec.jss.fr 'sudo  /usr/bin/systemctl stop ssr.service;exit'
ssh -t jss@rec.jss.fr 'rm -R /appli/jss/*;exit'
scp -r dist/* jss@rec.jss.fr:/appli/jss/.
ssh -t jss@rec.jss.fr 'chown jss:appli /appli/jss/*;exit'
ssh -t jss@rec.jss.fr 'sudo  /usr/bin/systemctl start ssr.service;exit'