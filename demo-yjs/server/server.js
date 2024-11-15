#!/usr/bin/env node
require('dotenv').config();
const WebSocket = require('ws');
const http = require('http');
const number = require('lib0/number');
const wss = new WebSocket.Server({ noServer: true });
const wsUtils = require('./utils');

const host = process.env.HOST || 'localhost';
const port = number.parseInt(process.env.PORT || '1234');

const server = http.createServer((_request, response) => {
    response.writeHead(200, { 'Content-Type': 'application/json' });
    response.end(JSON.stringify({ status: 'ok' }));
});

wss.on('connection', (ws, req) => {
    console.log('收到websocket连接请求...');
    // 从WebsocketProvider的params中获取token
    const token = req.url.split('?')[1].split('=')[1];
    console.log('Received token:', token);
    if (!token) {
        ws.close(4001, 'Missing token');
        return;
    }
    if (!verifyToken(token)) {
        ws.close(4002, 'Invalid token');
        return;
    }

    wsUtils.setupWSConnection(ws, req);

    ws.on("close", (conn) => {
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
