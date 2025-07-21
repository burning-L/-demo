package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mode.dto.ArtistAddDTO;
import com.example.mode.dto.ArtistDTO;
import com.example.mode.dto.ArtistUpdateDTO;
import com.example.mode.entity.Artist;
import com.example.mode.vo.ArtistDetailVO;
import com.example.mode.vo.ArtistNameVO;
import com.example.mode.vo.ArtistVO;
import com.example.result.PageResult;
import com.example.result.Result;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IArtistService extends IService<Artist> {
    // 添加歌手
    Result addArtist(ArtistAddDTO artistAddDTO);
    // 获取所有歌手数量
    Result<Long> getAllArtistsCount(Integer gender, String area);
//    // 获取所有歌手
//    Result<PageResult<ArtistVO>> getAllArtists(ArtistDTO artistDTO);
    // 获取所有歌手
    Result<PageResult<Artist>> getAllArtistsAndDetail(ArtistDTO artistDTO);
    // 更新歌手
    Result updateArtist(ArtistUpdateDTO artistUpdateDTO);

    // 更新歌手头像
    Result updateArtistAvatar(Long artistId, String avatar);
//
    // 删除歌手
    Result deleteArtist(Long ArtistId);

    // 批量删除歌手
    Result deleteArtists(List<Long> artistIds);

    // 获取所有歌手id和名字
    Result<List<ArtistNameVO>> getAllArtistNames();
//
//    // 获取随机歌手
//    Result<List<ArtistVO>> getRandomArtists();
//
//    // 根据id获取歌手详情
//    Result<ArtistDetailVO> getArtistDetail(Long artistId, HttpServletRequest request);

}
