#!/bin/bash
cd ../client-jss
rm dist/* -R
npm install  
ng build --configuration recette

tar -czf deploy.tar.gz -C dist .
scp deploy.tar.gz jss@rec.jss.fr:/tmp/deploy.tar.gz

ssh -t jss@rec.jss.fr << 'EOF'
  rm -rf /tmp/deploy_unzipped
  mkdir -p /tmp/deploy_unzipped
  
  tar -xzf /tmp/deploy.tar.gz -C /tmp/deploy_unzipped
  
  sudo /usr/bin/systemctl stop ssr.service
  
  rm -R /appli/jss/*
  mv /tmp/deploy_unzipped/* /appli/jss/.
  
  chown -R jss:appli /appli/jss/
  sudo /usr/bin/systemctl start ssr.service
  
  rm /tmp/deploy.tar.gz
  rm -rf /tmp/deploy_unzipped
  exit
EOF

rm deploy.tar.gz