<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="10">
        <el-input
            v-model="room"
            style="width: 240px"
            placeholder="请输入房间号"
            clearable
        />
        <el-input
            v-model="token"
            style="width: 150px"
            placeholder="token"
            clearable
        />
        <el-button type="primary" @click="joinRoom">加入房间</el-button>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="100">
        当前用户：{{ userName }}
      </el-col>
    </el-row>
    <!-- 编辑器 -->
    <el-row>
      <el-col :span="24">
        <el-input v-model="editorText" type="textarea" :rows="10" placeholder="Start typing..."></el-input>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="3">
        <el-button :plain="true" @click="open">Show message</el-button>
      </el-col>
      <el-col :span="3">
        <el-button :plain="true" @click="open">Show message</el-button>
      </el-col>
      <el-col :span="3">
        <el-button :plain="true" @click="open">Show message</el-button>
      </el-col>
      <el-col :span="3">
        <el-button :plain="true" @click="open">Show message</el-button>
      </el-col>
    </el-row>

    <!-- 用户列表和光标同步 -->
    <el-row>
      <!-- 用户列表 -->
      <el-col :span="12">
        <h3>参与用户</h3>
        <el-table :data="users" style="width: 100%">
          <el-table-column prop="name" label="用户名" width="180" />
        </el-table>
      </el-col>

      <!-- 光标同步 -->
      <el-col :span="12">
        <h3>光标</h3>
        <div v-for="cursor in cursors" :key="cursor.id" :style="cursor.style">{{ cursor.name }}'s Cursor</div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import * as Y from 'yjs';
import { WebsocketProvider } from 'y-websocket';
import { ElMessage } from 'element-plus'
export default {
  name: 'CollaborativeEditor',
  data() {
    return {
      room: 'test',
      userName: '',
      token: 'VALID_TOKEN',
      doc: null,
      provider: null,
      users: [],
    };
  },
  methods: {
    joinRoom() {
      if (!this.room) {
        ElMessage.error('请输入房间号')
        return;
      }
      // 加入房间
      this.doc = new Y.Doc();
      this.provider = new WebsocketProvider(
          `ws://localhost:1234?token=${this.token}`,
          this.room,
          this.doc
      )
      // 设置用户的初始状态
      this.provider.awareness.setLocalStateField('user', {
        name: this.userName || this.getRandomString('User-') ,
      });

      // 连接状态
      this.provider.on('status', ({ status }) => {
        if (status === 'connected') {
          ElMessage.success('已连接到WebSocket')
          // 当前用户
          this.userName = this.provider.awareness.getLocalState().user.name;
        } else if (status === 'disconnected') {
          ElMessage.error('已断开与WebSocket的连接')
          this.userName = ''
        }
      });

      // 监听`awareness`的变化
      this.provider.awareness.on('change', () => {
        const awarenessStates = Array.from(this.provider.awareness.getStates().values());
        this.users = awarenessStates.map(state => state.user); // 更新用户列表
        console.log(this.users);
      });


      // 监听 WebSocket 关闭事件以获取关闭代码
      this.provider.on('connection-close', (event) => {
        const code = event.code;
        const reason = event.reason;
        ElMessage(`连接被服务端关闭 code: ${code}, reason: ${reason}`)

        if (code === 4001) {
          ElMessage.error('客户端缺少token，验证失败,不再重新建立连接')
          this.provider.disconnect();
        } else if (code === 4002) {
          ElMessage.error('客户端token无效，验证失败,不再重新建立连接')
          this.provider.disconnect();
        }
      });


    },
    // 生成16为随机字符串数据,包含大小写字母以及数字
    getRandomString(prefixes) {
      const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      let result = '';
      for (let i = 0; i < 16; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
      }
      return prefixes + result;
    }
  }
};
</script>
<style>
.el-row {
  margin-bottom: 20px;
}
.el-row:last-child {
  margin-bottom: 0;
}
.el-col {
  border-radius: 4px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
</style>