
import { createApp } from 'vue'
import { createPinia } from 'pinia'
// svg插件需要配置代码
import 'virtual:svg-icons-register'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 引入自定义插件对象：注册整个项目全局组件
import globalComponent from '@/components/index.ts'
import '@/styles/index.scss'
import './permission'
import pinia from './stores'




const app = createApp(App)
app.use(ElementPlus)
app.use(createPinia())
app.use(router)
app.use(pinia)
app.use(globalComponent)
app.mount('#app')
