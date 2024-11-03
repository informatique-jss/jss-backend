cd ..
cd client-jss
npm install  
ng build --configuration recette
ssh -t jss@rec.jss.fr 'rm -R /appli/jss/*;exit'
scp -r dist/jss/browser/* jss@rec.jss.fr:/appli/jss/.
ssh -t jss@rec.jss.fr 'chown jss:appli /appli/jss/*;exit'