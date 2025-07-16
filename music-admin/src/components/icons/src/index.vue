<script setup lang="ts">
defineProps({
    prefix:{
      type:String,
      default:'#icon-'
    },
  name:String,
  color:{
      type:String,
       default:''
  }
})

</script>

<template>
<svg style="width: 30px;height: 30px">
  <use :xlink:href="prefix+name" :fill="color"></use>
</svg>
</template>

<style scoped>

</style>
