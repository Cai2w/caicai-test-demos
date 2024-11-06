

<template>
  <header>

    <button @click="connect">连接</button>
    <button @click="downloadProxy">代理下载</button>
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
      const doc = new Y.Doc()
      const wsProvider = new WebsocketProvider(
          'ws://127.0.0.1:1234',
          'my-roomname',
          doc
      )

      wsProvider.on('status', event => {
        console.log(event.status) // logs "connected" or "disconnected"
      })
    },
    downloadNOProxy(){
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
