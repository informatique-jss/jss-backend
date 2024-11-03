cd ..
cd client-myjss
npm install  
ng build --configuration recette
ssh -t myjss@rec.my.jss.fr 'rm -R /appli/myjss/*;exit'
scp -r dist/myjss/browser/* myjss@rec.my.jss.fr:/appli/myjss/.
ssh -t myjss@rec.my.jss.fr 'chown myjss:appli /appli/myjss/*;exit'