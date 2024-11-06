const { createServer } = require('y-websocket-auth/server')

const server = createServer({
    // accessToken is passed as { auth: ACCESS_TOKEN }
    // in the WebsocketProvider constructor on the client-side
    authenticate: async (accessToken) => {
        // do authentication
        return true
    }
})
const host = '127.0.0.1'
const port = 6666
server.listen(port, host, () => {
    console.log(`running at '${host}' on port ${port}`)
})