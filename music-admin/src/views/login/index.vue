<script setup lang="ts">
import Lock from "@iconify-icons/ri/lock-fill";
import User from "@iconify-icons/ri/user-3-fill";
import { bg, avatar, illustration} from "./utils/static";
import dayIcon from "@/assets/svg/day.svg?component";
import darkIcon from "@/assets/svg/dark.svg?component";
import {onMounted, reactive, Ref, ref, toRaw} from "vue";
import {useRenderIcon} from "@/components/Relcon/src/hooks.ts";
import { Warning } from '@element-plus/icons-vue'
import Identify from '@/components/VerifyCode/index.vue'
import useUserStore from "@/stores/modules/user.ts";
import { useRouter, useRoute } from 'vue-router'
import {ElNotification} from "element-plus";
import { getTime } from '@/utils/time'


let loginForms = ref()
const loginForm = reactive({
  username: "admin",
  password: "123456",
  verifyCode: '1234',
});
/** 用户名正则（用户名格式应为4-16位字母、数字、下划线、连字符的任意组合） */
const REGEXP_USERNAME = /^[a-zA-Z0-9_-]{4,16}$/;

let useStore = useUserStore()
let $router = useRouter()
let $route = useRoute()

const loading = ref(false);
const identifyCode = ref('1234')
const identifyCodes = ref('1234567890abcdefjhijklinopqrsduvwxyz')
// 重置验证码
const refreshCode = () => {
  identifyCode.value = ''
  makeCode(identifyCode, 4)
}
const makeCode = (o: Ref<any>, l: number) => {
  for (let i = 0; i < l; i++) {
    identifyCode.value +=
      identifyCodes.value[randomNum(0, identifyCodes.value.length)]
  }
}
// 页面加载时生成验证码
// onMounted(() => {
//   refreshCode()
// })
const randomNum = (min: number, max: number) => {
  return Math.floor(Math.random() * (max - min) + min)
}
const validatorUsername = (rule: any, value: any, callback: any) => {
  if (value === "") {
    callback(new Error("请输入用户名"));
  } else if (!REGEXP_USERNAME.test(value)) {
    callback(new Error("4-16位字母、数字、下划线、连字符的任意组合"));
  } else {
    callback();
  }
}
const validatorVerifyCode = (rule: any, value: any, callback: any) => {
  console.log(value, identifyCode.value)

  if (value.length === 0) {
    callback(new Error('请输入验证码'))
  } else if (value.length < 4) {
    callback(new Error('请输入正确的验证码'))
  } else if (identifyCode.value !== value) {
    callback(new Error('请输入正确的验证码'))
  } else if (identifyCode.value === value) {
    callback()
  }
}
const loginRules = {
  username: [
    {
      trigger: 'change',
      validator: validatorUsername,
    },
  ],
  verifyCode: [
    {
      trigger: 'blur',
      validator: validatorVerifyCode,
    },
  ],
}

const login=async ()=>{
  await loginForms.value.validate();
  loading.value = true;
  try {
    await useStore.userLogin(loginForm)
    let redirect: string = $route.query.redirect as string
    $router.push({ path: redirect || '/' })
    ElNotification({
      type: 'success',
      message: '登陆成功',
      title: `Hi, ${getTime()}好`,
    })
    loading.value = false
  } catch (error) {
    loading.value = false
    ElNotification({
      type: 'error',
      message: (error as Error).message,
    })
  }
}
</script>

<template>
  <div class="select-none">
    <img :src="bg" class="wave" />
    <div class="flex-c absolute right-5 top-3">
      <!-- 主题 -->
      <el-switch
        inline-prompt
        :active-icon="dayIcon"
        :inactive-icon="darkIcon"
        @change=""
      />
    </div>
    <div class="login-container">
      <div class="img">
        <component :is="toRaw(illustration)" />
      </div>
      <div class="login-box">
        <div class="login-form">
          <img src="../../assets/login/avatar.svg" class="avatar" />
            <h2 class="outline-none">主题</h2>
          <el-form :model="loginForm" ref="loginForms" :rules="loginRules" size="large">
              <el-form-item prop="username" style="margin-top: 10px">
                <el-input
                  v-model="loginForm.username"
                  clearable
                  placeholder="用户名"
                  :prefix-icon="useRenderIcon(User)"
                />
              </el-form-item>
              <el-form-item prop="password" style="margin-top: 10px">
                <el-input
                  v-model="loginForm.password"
                  clearable
                  show-password
                  placeholder="密码"
                  :prefix-icon="useRenderIcon(Lock)"
                />
              </el-form-item>
              <el-form-item prop="verifyCode" style="margin-top: 10px">
                <el-input
                  :prefix-icon="Warning"
                  show-password
                  v-model="loginForm.verifyCode"
                  placeholder="VerifyCode"
                  size="large"
                  maxlength="4"
                >
                  <template #append>
                    <Identify :identifyCode="identifyCode" @click="refreshCode" />
                  </template>
                </el-input>
              </el-form-item >
              <el-button
                class="w-full mt-4"
                size="default"
                type="primary"
                :loading="loading"
                @click="login()"
              >
                登录
              </el-button>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url("@/styles/login.css");
</style>

<style lang="scss" scoped>
:deep(.el-input-group__append, .el-input-group__prepend) {
  padding: 0;
}
.login_form {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  position: static;
  top: auto;
  left: auto;

  h1 {
    font-size: 32px;
    margin-bottom: 30px;
    margin-top: 0;
  }
}
</style>
