module.exports = {
  apps: [
    {
      name: 'ssr',
      script: '/appli/jss/server/server.mjs',
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
