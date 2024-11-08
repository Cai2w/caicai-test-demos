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
    <!-- 编辑器和用户列表横向布局 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <!-- 用户列表 -->
        <h3>参与用户</h3>
        <el-table :data="users" style="width: 100%">
          <el-table-column prop="name" label="用户名"/>
        </el-table>
      </el-col>

      <el-col :span="18">
        <!-- LogicFlow 添加节点 -->
        <el-button class="add" @click="addNode">添加Node</el-button>
        <el-drawer v-model="visible" title="添加节点信息">
          <el-form :model="form" style="max-width: 460px">
            <el-form-item label="节点ID">
              <el-input v-model="form.id" disabled />
            </el-form-item>
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
            <el-form-item label="节点文本">
              <el-input v-model="form.text" />
            </el-form-item>
            <el-form-item label="">
              <el-button @click="comfirm" type="primary">确认</el-button>
            </el-form-item>
          </el-form>
        </el-drawer>
        <!-- 编辑器容器 -->
        <div class="container" ref="container"></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as Y from 'yjs';
import { WebsocketProvider } from 'y-websocket';
import { ElMessage } from 'element-plus'
import LogicFlow from "@logicflow/core";
import "@logicflow/core/lib/style/index.css";
export default {
  name: 'CollaborativeEditor',
  data() {
    return {
      room: 'test',
      userName: '',
      token: 'VALID_TOKEN',
      doc: null,
      provider: null,
      ymap: null,
      users: [],
      visible: false,
      form: {
        type: 'rect',
        x: 100,
        y: 100,
        id: '',
        text: '',
      },
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
      this.doc.on('update', (update, origin) => {
        console.log('收到更新', update, origin);
      })
      // 连接状态
      this.provider.on('status', ({ status }) => {
        if (status === 'connected') {
          ElMessage.success('已连接到WebSocket')
          // 当前用户
          // 设置用户的初始状态
          this.provider.awareness.setLocalStateField('user', {
            name: this.userName || this.getRandomString('User-') ,
          });
          this.userName = this.provider.awareness.getLocalState().user.name;
          // 初始化map
          this.ymap = this.doc.getMap('canvas')
          // 监听 Yjs 数据的变化
          this.ymap.observe(({ transaction, changes }) => {
            console.log('收到Yjs数据变化', transaction, changes);
            if (!transaction.origin) return; // 没有 origin 表示的是本地发起
            changes.keys.forEach((change, key) => {
              console.log(change, key);
              // 直接调用 YjsHandle 方法
              const value = this.ymap.get(key)
              this.YjsHandle(this.lf, { change, key, value });
            });
          });
        } else if (status === 'disconnected') {
          ElMessage.error('已断开与WebSocket的连接')
          this.userName = ''
        }
      });

      // 监听`awareness`的变化
      this.provider.awareness.on('change', () => {
        const awarenessStates = Array.from(this.provider.awareness.getStates().values());
        this.users = awarenessStates.map(state => state.user); // 更新用户列表
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
    },
    addNode() {
      this.visible = true;
      this.form.id = Math.random().toString().split('.')[1]
    },
    comfirm() {
      // 确保 x 和 y 是数字
      const x = Number(this.form.x);
      const y = Number(this.form.y);

      // 格式化表单数据
      const node = {
        id: this.form.id || Math.random().toString().split('.')[1], // 如果没有ID则随机生成
        type: this.form.type,
        x: x,
        y: y,
        text: this.form.text || '',
      };

      this.visible = false;
      this.lf.addNode(node); // 直接调用 addNode 方法
      if (!this.provider || !this.doc) {
        ElMessage.warning('请先加入房间,否则数据无法同步')
        return;
      }
      // 使用事务来避免事件触发时数据冲突
      this.doc.transact(() => {
        // 更新 Yjs 数据
        this.ymap.set("addNode", node);
      });

      // 重置表单
      this.form = {
        type: 'rect',
        x: 100,
        y: 100,
        id: '',
        text: '',
      }
    },
    // 在当前组件内直接定义 YjsHandle 方法
    YjsHandle(lf, { change, key, value }) {
      console.log('YjsHandle', change, key, value);
      switch (key) {
        case 'addNode':
          lf.addNode(value);  // 添加节点到 LogicFlow
          break;
        case "nodeMove":
          let { x, y, id } = value;
          console.log(x, y, id );
          lf.graphModel.moveNode2Coordinate(id, x, y, true);
          break;
        default:
          break;
      }
    }
  },
  mounted() {
    this.lf = new LogicFlow({
      container: this.$refs.container,
      grid: true,
    });
    this.lf.render();
    // 优化：当画布上的元素发生变化时会触发history:change事件，可以统一处理。
    this.lf.on("node:drag", ({ data, e }) => {
      this.doc.transact(() => {
        let { x, y, id } = data;
        this.ymap.set("nodeMove", { x, y, id });
      });
    });
  },
};
</script>

<style>
.container {
  width: 100%;
  height: 500px;
  border: 2px solid #ccc; /* 为容器添加边框 */
  border-radius: 8px; /* 可选：为容器添加圆角 */
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); /* 可选：添加阴影提升立体感 */
}
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
.add {
  left: 0;
  top: 0;
}
</style>