#!/usr/bin/env node
require('dotenv').config();
const WebSocket = require('ws');
const http = require('http');
const number = require('lib0/number');
const wss = new WebSocket.Server({ noServer: true });
const wsUtils = require('y-websocket/bin/utils');

const host = process.env.HOST || 'localhost';
const port = number.parseInt(process.env.PORT || '1234');

const server = http.createServer((_request, response) => {
    response.writeHead(200, { 'Content-Type': 'application/json' });
    response.end(JSON.stringify({ status: 'ok' }));
});

wss.on('connection', (ws, req) => {
    console.log('收到websocket连接请求...');
    const urlParams = new URL(req.url, `http://${req.headers.host}`);
    let token = urlParams.searchParams.get('token');
    if (!token) {
        ws.close(4001, 'Missing token');
        return;
    }
    token = token.includes('/') ? token.split('/')[0] : token;
    console.log('Received token:', token);
    if (!verifyToken(token)) {
        ws.close(4002, 'Invalid token');
        return;
    }

    wsUtils.setupWSConnection(ws, req);
    var persistence = wsUtils.getPersistence();
    console.log("持久化：" + persistence);

    ws.on("close", (conn) => {
        console.log(conn);
        console.log("yjs 用户关闭连接");
    });
});

function verifyToken(token) {
    return token === 'VALID_TOKEN';
}

server.on('upgrade', (request, socket, head) => {
    wss.handleUpgrade(request, socket, head, ws => {
        wss.emit('connection', ws, request);
    });
});

server.listen(port, host, () => {
    console.log(`WebSocket server running at ws://${host}:${port}`);
});
