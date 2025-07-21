package com.example.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mode.entity.Artist;
import com.example.mode.vo.ArtistDetailVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 */
@Mapper
public interface ArtistMapper extends BaseMapper<Artist> {

    // 根据id查询歌手详情
//    ArtistDetailVO getArtistDetailById(Long artistId);

}
