import request from '@/utils/request'
import type {
  UserResponseData,
  User,
  AllRoleResponseData,
  SetRoleData,
} from './type'

enum API {
  ALLUSER_URL = '/admin/getAllUsers',
  ADDUSER_URL = '/admin/addUser',
  UPDATEUSER_URL = '/admin/updateUser',
  ALLROLEURL = '/acl/user/toAssign/',
  SETROLE_url = '/acl/user/doAssignRole',
  DELETEUSER_URL = '/admin/deleteUser',
  DELETEALLUSER_URL = '/admin/deleteUsers',
}

// export const reqUserInfo = (page: number, limit: number, username: string) =>
//   request.get<any, UserResponseData>(
//     API.ALLUSER_URL + `${page}/${limit}/?username=${username}`,
//   )
export const reqUserInfo = (params: {
  pageNum: number;
  pageSize: number;
  username?: string;
  phone?:string;
  userStatus?:number;
}) => request.post<any, UserResponseData>(
  API.ALLUSER_URL,
  params // 将整个参数对象作为请求体
);

export const reqAddOrUpdateUser = (data: User) => {
  if (data.userId) {
    return request.put<any, any>(API.UPDATEUSER_URL, data)
  } else {
    return request.post<any, any>(API.ADDUSER_URL, data)
  }
}

export const reqAllRole = (userId: number) =>
  request.get<any, AllRoleResponseData>(API.ALLROLEURL + userId)

export const reqSetUserRole = (data: SetRoleData) =>
  request.post<any, any>(API.SETROLE_url, data)

export const reqRemoveUser = (userId: number) =>
  request.delete<any, any>(`${API.DELETEUSER_URL}/${userId}`);

export const reqSelectUser = (idList: number[]) =>
  request.delete<any, any>(API.DELETEALLUSER_URL, {
    data: idList , // 直接传数组，无需包装
    headers: {
      'Content-Type': 'application/json'
    }
  });
