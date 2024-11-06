

<template>
  <header>

    <button @click="downloadNOProxy">直接下载</button>
    <button @click="downloadProxy">代理下载</button>
  </header>

  <main>
    <TheWelcome />
  </main>
</template>
<script >

import axios from "axios";

export default {
  name: 'App',
  components: {},
  methods:{
    downloadProxy(){

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
