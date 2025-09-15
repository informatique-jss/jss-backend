module.exports = {
  apps: [
    {
      name: 'ssr',
      script: '/appli/myjss/server/server.mjs',
      instances: 'max',
      exec_mode: 'cluster',
      env: {
        NODE_ENV: 'production',
        PM2: 'true',
        PORT: 4000
      }
    }
  ]
};
