<script setup lang="ts">
import Lock from "@iconify-icons/ri/lock-fill";
import User from "@iconify-icons/ri/user-3-fill";
import { bg, avatar, illustration } from "./utils/static";
import {toRaw} from "vue";
import BgComponent from '@/assets/login/illustration.svg?component'
</script>

<template>
  <div class="select-none">
<!--    <img :src="bg" class="wave" />-->
    <BgComponent class="wave" />
  </div>
</template>

<style scoped>
@import url("@/styles/login.css");
</style>

<style lang="scss" scoped>
:deep(.el-input-group__append, .el-input-group__prepend) {
  padding: 0;
}
</style>
