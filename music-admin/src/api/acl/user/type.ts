export interface ResponseData {
  code: number
  message: string
  ok: boolean
}

// export interface User {
//   id?: number
//   createTime?: string
//   updateTime?: string
//   username?: string
//   password?: string
//   name?: string
//   phone?: null
//   roleName?: string
// }
export interface User {
  userId?: number
  username?: string
  password?:string
  phone?: string
  email?: string
  userAvatar?: string
  introduction?: string
  createTime?: null
  updateTime?: string
  userStatus?:string
}

export type Records = User[]

// export interface UserResponseData extends ResponseData {
//   data: {
//     records: Records
//     total: number
//     size: number
//     current: number
//     pages: number
//   }
// }
export interface UserResponseData extends ResponseData {
  data: {
    items: Records
    total: number
    size: number
    current: number
    pages: number
  }
}


export interface RoleData {
  id?: number
  createTime?: string
  updateTime?: string
  roleName: string
  remark: null
}

export type AllRole = RoleData[]
export interface AllRoleResponseData extends ResponseData {
  data: {
    assignRoles: AllRole
    allRolesList: AllRole
  }
}

export interface SetRoleData {
  roleIdList: number[]
  userId: number
}

export interface UserQueryParams {
  page: number;
  limit: number;
  username?: string;
  phone?:string;
  userStatus?:number;
}
