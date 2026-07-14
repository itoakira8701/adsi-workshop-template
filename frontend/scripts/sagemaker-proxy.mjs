import http from "node:http";
import httpProxy from "http-proxy";

const LISTEN_PORT = 3000;
const NEXT_PORT = 3001;
const RESTORE_PREFIX = "/codeeditor/default";

const proxy = httpProxy.createProxyServer({
  target: `http://127.0.0.1:${NEXT_PORT}`,
  changeOrigin: true,
});

proxy.on("error", (err, _req, res) => {
  console.error("[proxy] error:", err.message);
  if (res.writeHead) {
    res.writeHead(502, { "Content-Type": "text/plain" });
    res.end("Bad Gateway");
  }
});

const server = http.createServer((req, res) => {
  // code-server delivers: /absports/3000/...
  // Next.js expects: /codeeditor/default/absports/3000/...
  req.url = `${RESTORE_PREFIX}${req.url}`;
  proxy.web(req, res);
});

server.on("upgrade", (req, socket, head) => {
  req.url = `${RESTORE_PREFIX}${req.url}`;
  proxy.ws(req, socket, head);
});

server.listen(LISTEN_PORT, () => {
  console.log(`[sagemaker-proxy] :${LISTEN_PORT} → :${NEXT_PORT} (restore "${RESTORE_PREFIX}")`);
});
