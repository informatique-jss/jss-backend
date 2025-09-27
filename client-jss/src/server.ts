import {
  AngularNodeAppEngine,
  createNodeRequestHandler,
  isMainModule
} from '@angular/ssr/node';
import express from 'express';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const serverDistFolder = dirname(fileURLToPath(import.meta.url));
const browserDistFolder = resolve(serverDistFolder, '../browser');

const app = express();
const angularApp = new AngularNodeAppEngine();

interface CacheEntry {
  body: string;
  expiresAt: number;
}

const ssrCache: Record<string, CacheEntry> = {};
const CACHE_TTL = 300_000;

/**
 * Serve static files from /browser
 */
app.use(
  express.static(browserDistFolder, {
    maxAge: '1y',
    index: false,
    redirect: false,
  }),
);

/**
 * Handle all other requests by rendering the Angular application.
 */
app.use('/**', async (req, res, next) => {
  const cacheKey = req.originalUrl;
  const cached = ssrCache[cacheKey];

  if (cached && cached.expiresAt > Date.now()) {
    console.log(`Cache hit for ${cacheKey}`);
    res.send(cached.body);
    return;
  }

  (global as any).cookies = req.headers.cookie ?? '';
  try {
    const response = await angularApp.handle(req);

    if (!response || !response.body) {
      next();
      return;
    }

    // Convert html
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let html = '';
    let done = false;

    while (!done) {
      const { value, done: streamDone } = await reader.read();
      if (value) {
        html += decoder.decode(value, { stream: true });
      }
      done = streamDone;
    }
    html += decoder.decode(); // final flush

    // Put in cache
    if (response.ok) {
      ssrCache[cacheKey] = {
        body: html,
        expiresAt: Date.now() + CACHE_TTL,
      };
    }

    // Send to client
    res.status(200);
    for (const [key, value] of Object.entries(response.headers)) {
      res.setHeader(key, value as string);
    }
    res.send(html);

  } catch (err) {
    next(err);
  }
});

/**
 * Start the server if this module is the main entry point.
 * The server listens on the port defined by the `PORT` environment variable, or defaults to 4000.
 */
if (isMainModule(import.meta.url) || process.env['PM2']) {
  const port = process.env['PORT'] ? parseInt(process.env['PORT'], 10) : 4000;
  app.listen(port, () => {
    console.log(`Node Express server listening on http://localhost:${port}`);
  });
}

/**
 * The request handler used by the Angular CLI (dev-server and during build).
 */
export const reqHandler = createNodeRequestHandler(app);
