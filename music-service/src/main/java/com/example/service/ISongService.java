package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mode.dto.SongAddDTO;
import com.example.mode.dto.SongAndArtistDTO;
import com.example.mode.dto.SongUpdateDTO;
import com.example.mode.entity.Song;
import com.example.mode.vo.SongAdminVO;
import com.example.result.PageResult;
import com.example.result.Result;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 */
public interface ISongService extends IService<Song> {

//    // 获取所有歌曲
//    Result<PageResult<SongVO>> getAllSongs(SongDTO songDTO, HttpServletRequest request);

    // 获取所有歌曲
    Result<PageResult<SongAdminVO>> getAllSongsByArtist(SongAndArtistDTO songDTO);

//    // 获取推荐歌曲
//    Result<List<SongVO>> getRecommendedSongs(HttpServletRequest request);
//
//    // 根据id获取歌曲详情
//    Result<SongDetailVO> getSongDetail(Long songId, HttpServletRequest request);

    // 获取所有歌曲数量
    Result<Long> getAllSongsCount(String style);

    // 添加歌曲信息
    Result addSong(SongAddDTO songAddDTO);

    // 更新歌曲信息
    Result updateSong(SongUpdateDTO songUpdateDTO);

    // 更新歌曲封面
    Result updateSongCover(Long songId, String coverUrl);

    // 更新歌曲音频
    Result updateSongAudio(Long songId, String audioUrl);

    // 删除歌曲
    Result deleteSong(Long songId);

    // 批量删除歌曲
    Result deleteSongs(List<Long> songIds);

}
