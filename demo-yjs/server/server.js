const http = require('http');
const express = require('express');
const WebSocket = require('ws');
const { setupWSConnection } = require('y-websocket/bin/utils');

const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

// 设置WebSocket连接和协同功能
wss.on('connection', (ws, req) => {
    const docName = req.url.slice(1).split('?')[0];// 从URL中提取文档名称
    console.log(`New connection to ${docName}`);
    setupWSConnection(ws, req, {
        docName
    });
});

// 设置服务器端口号
const port = process.env.PORT || 1234;

server.listen(port, () => {
    console.log(`WebSocket Server is running on port ${port}`);
});
