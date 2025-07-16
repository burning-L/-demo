<!--<script setup lang="ts">-->
<!--import Lock from "@iconify-icons/ri/lock-fill";-->
<!--import User from "@iconify-icons/ri/user-3-fill";-->
<!--</script>-->

<!--<template>-->
<!--  <div class="select-none">-->
<!--    <img :src="bg" class="wave" />-->
<!--    <div class="flex-c absolute right-5 top-3">-->
<!--      &lt;!&ndash; 主题 &ndash;&gt;-->
<!--      <el-switch-->
<!--        v-model="dataTheme"-->
<!--        inline-prompt-->
<!--        :active-icon="dayIcon"-->
<!--        :inactive-icon="darkIcon"-->
<!--        @change="dataThemeChange"-->
<!--      />-->
<!--    </div>-->
<!--    <div class="login-container">-->
<!--      <div class="img">-->
<!--        <component :is="toRaw(illustration)" />-->
<!--      </div>-->
<!--      <div class="login-box">-->
<!--        <div class="login-form">-->
<!--          <img src="../../assets/login/avatar.svg" class="avatar" />-->
<!--          <Motion>-->
<!--            <h2 class="outline-none">{{ title }}</h2>-->
<!--          </Motion>-->

<!--          <el-form-->
<!--            ref="ruleFormRef"-->
<!--            :model=""-->
<!--            :rules=""-->
<!--            size="large"-->
<!--          >-->
<!--            <Motion :delay="100">-->
<!--              <el-form-item-->
<!--                :rules="[-->
<!--                  {-->
<!--                    required: true,-->
<!--                    message: '请输入用户名',-->
<!--                    trigger: 'blur'-->
<!--                  }-->
<!--                ]"-->
<!--                prop="username"-->
<!--              >-->
<!--                <el-input-->
<!--                  v-model=""-->
<!--                  clearable-->
<!--                  placeholder="用户名"-->
<!--                  :prefix-icon="useRenderIcon(User)"-->
<!--                />-->
<!--              </el-form-item>-->
<!--            </Motion>-->

<!--            <Motion :delay="150">-->
<!--              <el-form-item prop="password">-->
<!--                <el-input-->
<!--                  v-model=""-->
<!--                  clearable-->
<!--                  show-password-->
<!--                  placeholder="密码"-->
<!--                  :prefix-icon="useRenderIcon(Lock)"-->
<!--                />-->
<!--              </el-form-item>-->
<!--            </Motion>-->

<!--            <Motion :delay="250">-->
<!--              <el-button-->
<!--                class="w-full mt-4"-->
<!--                size="default"-->
<!--                type="primary"-->
<!--                @click=""-->
<!--              >-->
<!--                登录-->
<!--              </el-button>-->
<!--            </Motion>-->
<!--          </el-form>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<style scoped>-->
<!--@import url("@/style/login.css");-->
<!--</style>-->

<!--<style lang="scss" scoped>-->
<!--:deep(.el-input-group__append, .el-input-group__prepend) {-->
<!--  padding: 0;-->
<!--}-->
<!--</style>-->
