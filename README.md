# 待办提醒 Android 应用

## 功能特性

1. **支持录入待办提醒** - 用户可以添加带有标题和描述的待办任务
2. **单次任务完成提醒之后，进入已完成列表中** - 完成的任务会自动移到已完成列表
3. **所有待办任务持久化** - 使用 Room 数据库进行数据持久化存储
4. **支持三种提醒模式**：
   - 单次提醒模式：在指定日期和时间提醒一次
   - 重复 + 按时间点模式：在指定日期范围内，每天在指定时间点（如 8:35, 9:35）提醒
   - 重复 + 轮询模式：在指定日期和时间范围内，每隔 X 小时/Y 分钟提醒一次
5. **配置界面要有美感** - 使用 Material Design 3 设计语言，界面美观现代
6. **设置一个精美的app图标** - 自定义设计的应用图标，采用紫色主题色

## 技术栈

- **编程语言**: Kotlin
- **UI 框架**: Material Design 3
- **数据库**: Room (SQLite)
- **架构**: MVVM (Model-View-ViewModel)
- **异步处理**: Kotlin Coroutines
- **提醒功能**: AlarmManager + BroadcastReceiver

## 项目结构

```
TodoReminderApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/todoreminder/
│   │   │   ├── TodoReminderApplication.kt
│   │   │   ├── data/
│   │   │   │   ├── TodoEntity.kt
│   │   │   │   ├── TodoDao.kt
│   │   │   │   ├── TodoDatabase.kt
│   │   │   │   ├── DateConverter.kt
│   │   │   │   └── TodoRepository.kt
│   │   │   ├── ui/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── AddEditTodoActivity.kt
│   │   │   │   ├── CompletedTasksActivity.kt
│   │   │   │   ├── TodoViewModel.kt
│   │   │   │   ├── TodoAdapter.kt
│   │   │   │   └── CompletedTodoAdapter.kt
│   │   │   ├── receiver/
│   │   │   │   ├── ReminderReceiver.kt
│   │   │   │   └── BootReceiver.kt
│   │   │   └── worker/
│   │   │       └── AlarmScheduler.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   ├── mipmap/
│   │   │   └── menu/
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── gradle/
└── settings.gradle.kts
```

## 使用说明

### 1. 在 Android Studio 中打开项目

1. 将 `TodoReminderApp` 文件夹复制到你的工作目录
2. 打开 Android Studio
3. 选择 "Open an Existing Project"
4. 导航到 `TodoReminderApp` 文件夹并打开

### 2. 配置 Gradle

项目使用 Gradle 构建系统。首次打开时，Android Studio 会自动下载所需的 Gradle 版本和依赖项。

### 3. 运行应用

1. 连接 Android 设备或启动模拟器
2. 点击 Android Studio 中的 "Run" 按钮
3. 选择目标设备
4. 应用将安装并运行在设备上

## 功能使用指南

### 添加待办任务

1. 在主界面点击右下角的 "+" 按钮
2. 输入任务标题（必填）和描述（可选）
3. 选择提醒类型：
   - **单次提醒**：选择具体的日期和时间
   - **重复 + 按时间点**：选择日期范围，然后添加多个提醒时间点
   - **重复 + 轮询**：选择日期范围和时间窗口，设置轮询间隔
4. 点击 "保存" 按钮

### 完成任务

- 在主界面，点击任务卡片上的 "完成" 按钮
- 任务会移动到 "已完成任务" 列表

### 查看已完成任务

- 点击主界面右上角的菜单按钮
- 选择 "已完成任务"

### 删除任务

- 在主界面或已完成任务界面，点击任务卡片上的 "删除" 按钮
- 确认删除操作

## 权限说明

应用需要以下权限：

- `POST_NOTIFICATIONS`：发送提醒通知
- `SCHEDULE_EXACT_ALARM`：设置精确闹钟
- `RECEIVE_BOOT_COMPLETED`：设备重启后重新调度提醒

## 注意事项

1. **提醒功能**：应用使用 AlarmManager 来调度提醒。为了确保提醒能够正常触发，请在设备的设置中允许应用发送通知和设置闹钟。

2. **电池优化**：某些设备可能会限制后台应用的活动。为了确保提醒功能正常工作，请将应用添加到电池优化的白名单中。

3. **数据备份**：应用使用 Room 数据库存储数据。如果需要备份数据，可以导出数据库文件。

## 定制化

### 修改应用图标

应用的图标文件位于 `app/src/main/res/mipmap-anydpi-v26/` 目录。你可以替换这些文件来使用自定义图标。

### 修改主题颜色

应用的主题颜色定义在 `app/src/main/res/values/colors.xml` 文件中。你可以修改这些颜色值来更改应用的主题。

### 修改提醒通知

提醒通知的配置在 `receiver/ReminderReceiver.kt` 文件中。你可以修改通知的样式、声音等。

## 已知问题

1. **重复提醒的精确调度**：对于重复提醒模式，当前的实现使用 AlarmManager 来调度闹钟。在某些情况下（如设备重启、系统时间变更等），可能需要重新调度闹钟。

2. **大量提醒的管理**：如果用户设置了大量的重复提醒，可能会影响设备的性能。建议合理设置提醒数量。

## 未来改进

- [ ] 添加任务优先级功能
- [ ] 添加任务分类/标签功能
- [ ] 添加任务搜索功能
- [ ] 添加数据导出/导入功能
- [ ] 添加桌面小部件
- [ ] 添加云同步功能

## 许可证

本项目仅供学习和参考使用。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件

---

**祝你使用愉快！**
