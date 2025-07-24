//用户相关的接口
import request  from "@/utils/request.ts";
import type {LoginFormData, LoginResponseData, userInfoResponseData} from "@/api/user/type.ts";

enum API {
  LOGIN_URL = '/admin/login',
  USERINFO_URL = '/admin/info',
  LOGOUT_URL = '/admin/logout',
}

export const reqLogin = (data: LoginFormData) => {
  return request.post<any, LoginResponseData>(API.LOGIN_URL, data);
}

export const reqUserInfo = () =>
  request.get<any, userInfoResponseData>(API.USERINFO_URL)

export const reqLogOut = () => request.post<any, any>(API.LOGOUT_URL)
