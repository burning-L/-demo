import request from '@/utils/request'
import type { TradeMark, TradeMarkResponseData } from './type'
import type {UserResponseData} from "@/api/acl/user/type.ts";
enum API {
  TRADEMARK_URL = '/admin/getAllArtists',
  ADDTRADEMARK_URL = '/admin/addArtist',
  UPDATETRADEMARK_URL = '/admin/updateArtist',
  DELETE_URL = '/admin/deleteArtist',
  UPDATE_AVATAR_URL = '/admin/updateArtistAvatar',
}

// export const reqHasTradeMark = (page: number, limit: number) =>
//   request.get<any, TradeMarkResponseData>(
//     API.TRADEMARK_URL + `${page}/${limit}`,
//   )
export const reqHasTradeMark = (params: {
  pageNum: number;
  pageSize: number;
  artistName?: string;
  phone?:string;
  userStatus?:number;
}) => request.post<any, TradeMarkResponseData>(
  API.TRADEMARK_URL,
  params // 将整个参数对象作为请求体
);

export const reqAddOrUpdateTrademark = (data: TradeMark) => {
  if (data.artistId) {
    return request.put<any, any>(API.UPDATETRADEMARK_URL, data)
  } else {
    return request.post<any, any>(API.ADDTRADEMARK_URL, data)
  }
}

export const reqDeleteTrademark = (id: number) =>
  request.delete<any, any>(`${API.DELETE_URL}/${id}`);


export const reqUpdateArtistAvatar = (artistId: number, file: File) => {
  const formData = new FormData();
  formData.append('avatar', file);
  return request.patch<any, any>(
    `${API.UPDATE_AVATAR_URL}/${artistId}`,
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }
  );
};
