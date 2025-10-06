cd ..
cd client-osiris
rm dist/* -R
npm install  
ng build --configuration production
ssh -t osiris@osiris.jss.fr 'rm -R /appli/osiris2/*;exit'
scp -r dist/browser/* osiris@osiris.jss.fr:/appli/osiris2/.
ssh -t osiris@osiris.jss.fr 'chown osiris:appli /appli/osiris2/*;exit'