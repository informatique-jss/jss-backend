import angular from '@analogjs/vite-plugin-angular';
import { defineConfig } from 'vite';

export default defineConfig({
  plugins: [angular()],
  server: {
    host: true,
    port: 4202,
    strictPort: true,
    // autorise tous les hosts et param gtm_debug
    allowedHosts: 'all',
    fs: {
      strict: false
    }
  }
});
