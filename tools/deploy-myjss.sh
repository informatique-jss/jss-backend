cd ..
cd client-myjss
npm install  
ng build --configuration production
ssh -t myjss@my.jss.fr 'sudo  /usr/bin/systemctl stop ssr.service;exit'
ssh -t myjss@my.jss.fr 'rm -R /appli/myjss/*;exit'
scp -r dist/* myjss@my.jss.fr:/appli/myjss/.
ssh -t myjss@my.jss.fr 'chown myjss:appli /appli/myjss/*;exit'
ssh -t myjss@my.jss.fr 'sudo  /usr/bin/systemctl start ssr.service;exit'