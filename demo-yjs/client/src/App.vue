

<template>
  <header>

    <button @click="connect">连接</button>
    <button @click="mapTest">Map测试</button>
  </header>

  <main>
    <TheWelcome />
  </main>
</template>
<script >

import axios from "axios";
import * as Y from 'yjs';
import { WebsocketProvider } from 'y-websocket';
export default {
  name: 'App',
  components: {},
  methods:{
    connect(){
      const doc = new Y.Doc();
      const wsProvider = new WebsocketProvider(
          'ws://127.0.0.1:1234?token=VALID_TOKEN',
          'my-roomname',
          doc
      );

      let retryCount = 0;
      const maxRetries = 3;

      // 监听 WebSocket 关闭事件以获取关闭代码
      wsProvider.on('connection-close', (event) => {
        const code = event.code;
        const reason = event.reason;
        alert(`连接关闭 code: ${code}, reason: ${reason}`);
        console.log(`Connection closed with code: ${code}, reason: ${reason}`);

        if (code === 4001) {
          console.error('Token missing, not attempting to reconnect.');
          wsProvider.disconnect();
        } else if (code === 4002) {
          console.error('Invalid token, not attempting to reconnect.');
          wsProvider.disconnect();
        }

        // if (retryCount < maxRetries) {
        //   retryCount++;
        //   console.log(`Retrying connection attempt ${retryCount}...`);
        //   setTimeout(() => {
        //     wsProvider.connect();
        //   }, 5000); // Retry after 5 seconds
        // } else {
        //   console.error('Max retries reached, not attempting to reconnect.');
        //   wsProvider.disconnect();
        // }
      });

    },
    mapTest(){

    }
  }
}
</script>
<style scoped>
header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}
</style>
