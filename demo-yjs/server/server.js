#!/usr/bin/env node

const WebSocket = require('ws')
const http = require('http')
const number = require('lib0/number')
const wss = new WebSocket.Server({ noServer: true })
const setupWSConnection = require('y-websocket/bin/utils').setupWSConnection

const host = process.env.HOST || 'localhost'
const port = number.parseInt(process.env.PORT || '1234')

const server = http.createServer((_request, response) => {
    response.writeHead(200, { 'Content-Type': 'application/json' })
    response.end(JSON.stringify({ status: 'ok' }))
})

wss.on('connection', (ws, req) => {
    // 处理传入的 token 验证
    console.log('收到websocket连接请求...');
    console.log('请求的host:', req.headers.host)
    console.log('请求的url:', req.url)
    const urlParams = new URL(req.url, `http://${req.headers.host}`);
    console.log('请求的参数:', urlParams.searchParams)
    let token = urlParams.searchParams.get('token');
    if (!token) {
        ws.close(4001, 'Missing token');
        return;
    }
    // token的值可能是 123/456 我只想要123
    token = token.includes('/') ? token.split('/')[0]:token
    console.log('Received token:', token);
    if (!verifyToken(token)) {
        ws.close(4002, 'Invalid token');
        return;
    }

    // 调用原始的 setupWSConnection 函数来处理 WebSocket 连接
    setupWSConnection(ws, req)
});

// 简单的 token 验证逻辑 (实际项目中应结合数据库或其它方式)
function verifyToken(token) {
    // 假设固定 token 为 'VALID_TOKEN'
    return token === 'VALID_TOKEN';
}


server.on('upgrade', (request, socket, head) => {
    // You may check auth of request here..
    // Call `wss.HandleUpgrade` *after* you checked whether the client has access
    // (e.g. by checking cookies, or url parameters).
    // See https://github.com/websockets/ws#client-authentication
    wss.handleUpgrade(request, socket, head, /** @param {any} ws */ ws => {
        wss.emit('connection', ws, request)
    })
})

server.listen(port, host, () => {
    console.log(`running at '${host}' on port ${port}`)
})
