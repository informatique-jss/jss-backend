#!/bin/bash
cd ../client-myjss
rm dist/* -R
npm install  
ng build --configuration production

tar -czf deploy.tar.gz -C dist .
scp deploy.tar.gz jss@my.jss.fr:/tmp/deploy.tar.gz

ssh -t myjss@my.jss.fr << 'EOF'
  rm -rf /tmp/deploy_unzipped
  mkdir -p /tmp/deploy_unzipped
  
  tar -xzf /tmp/deploy.tar.gz -C /tmp/deploy_unzipped
  
  sudo /usr/bin/systemctl stop ssr.service
  
  rm -R /appli/myjss/*
  mv /tmp/deploy_unzipped/* /appli/myjss/.
  
  chown -R myjss:appli /appli/myjss/
  sudo /usr/bin/systemctl start ssr.service
  
  rm /tmp/deploy.tar.gz
  rm -rf /tmp/deploy_unzipped
  exit
EOF

rm deploy.tar.gz