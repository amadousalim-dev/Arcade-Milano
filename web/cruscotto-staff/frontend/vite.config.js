import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// In sviluppo /api/... viene inoltrato al mock (o a un'API di test):
// /api/v1/edizioni/... diventa /v1/edizioni/...
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: process.env.API_PROXY_TARGET || 'http://localhost:8001',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
  test: { environment: 'node' },
});
