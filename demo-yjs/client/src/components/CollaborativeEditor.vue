<template>
  <div>
    <!-- 房间输入和用户信息 -->
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
        <!-- LogicFlow 添加节点 -->
        <el-button class="add" @click="addNode">添加Node</el-button>
        <el-drawer v-model="visible" title="添加节点信息">
          <el-form :model="form" style="max-width: 460px">
            <el-form-item label="节点类型">
              <el-select v-model="form.type" class="m-2" placeholder="Select">
                <el-option label="矩形" value="rect" />
                <el-option label="圆形" value="circle" />
                <el-option label="椭圆" value="ellipse" />
                <el-option label="多边形" value="polygon" />
              </el-select>
            </el-form-item>
            <el-form-item label="节点X坐标">
              <el-input v-model="form.x" />
            </el-form-item>
            <el-form-item label="节点Y坐标">
              <el-input v-model="form.y" />
            </el-form-item>
            <el-form-item label="节点ID">
              <el-input v-model="form.id" />
            </el-form-item>
            <el-form-item label="节点文本">
              <el-input v-model="form.text" />
            </el-form-item>
            <el-form-item label="">
              <el-button @click="comfirm" type="primary">确认</el-button>
            </el-form-item>
          </el-form>
        </el-drawer>

        <div class="box"></div>
      </el-col>
    </el-row>

    <!-- 用户列表 -->
    <el-row>
      <el-col :span="12">
        <h3>参与用户</h3>
        <el-table :data="users" style="width: 100%">
          <el-table-column prop="name" label="用户名" width="180" />
        </el-table>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import * as Y from 'yjs';
import { WebsocketProvider } from 'y-websocket';
import LogicFlow from '@logicflow/core';
import {ElMessage} from 'element-plus';

export default {
  name: 'CollaborativeEditor',
  setup() {
    // 房间和用户信息
    const room = ref('test');
    const token = ref('VALID_TOKEN');
    const userName = ref('');
    const users = ref([]);
    const editorText = ref('');

    // Yjs相关
    const doc = ref(null);
    const provider = ref(null);

    // LogicFlow相关
    const lf = ref(null);
    const visible = ref(false);
    const form = reactive({
      type: 'rect',
      x: '',
      y: '',
      id: '',
      text: ''
    });

    // 加入房间逻辑
    function joinRoom() {
      if (!room.value) {
        ElMessage.error('请输入房间号');
        return;
      }
      // 创建Yjs文档和连接WebSocket
      doc.value = new Y.Doc();
      provider.value = new WebsocketProvider(
          `ws://localhost:1234?token=${token.value}`,
          room.value,
          doc.value
      );
      provider.value.awareness.setLocalStateField('user', {name: userName.value || getRandomString('User-')});

      provider.value.on('status', ({status}) => {
        if (status === 'connected') {
          ElMessage.success('已连接到WebSocket');
          userName.value = provider.value.awareness.getLocalState().user.name;
        } else if (status === 'disconnected') {
          ElMessage.error('已断开与WebSocket的连接');
          userName.value = '';
        }
      });

      provider.value.awareness.on('change', () => {
        const awarenessStates = Array.from(provider.value.awareness.getStates().values());
        users.value = awarenessStates.map(state => state.user); // 更新用户列表
      });
    }

    // 生成随机字符串
    function getRandomString(prefix) {
      const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      let result = '';
      for (let i = 0; i < 16; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
      }
      return prefix + result;
    }

    // LogicFlow节点操作
    function addNode() {
      visible.value = true;
    }

    function comfirm() {
      form.id = Math.random().toString().split('.')[1];
      visible.value = false;
      lf.value.addNode(form); // 添加节点到LogicFlow
      // 同步节点到Yjs
      const yjs = new Y.Map();
      yjs.set('addNode', form);
      doc.value.getMap('nodes').set(form.id, yjs);
    }

    // 初始化LogicFlow
    onMounted(() => {
      lf.value = new LogicFlow({
        container: document.querySelector('.box'),
        grid: true
      });
      lf.value.render([]);
    });

    return {
      room,
      token,
      userName,
      users,
      editorText,
      visible,
      form,
      addNode,
      comfirm,
      joinRoom
    };
  }
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.box {
  height: calc(100vh - 50px);
  width: 100vw;
  overflow: hidden;
}

.add {
  left: 0;
  top: 0;
}
</style>

