// Run with: node --test proxy/worker.test.mjs
import { test, beforeEach } from "node:test";
import assert from "node:assert/strict";
import worker from "./worker.js";

const env = { GEMINI_API_KEY: "test-key", GEMINI_MODEL: "test-model" };
let upstream;
let lastUpstreamRequest;

beforeEach(() => {
  upstream = { status: 200, body: {} };
  globalThis.fetch = async (url, init) => {
    lastUpstreamRequest = { url, init, body: JSON.parse(init.body) };
    return new Response(JSON.stringify(upstream.body), { status: upstream.status });
  };
});

const geminiReply = (obj) => ({ candidates: [{ content: { parts: [{ text: JSON.stringify(obj) }] } }] });
const post = (path, body) =>
  worker.fetch(new Request(`https://relay.test${path}`, { method: "POST", body: JSON.stringify(body) }), env);

test("reframe returns clean ABC fields and keeps the key out of the URL", async () => {
  upstream.body = geminiReply({ activating_event: "A", beliefs: "B", consequences: "C", ai_synopsis: "S" });
  const res = await post("/v1/reframe", { transcription: "I panicked" });
  assert.equal(res.status, 200);
  assert.deepEqual(await res.json(), { activating_event: "A", beliefs: "B", consequences: "C", ai_synopsis: "S" });
  assert.ok(!lastUpstreamRequest.url.includes("test-key"));
  assert.equal(lastUpstreamRequest.init.headers["x-goog-api-key"], "test-key");
  assert.ok(lastUpstreamRequest.url.includes("test-model"));
});

test("reframe rejects empty and oversized input without calling upstream", async () => {
  lastUpstreamRequest = undefined;
  assert.equal((await post("/v1/reframe", { transcription: "  " })).status, 400);
  assert.equal((await post("/v1/reframe", { transcription: "x".repeat(6001) })).status, 400);
  assert.equal(lastUpstreamRequest, undefined);
});

test("upstream failure returns a generic 502", async () => {
  upstream = { status: 500, body: { error: "secret details I panicked" } };
  const res = await post("/v1/reframe", { transcription: "I panicked" });
  assert.equal(res.status, 502);
  assert.deepEqual(await res.json(), { error: "upstream_error" });
});

test("search drops ids the model invented", async () => {
  upstream.body = geminiReply({ matching_ids: [1, 99, 1], pattern_insight: "Work stress." });
  const res = await post("/v1/search", { query: "work", entries: [{ id: 1, text: "work panic" }, { id: 2, text: "beach" }] });
  assert.deepEqual(await res.json(), { matching_ids: [1], pattern_insight: "Work stress." });
});

test("only POST is allowed and unknown paths 404", async () => {
  const get = await worker.fetch(new Request("https://relay.test/v1/reframe"), env);
  assert.equal(get.status, 405);
  assert.equal((await post("/v1/other", {})).status, 404);
});

test("missing key reports not configured", async () => {
  const res = await worker.fetch(
    new Request("https://relay.test/v1/reframe", { method: "POST", body: "{}" }),
    {},
  );
  assert.equal(res.status, 503);
});
