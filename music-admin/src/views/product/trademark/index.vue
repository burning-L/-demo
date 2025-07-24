<script setup lang="ts">
import { ref, onMounted, reactive, nextTick } from 'vue'

import {
  reqHasTradeMark,
  reqAddOrUpdateTrademark,
  reqDeleteTrademark, reqUpdateArtistAvatar,
} from '@/api/product/trademark'
import type {
  Records,
  TradeMark,
  TradeMarkResponseData,
} from '@/api/product/trademark/type'
import { UploadProps } from 'element-plus/es/components/upload/src/upload'
import {ElMessage} from "element-plus";
import useLayOutSettingStore from "@/stores/modules/setting.ts";

let pageNo = ref<number>(1)

let pageSize = ref<number>(3)
let total = ref<number>(0)
let dialogFormVisible = ref<boolean>(false)
let keyword = ref<string>('')

let tradeMarkArr = ref<Records>([])

let trademarkParams = reactive<TradeMark>({
  artistName: '',
  avatar: '',
  gender:0,
  birth:'',
  area:'',
  introduction:'',
})
let formRef = ref()
const getHasTradeMark = async (pager = 1) => {
  pageNo.value = pager
  let res: TradeMarkResponseData = await reqHasTradeMark({
      "pageNum":pageNo.value,
      "pageSize":pageSize.value,
      "artistName":keyword.value,
  }
  )
  if (res.code === 200) {
    total.value = res.data.total
    tradeMarkArr.value = res.data.items
  }
}
const search = () => {
  getHasTradeMark()
  keyword.value = ''
}
let settingStore = useLayOutSettingStore()
const reset = () => {
  settingStore.refsh = !settingStore.refsh
}
onMounted(() => {
  getHasTradeMark()
})

const sizeChange = () => {
  getHasTradeMark()
}

const addTradeMark = () => {
  dialogFormVisible.value = true
  trademarkParams.artistName = ''
  trademarkParams.gender =0
  trademarkParams.avatar=''
  trademarkParams.area=''
  trademarkParams.introduction=''
  trademarkParams.birth=''

  nextTick(() => {
    if (formRef.value) {
      formRef.value.clearValidate('artistName')
      formRef.value.clearValidate('avatar')
    }
  })
}

const updateTradeMark = (row: TradeMark) => {
  dialogFormVisible.value = true
  Object.assign(trademarkParams, row)

  nextTick(() => {
    if (formRef.value) {
      formRef.value.clearValidate('tmName')
      formRef.value.clearValidate('logoUrl')
    }
  })
}

const cancel = () => {
  dialogFormVisible.value = false
}

const confirm = async () => {
  await formRef.value.validate()

  let res = await reqAddOrUpdateTrademark(trademarkParams)
  if (res.code === 200) {
    dialogFormVisible.value = false
    ElMessage({
      type: 'success',
      message: trademarkParams.artistId ? '修改歌手成功' : '添加歌手成功',
    })
    getHasTradeMark(trademarkParams.artistId ? pageNo.value : 1)
  } else {
    ElMessage({
      type: 'error',
      message: trademarkParams.artistId ? '修改歌手失败' : '添加歌手失败',
    })
  }
}
//图片上传校验函数
const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (
    rawFile.type === 'image/png' ||
    rawFile.type === 'image/jpeg' ||
    rawFile.type === 'image/gif'
  ) {
    if (rawFile.size / 1024 / 1024 < 4) {

      return true
    } else {
      ElMessage({
        type: 'error',
        message: '上传的文件大小应小于4M',
      })
    }
  } else {
    ElMessage({
      type: 'error',
      message: '上传的文件类型必须是PNG|JPG|GIF',
    })
    return false
  }
}
//图片上传成功后的回调函数
const handleAvatarSuccess: UploadProps['onSuccess'] = (
  response,
  uploadFile,
) => {
  trademarkParams.avatar = response.data
  formRef.value.clearValidate('logoUrl')
}
// //音频上传成功后的回调函数
// const handleAudioSuccess: UploadProps['onSuccess'] =(response) => {
//   trademarkParams.audioUrl = response.data; // 存储音频URL
//   formRef.value.clearValidate('audioUrl'); // 清除校验提示
//   console.log('Updated audio URL:', trademarkParams.audioUrl); //
// };
// // 音频上传校验函数
// const beforeAudioUpload: UploadProps['beforeUpload'] = (rawFile) => {
//   // 音频类型校验（MP3、WAV、OGG等）
//   const allowedAudioTypes = ['audio/mpeg', 'audio/wav', 'audio/ogg', 'audio/mp4', 'audio/webm'];
//   if (!allowedAudioTypes.includes(rawFile.type)) {
//     ElMessage.error('音频类型必须是 MP3、WAV、OGG、M4A 或 WebM');
//     return false;
//   }
//   // 音频大小限制（例如20MB）
//   const maxSize = 20 * 1024 * 1024;
//   if (rawFile.size > maxSize) {
//     ElMessage.error('音频大小应小于 20MB');
//     return false;
//   }
//   return true;
// };

const validatorTmName = (rule: any, value: any, callBack: any) => {
  if (value.trim().length >= 2) {
    callBack()
  } else {
    callBack(new Error('歌手名字大于等于两位'))
  }
}

const validatorLogoUrl = (rule: any, value: any, callBack: any) => {
  if (value) {
    callBack()
  } else {
    callBack(new Error('Logo的图片务必上传'))
  }
}
// const validatorAudioUrl = (rule: any, value: any, callBack: any) => {
//   if (value) {
//     callBack()
//   } else {
//     callBack(new Error('音频务必上传'))
//   }
// }

const rules = {
  artistName: [
    {
      required: true,
      trigger: 'blur',
      validator: validatorTmName,
    },
  ],
  avatar: [
    {
      required: true,
      trigger: 'change',
      validator: validatorLogoUrl,
    },
  ],
  // audioUrl: [ // 新增音频校验规则（可选或必传，根据需求调整）
  //   {
  //     required: false, // 设为false表示可选上传
  //     trigger: 'change',
  //     validator: validatorAudioUrl,
  //   }
  // ]
}

const removeTradeMark = async (id: number) => {
  let res = await reqDeleteTrademark(id)

  if (res.code === 200) {
    ElMessage({
      type: 'success',
      message: '删除歌手成功',
    })
    //再次获取已有的品牌数据
    getHasTradeMark(
      tradeMarkArr.value.length > 1 ? pageNo.value : pageNo.value - 1,
    )
  } else {
    ElMessage({
      type: 'error',
      message: '删除歌手失败',
    })
  }
}
const audioUrl = 'http://192.168.2.35:9000/music-project/songs/52617eeb-ac38-475b-93dd-4565363d017e-356596524 (1).mp3'
const token = localStorage.getItem('TOKEN');
const headers = {
  Authorization: `Bearer ${token}`
};
const selectedFile = ref<File | null>(null);
const handleFileChange = (file: any) => {
  selectedFile.value = file.raw;
}
// 手动提交上传
const submitUpload = async (id: number) => {
  if (selectedFile.value) {
    try {
      const res = await reqUpdateArtistAvatar(id, selectedFile.value);
      ElMessage.success('头像上传成功');
      // 上传成功后，可做更新页面等操作，比如更新 row.avatar 显示新头像
      getHasTradeMark(pageNo.value);
      // 清空 selectedFile
      selectedFile.value=null;
    } catch (error) {
      ElMessage.error('头像上传失败，请重试');
    }
  } else {
    ElMessage.warning('请先选择要上传的文件');
  }
};
</script>
<template>
  <el-card style="height: 80px">
    <el-form :inline="true" class="form">
      <el-form-item label="用户名:">
        <el-input placeholder="请你输入搜索用户名" v-model="keyword"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          size="default"
          :disabled="keyword.length ? false : true"
          @click="search()"
        >
          搜索
        </el-button>
        <el-button size="default" @click="reset">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
  <el-card class="box-card" style="margin: 10px 0">
    <!-- 添加品牌 -->
    <el-button
      type="primary"
      size="default"
      icon="Plus"
      @click="addTradeMark"
      v-has="`btn.Trademark.add`"
    >
      添加歌手
    </el-button>
    <el-table style="margin: 10px 0" border :data="tradeMarkArr">
      <el-table-column
        label="序号"
        width="80px"
        align="center"
        type="index"
      ></el-table-column>
      <el-table-column label="歌手名字" prop="artistName" align="center" ></el-table-column>
      <el-table-column label="歌手照片" align="center" >
        <template #="{ row, $index }">
          <img
            :src="row.avatar"
            alt="图片丢失了~"
            style="width: 100px; height: 100px"
          />
        </template>
      </el-table-column>
      <el-table-column label="歌手性别" align="center"  >
        <template #default="scope">
          <div style="text-align: center" >
            <span v-if="scope.row.gender === 0">男</span>
            <span v-else-if="scope.row.gender === 1">女</span>
            <span v-else>未知</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="birth" label="出生日期" align="center"  />
      <el-table-column prop="area" label="国籍" align="center"  />
      <el-table-column prop="introduction" label="简介" align="center"  />
<!--      <el-table-column label="音频">-->
<!--        <template #="{ row }"> &lt;!&ndash; 简化解构，只取 row &ndash;&gt;-->
<!--          <div v-if="row.audioUrl" class="audio-player">-->
<!--            <audio-->
<!--              :src="row.audioUrl"-->
<!--              controls-->
<!--              style="width: 200px;"-->
<!--              preload="metadata"-->
<!--            >-->
<!--              您的浏览器不支持音频播放-->
<!--            </audio>-->
<!--          </div>-->
<!--          <span v-else class="text-gray-400">无音频</span> &lt;!&ndash; 无音频时的提示 &ndash;&gt;-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="操作"align="center" >
        <template #="{ row, $index }">
          <el-button
            type="primary"
            size="small"
            icon="Edit"
            @click="updateTradeMark(row)"
          ></el-button>
          <el-popconfirm
            :title="`您确定删除${row.artistName}`"
            width="250px"
            icon="delete"
            @confirm="removeTradeMark(row.artistId)"
          >
            <template #reference>
              <el-button type="danger" size="small" icon="Delete"></el-button>
            </template>
          </el-popconfirm>
<!--          <el-upload-->
<!--            method="patch"-->
<!--            name="avatar"-->
<!--            action="/admin/updateArtistAvatar/146"-->
<!--            :show-file-list="false"-->
<!--            :on-success="handleAvatarSuccess"-->
<!--            :before-upload="beforeAvatarUpload"-->
<!--            class="dot-upload"-->
<!--            :headers="headers"-->
<!--          >-->
<!--            <div class="dot-btn">...</div>-->
<!--          </el-upload>-->
          <el-upload
            :auto-upload="false"
            :on-change="handleFileChange"
            :before-upload="beforeAvatarUpload"
            class="dot-upload"
          >
            <div class="dot-btn">...</div>
          </el-upload>
          <!-- 可选：手动触发上传的按钮 -->
          <el-button type="primary" @click="submitUpload(row.artistId)">确认上传</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- pagination -->
    <el-pagination
      v-model:current-page="pageNo"
      v-model:page-size="pageSize"
      :page-sizes="[3, 5, 7, 9]"
      :background="true"
      layout="prev, pager, next, jumper, ->, sizes, total"
      :total="total"
      @current-change="getHasTradeMark"
      @size-change="sizeChange"
    />
  </el-card>

  <el-dialog
    v-model="dialogFormVisible"
    :title="trademarkParams.artistId ? '修改歌手' : '添加歌手'"
  >
    <el-form
      style="width: 90%"
      :model="trademarkParams"
      :rules="rules"
      ref="formRef"
    >
      <el-form-item label="歌手姓名" label-width="100px" prop="artistName">
        <el-input
          placeholder="请您输入歌手姓名"
          v-model="trademarkParams.artistName"
        ></el-input>
      </el-form-item>
<!--      &lt;!&ndash; 图片上传组件 &ndash;&gt;-->
<!--      <el-form-item label="品牌Logo" label-width="100px" prop="avatar">-->
<!--        <el-upload-->
<!--          class="avatar-uploader"-->
<!--          action="/api/admin/product/fileUpload"-->
<!--          :show-file-list="false"-->
<!--          :on-success="handleAvatarSuccess"-->
<!--          :before-upload="beforeAvatarUpload"-->
<!--        >-->
<!--          <img-->
<!--            v-if="trademarkParams.avatar"-->
<!--            :src="trademarkParams.avatar"-->
<!--            class="avatar"-->
<!--          />-->
<!--          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>-->
<!--        </el-upload>-->
<!--      </el-form-item>-->
      <el-form-item label="歌手性别" label-width="100px" prop="gender">
        <el-input
          placeholder="歌手性别"
          v-model="trademarkParams.gender"
        ></el-input>
      </el-form-item>
      <el-form-item label="出生日期" label-width="100px" prop="birth">
        <el-date-picker
          v-model="trademarkParams.birth"
          type="date"
          placeholder="请选择出生日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          :editable="false"
        />
      </el-form-item>
      <el-form-item label="国籍" label-width="100px" prop="birth">
        <el-input
          placeholder="国籍"
          v-model="trademarkParams.area"
        ></el-input>
      </el-form-item>
      <el-form-item label="简介" label-width="100px" prop="birth">
        <el-input
          placeholder="简介"
          v-model="trademarkParams.introduction"
        ></el-input>
      </el-form-item>
<!--      &lt;!&ndash; 音频上传组件 &ndash;&gt;-->
<!--      <el-form-item label="品牌音频" label-width="100px" prop="audioUrl">-->
<!--        <el-upload-->
<!--          class="custom-audio-uploader"-->
<!--          action="/api/admin/product/fileUpload"-->
<!--          :show-file-list="false"-->
<!--          :on-success="handleAudioSuccess"-->
<!--          :before-upload="beforeAudioUpload"-->
<!--        >-->
<!--          <el-button type="primary">上传音频</el-button>-->
<!--        </el-upload>-->
<!--      </el-form-item>-->
    </el-form>
    <template #footer>
      <el-button type="primary" size="default" @click="cancel">取消</el-button>
      <el-button type="primary" size="default" @click="confirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}


.audio-player {
  margin-top: 10px;
}
.audio-player audio {
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
  border-radius: 4px;
}

</style>
