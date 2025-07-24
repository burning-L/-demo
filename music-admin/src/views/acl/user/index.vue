<script setup lang="ts">
import { ref, onMounted, reactive, nextTick } from 'vue'
import type {
  UserResponseData,
  Records,
  User,
  AllRoleResponseData,
  AllRole,
  SetRoleData,
  UserQueryParams
} from '@/api/acl/user/type'
import {
  reqUserInfo,
  reqAddOrUpdateUser,
  reqAllRole,
  reqSetUserRole,
  reqRemoveUser,
  reqSelectUser,
} from '@/api/acl/user'
import useLayOutSettingStore from "@/stores/modules/setting.ts";
import { ElMessage } from 'element-plus'
let keyword = ref<string>('')

let selectIdArr = ref<User[]>([])
const selectChange = (value: any) => {
  selectIdArr.value = value
}
let userArr = ref<Records>([])

let pageNo = ref<number>(1)
let pageSize = ref<number>(5)
let total = ref<number>(0)

let drawer = ref<boolean>(false)
let drawer1 = ref<boolean>(false)
let userParams = reactive<User>({
  username: '',
  phone: '',
  password: '',
  email:'',
})

const checkAll = ref<boolean>(false)
const isIndeterminate = ref<boolean>(true)

let allRole = ref<AllRole>([])

let userRole = ref<AllRole>([])

const validatorUserName = (rule: any, value: any, callBack: any) => {
  if (value.trim().length >= 5) {
    callBack()
  } else {
    callBack(new Error('用户名字至少五位'))
  }
}

const validatorPhone = (rule: any, value: any, callBack: any) => {
  if (value.trim().length >= 11) {
    callBack()
  } else {
    callBack(new Error('用户昵称至少十一位'))
  }
}

const validatorPassword = (rule: any, value: any, callBack: any) => {
  if (value.trim().length >= 5) {
    callBack()
  } else {
    callBack(new Error('用户密码至少六位'))
  }
}
const validatorEmail = (rule: any, value: any, callback: any) => {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!value) {
    callback(new Error('请输入邮箱')); // 检查是否为空
  } else if (!emailRegex.test(value.trim())) {
    callback(new Error('请输入正确的邮箱格式')); // 检查格式
  } else {
    callback(); // 验证通过
  }
}
const rules = {
  username: [{ required: true, trigger: 'blur', validator: validatorUserName }],
  phone: [{ required: true, trigger: 'blur', validator: validatorPhone }],
  password: [{ required: true, trigger: 'blur', validator: validatorPassword }],
  email: [{ required: true, trigger: 'blur', validator: validatorEmail }],
}
//刷新页面获取数据
onMounted(() => {
  getHasUser()
})
const getHasUser = async (pager = 1) => {
  pageNo.value = pager
  let res: UserResponseData = await reqUserInfo({
    "pageNum":pageNo.value,
    "pageSize":pageSize.value,
    "username":keyword.value,
    }
  )
  if (res.code === 200) {
    total.value = res.data.total
    userArr.value = res.data.items
  }
}

const search = () => {
  getHasUser()
  keyword.value = ''
}
let settingStore = useLayOutSettingStore()
const reset = () => {
  settingStore.refsh = !settingStore.refsh
}

let formRef = ref<any>()   //给rules（表格）标记
const addUser = () => {
  drawer.value = true
  // 重置 userParams 表单数据
  Object.assign(userParams, {
    userStatus:1,
    username: '',
    phone: '',
    password: '',
    email:'',
  })
  // 在 DOM 更新后清除校验提示
  nextTick(() => {
    formRef.value.clearValidate('username')
    formRef.value.clearValidate('phone')
    formRef.value.clearValidate('password')
    formRef.value.clearValidate('email')
  })
}
const cancel = () => {
  drawer.value = false
}
const save = async () => {
  formRef.value.validate()
  let res: any = await reqAddOrUpdateUser(userParams)
  if (res.code === 200) {
    drawer.value = false
    ElMessage({
      type: 'success',
      message: userParams.userId ? '更新成功' : '添加成功',
    })
    setTimeout(() => {
      window.location.reload()
    }, 500) // 延迟 1 秒刷新
  } else {
    drawer.value = false
    ElMessage({
      type: 'error',
      message: userParams.userId ? '更新失败' : '添加失败',
    })
  }
}
const deleteSelectUser = async () => {
  try {
    let idList: any = selectIdArr.value.map((item) => {
      return item.userId
    })
    let res: any = await reqSelectUser(idList)
    if (res.code === 200) {
      ElMessage({ type: 'success', message: '删除成功' })
      getHasUser(userArr.value.length > 1 ? pageNo.value : pageNo.value - 1)
    }
  } catch (error) {
    console.error('删除操作异常:', error)
    ElMessage({ type: 'error', message: '删除失败，请重试' })
  }
}

const setRole = async (row: User) => {
  drawer1.value = true
  Object.assign(userParams, row)
  let res: AllRoleResponseData = await reqAllRole(userParams.userId as number)
  if (res.code === 200) {
    allRole.value = res.data.allRolesList
    userRole.value = res.data.assignRoles
    drawer1.value = true
  }
}
const confirmClick = async () => {
  let data: SetRoleData = {
    userId: userParams.userId as number,
    roleIdList: userRole.value.map((item) => {
      return item.id as number
    }),
  }
  let res: any = await reqSetUserRole(data)
  if (res.code === 200) {
    ElMessage({
      type: 'success',
      message: '分配职务成功',
    })
    drawer1.value = false
    getHasUser(pageNo.value)
  }
}
const handleCheckAllChange = (val: boolean) => {
  userRole.value = val ? allRole.value : []
  isIndeterminate.value = false
}
const handleCheckedUsersChange = (value: string[]) => {
  const checkedCount = value.length
  checkAll.value = checkedCount === allRole.value.length
  isIndeterminate.value =
    checkedCount > 0 && checkedCount < allRole.value.length
}

const updateUser = (row: User) => {
  drawer.value = true
  Object.assign(userParams, row)
  nextTick(() => {
    formRef.value.clearValidate('username')
    formRef.value.clearValidate('name')
    formRef.value.clearValidate('phone')
  })
}
const deleteUser = async (userId: number) => {
  let res: any = await reqRemoveUser(userId)
  if (res.code === 200) {
    ElMessage({ type: 'success', message: '删除成功' })
    getHasUser(userArr.value.length > 1 ? pageNo.value : pageNo.value - 1)
  }
}
const handler = () => {
  getHasUser()
}
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

  <el-card style="margin: 10px 0">
    <el-button type="primary" size="default" @click="addUser">
      添加用户
    </el-button>
    <el-button
      type="danger"
      size="default"
      :disabled="selectIdArr.length ? false : true"
      @click="deleteSelectUser"
    >
      批量删除
    </el-button>

    <el-table
      style="margin: 10px 0"
      border
      :data="userArr"
      @selection-change="selectChange"
    >
      <el-table-column type="selection" align="center"></el-table-column>
      <el-table-column label="#" align="center" type="index"></el-table-column>
      <el-table-column label="id" align="center" prop="userId"></el-table-column>
      <el-table-column
        label="用户名字"
        align="center"
        prop="username"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        label="电话"
        align="center"
        prop="phone"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        label="邮箱"
        align="center"
        prop="email"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        label="更新时间"
        align="center"
        prop="updateTime"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column label="操作" width="300px" align="center">
        <template #="{ row, $index }">
          <el-button size="small" icon="User" @click="setRole(row)">
            分配角色
          </el-button>
          <el-button
            type="primary"
            size="small"
            icon="Edit"
            @click="updateUser(row)"
          >
            编辑
          </el-button>
          <el-popconfirm
            :title="`你确定删除${row.username}`"
            width="260px"
            @confirm="deleteUser(row.userId)"
          >
            <template #reference>
              <el-button type="danger" size="small" icon="Delete">
                删除
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!--分页器组件-->
    <el-pagination
      v-model:current-page="pageNo"
      v-model:page-size="pageSize"
      :page-sizes="[5, 7, 9, 11]"
      :background="true"
      layout="prev, pager, next, jumper, -> , sizes, total"
      :total="total"
      @current-change="getHasUser"
      @size-change="handler"
    />
  </el-card>
<!--抽屉弹窗组件  drawer为true打开-->
  <el-drawer v-model="drawer">
    <template #header>
      <h4>{{ userParams.userId ? '更新用户' : '添加用户' }}</h4>
    </template>
    <template #default>
      <el-form :model="userParams" :rules="rules" ref="formRef">
        <el-form-item label="用户姓名" prop="username">
          <el-input
            placeholder="请您输入用户姓名"
            v-model="userParams.username"
          ></el-input>
        </el-form-item>
        <el-form-item label="用户密码" prop="password" v-if="!userParams.userId">
          <el-input
            placeholder="请您输入用户密码"
            v-model="userParams.password"
          ></el-input>
        </el-form-item>
        <el-form-item label="用户电话" prop="phone">
          <el-input
            placeholder="请您输入用户电话"
            v-model="userParams.phone"
          ></el-input>
        </el-form-item>
        <el-form-item label="用户邮箱" prop="email">
          <el-input
            placeholder="请您输入用户邮箱"
            v-model="userParams.email"
          ></el-input>
        </el-form-item>
        <el-form-item label="用户状态" prop="userStatus" v-if="!userParams.userId" required>
          <el-input
            placeholder="请您输入用户状态"
            v-model="userParams.userStatus"
          ></el-input>
        </el-form-item>
      </el-form>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </div>
    </template>
  </el-drawer>
  <!--抽屉弹窗组件  drawer为true打开-->
  <el-drawer v-model="drawer1">
    <template #header>
      <h4>分配角色</h4>
    </template>
    <template #default>
      <el-form>
        <el-form-item label="用户姓名">
          <el-input v-model="userParams.username" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="职位列表">
          <el-checkbox
            v-model="checkAll"
            :indeterminate="isIndeterminate"
            @change="handleCheckAllChange"
          >
            全选
          </el-checkbox>
          <el-checkbox-group
            v-model="userRole"
            @change="handleCheckedUsersChange"
          >
            <el-checkbox
              v-for="(role, index) in allRole"
              :key="index"
              :label="role"
            >
              {{ role.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="drawer1 = false">取消</el-button>
        <el-button type="primary" @click="confirmClick">确定</el-button>
      </div>
    </template>
  </el-drawer>
</template>
<style lang="scss" scoped>
.form {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
