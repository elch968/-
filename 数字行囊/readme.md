# 数字行囊 - 订阅管理APP

一个专业的Android订阅管理应用，帮助您跟踪所有付费会员项目，并在到期前及时提醒续费。

## ✨ 主要功能

### 📝 订阅管理
- **添加订阅项目**：记录项目名称、网站、到期日期、价格等信息
- **分类管理**：支持流媒体、软件服务、云存储、VPN、音乐等多种分类
- **灵活编辑**：随时修改订阅信息
- **快速删除**：不需要的订阅可以轻松删除

### 🔐 安全保护
- **账号密码加密存储**：使用AES-256-GCM加密算法
- **安全密钥管理**：采用Android Keystore系统
- **加密传输**：所有敏感信息都经过加密处理
- **一键复制**：支持复制账号和密码，方便登录

### ⏰ 智能提醒
- **自定义提醒时间**：可设置提前1-30天提醒
- **精确定时提醒**：使用AlarmManager确保准时提醒
- **后台持久化**：即使应用关闭也能收到提醒
- **批量提醒管理**：自动管理所有订阅的提醒

### 📊 直观展示
- **时间线视图**：按到期日期排序显示
- **状态标识**：清晰区分正常、即将到期、已过期状态
- **即将到期卡片**：首页突出显示7天内到期的项目
- **详细信息页**：完整展示订阅的所有信息

## 🏗️ 技术架构

### 核心技术栈
- **开发语言**：Kotlin
- **UI框架**：Jetpack Compose + Material Design 3
- **架构模式**：MVVM + Repository Pattern
- **数据库**：Room Database
- **异步处理**：Kotlin Coroutines + Flow
- **依赖注入**：Manual DI
- **通知管理**：WorkManager + AlarmManager
- **加密存储**：Android Security Crypto Library

### 项目结构

```
com.digitalbackpack.subscription/
├── data/                          # 数据层
│   ├── model/                     # 数据模型
│   │   └── Subscription.kt        # 订阅实体
│   ├── dao/                       # 数据访问对象
│   │   └── SubscriptionDao.kt     # 订阅DAO
│   ├── database/                  # 数据库
│   │   └── AppDatabase.kt         # Room数据库
│   └── repository/                # 数据仓库
│       └── SubscriptionRepository.kt
├── ui/                            # UI层
│   ├── screens/                   # 界面
│   │   ├── home/                  # 主页
│   │   ├── add_edit/              # 添加/编辑
│   │   └── detail/                # 详情页
│   ├── navigation/                # 导航
│   ├── theme/                     # 主题
│   └── MainActivity.kt            # 主Activity
├── notification/                  # 通知模块
│   ├── ReminderReceiver.kt        # 通知接收器
│   ├── ReminderScheduler.kt       # 提醒调度器
│   ├── ReminderWorker.kt          # 后台Worker
│   └── ReminderSchedulerHelper.kt # 调度助手
├── utils/                         # 工具类
│   └── CryptoManager.kt           # 加密管理器
└── SubscriptionApp.kt             # 应用类
```

## 🔒 安全特性

### 数据加密
- **加密算法**：AES-256-GCM（行业标准）
- **密钥存储**：使用EncryptedSharedPreferences
- **数据隔离**：加密密钥与数据分离存储
- **自动生成**：首次启动自动生成安全密钥

### 权限说明
- `POST_NOTIFICATIONS`：发送到期提醒通知
- `SCHEDULE_EXACT_ALARM`：设置精确的提醒时间
- `USE_EXACT_ALARM`：Android 14+精确闹钟权限

## 📱 使用说明

### 添加订阅
1. 点击右下角的 **+** 按钮
2. 填写项目名称、账号、密码（必填）
3. 选择到期日期和分类
4. 设置提醒天数（默认提前1天）
5. 可选填写价格、网站URL、备注等信息
6. 点击"保存"

### 查看订阅详情
1. 在主页点击任意订阅卡片
2. 查看完整的订阅信息
3. 点击眼睛图标显示/隐藏密码
4. 点击复制图标复制账号或密码
5. 可以编辑或删除订阅

### 编辑订阅
1. 在详情页点击右上角编辑图标
2. 修改需要更新的信息
3. 点击"保存"完成更新
4. 提醒会自动更新

### 删除订阅
1. 在详情页点击右上角删除图标
2. 确认删除操作
3. 订阅及其提醒会被完全删除

## 🎨 界面设计

### 主页
- 顶部显示即将到期提醒卡片
- 订阅列表按到期日期排序
- 不同颜色标识订阅状态：
  - 🟢 绿色：正常（7天以上）
  - 🟠 橙色：即将到期（7天内）
  - 🔴 红色：已过期

### 添加/编辑页
- 清晰的表单布局
- 日期选择器
- 分类下拉菜单
- 密码显示/隐藏开关
- 实时输入验证

### 详情页
- 大标题显示项目名称
- 状态卡片突出显示到期状态
- 登录信息卡片（支持复制）
- 订阅信息卡片（价格、周期等）

## 🔔 提醒机制

### 双重保障
1. **AlarmManager**：为每个订阅设置精确闹钟
2. **WorkManager**：每天定期检查并更新提醒

### 提醒时机
- 根据"到期日期"和"提前提醒天数"计算
- 在指定时间发送系统通知
- 点击通知打开应用

### 智能管理
- 添加订阅时自动创建提醒
- 编辑订阅时自动更新提醒
- 删除订阅时自动取消提醒
- 应用启动时重新调度所有提醒

## 🚀 构建与运行

### 环境要求
- Android Studio Arctic Fox 或更高版本
- Kotlin 1.9.0+
- Android SDK 34
- 最低支持Android 7.0（API 24）

### 构建步骤
1. 克隆或下载项目到本地
2. 使用Android Studio打开项目
3. 等待Gradle同步完成
4. 连接Android设备或启动模拟器
5. 点击运行按钮 ▶️

### 依赖库
```gradle
// Jetpack Compose
androidx.compose.material3
androidx.activity:activity-compose

// Room数据库
androidx.room:room-runtime
androidx.room:room-ktx

// WorkManager
androidx.work:work-runtime-ktx

// Security加密
androidx.security:security-crypto

// Kotlin协程
kotlinx-coroutines-android
```

## 📄 许可证

本项目仅供学习和个人使用。

## 💡 未来计划

- [ ] 添加数据备份与恢复功能
- [ ] 支持导入/导出CSV
- [ ] 添加统计图表（月度/年度支出）
- [ ] 支持多语言（英文、日文等）
- [ ] 添加深色模式切换
- [ ] 支持指纹/面部识别锁定
- [ ] 添加搜索功能
- [ ] 支持自定义提醒铃声
- [ ] 添加桌面小部件
- [ ] 云同步功能

## 👨‍💻 开发者

遵车：廖小波

## 📞 反馈与支持

TEL:18985625686

---

**数字行囊** - 让订阅管理更简单、更安全！📦
