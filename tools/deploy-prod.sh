cd .. 
cd client
npm install  --legacy-peer-deps
ng build --configuration production
ssh -t osiris@osiris.jss.fr 'rm -R /appli/osiris/*;exit'
scp -r dist/osiris/* osiris@osiris.jss.fr:/appli/osiris/.
ssh -t osiris@osiris.jss.fr 'chown osiris:appli /appli/osiris/*;exit'
 