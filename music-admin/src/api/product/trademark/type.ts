export interface ResponseData {
  code: number
  message: string
  ok: boolean
}

// export interface TradeMark {
//   id?: number
//   tmName: string
//   logoUrl: string
//   audioUrl:string
// }
export interface TradeMark {
  artistId?: number
  artistName: string
  gender?: number
  avatar?:string
  birth?:string
  area?:string
  introduction?:string
}


export type Records = TradeMark[]

export interface TradeMarkResponseData extends ResponseData {
  data: {
    items: Records
    total: number
    size: number
    current: number
    searchCount: boolean
    pages: number
  }
}
