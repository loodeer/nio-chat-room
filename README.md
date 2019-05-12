
### Channel
- 双向性
- 非阻塞性
- 操作唯一性（buffer）

### Buffer
- 作用：读写 Channel 中的数据
- 本质：一块内存区域
- 属性：
     - Capacity: 容量
     - Position: 位置
     - Limit: 上限
     - Mark: 标记
 
 ### Selector
 - 作用：I/O就绪选择
 - 地位：NIO 网络编程的基础
 - SelectionKey
 
 ### NIO 编程实现步骤
1. 创建 Selector
2. 创建 ServerSocketChannel，并绑定监听端口
3. 将 Channel 设置为非阻塞模式
4. 将 Channel 注册到 Selector 上，监听连接事件
5. 循环调用 Selector 的 select 方法，检测就绪情况
6. 调用 selectedKeys 方法获取就绪 channel 集合
7. 判断就绪事件种类，调用业务处理方法
8. 根据业务需要决定是否再次注册监听事件，重复执行地散步操作
