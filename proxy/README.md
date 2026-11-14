# CalmLoop AI relay

A tiny, stateless [Cloudflare Worker](https://developers.cloudflare.com/workers/) that sits between
the app and Google's Gemini API so the API key never ships inside the app.

- Stores nothing, no database, no accounts.
- Never logs request or response bodies (observability is off in `wrangler.toml`).
- Builds the prompts itself, so it can't be used as a general-purpose Gemini proxy.
- Rejects oversized input and returns generic errors.

## Endpoints

| Path | Body | Returns |
| --- | --- | --- |
| `POST /v1/reframe` | `{ "transcription": "..." }` (≤ 6,000 chars) | `{ activating_event, beliefs, consequences, ai_synopsis }` |
| `POST /v1/search` | `{ "query": "...", "entries": [{ "id": 1, "text": "..." }] }` (≤ 100 entries) | `{ matching_ids, pattern_insight }` |

## Deploy (about 10 minutes, free tier is enough to start)

1. Create a Gemini API key at <https://aistudio.google.com/apikey>. Use a **paid** (billing-enabled)
   project for production: on the free tier Google may use prompts to improve its products, which
   would contradict the privacy policy.
2. `npm install -g wrangler` and `wrangler login`.
3. From this folder: `wrangler secret put GEMINI_API_KEY` (paste the key when asked).
4. Check `GEMINI_MODEL` in `wrangler.toml` is a model Google currently offers, then `wrangler deploy`.
5. Put the printed URL in the repo's `.env` as `AI_PROXY_URL=https://calmloop-ai.<you>.workers.dev`
   and rebuild the app.
6. In the Cloudflare dashboard, add a rate-limiting rule for the worker (e.g. 30 requests/minute per IP)
   to cap abuse and cost.

## Test

```
node --test proxy/worker.test.mjs
```
