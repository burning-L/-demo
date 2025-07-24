import { createApp } from 'vue'
// svg插件需要配置代码
import 'virtual:svg-icons-register'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 引入自定义插件对象：注册整个项目全局组件
import globalComponent from '@/components/index.ts'
import 'element-plus/theme-chalk/el-notification.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/styles/index.scss'
import './permission'
import pinia from './stores'
import { isHasButton } from './directive/has'





const app = createApp(App)
isHasButton(app)
app.use(ElementPlus)
app.use(router)
app.use(pinia)
app.use(globalComponent)
app.mount('#app')
