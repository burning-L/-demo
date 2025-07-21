package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.MessageConstant;
import com.example.mapper.ArtistMapper;
import com.example.mode.dto.ArtistAddDTO;
import com.example.mode.dto.ArtistDTO;
import com.example.mode.dto.ArtistUpdateDTO;
import com.example.mode.entity.Artist;
import com.example.mode.vo.ArtistNameVO;
import com.example.mode.vo.ArtistVO;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.IArtistService;
import com.example.service.MinioService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "artistCache")
public class ArtistServiceImpl extends ServiceImpl<ArtistMapper, Artist> implements IArtistService {
    @Autowired
    private ArtistMapper artistMapper;
    @Autowired
    private MinioService minioService;
    /**
     * 添加歌手
     *
     * @param artistAddDTO 歌手添加DTO
     * @return 添加结果
     */
    @Override
    @CacheEvict(cacheNames = "artistCache", allEntries = true)
    public Result addArtist(ArtistAddDTO artistAddDTO) {
        QueryWrapper<Artist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", artistAddDTO.getArtistName());
        if (artistMapper.selectCount(queryWrapper) > 0) {
            return Result.error(MessageConstant.ARTIST + MessageConstant.ALREADY_EXISTS);
        }

        Artist artist = new Artist();
        BeanUtils.copyProperties(artistAddDTO, artist);
        artistMapper.insert(artist);

        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS);
    }
    /**
     * 获取所有歌手数量
     *
     * @param gender 性别
     * @param area   地区
     * @return 歌手数量
     */
    @Override
    public Result<Long> getAllArtistsCount(Integer gender, String area) {
        QueryWrapper<Artist> queryWrapper = new QueryWrapper<>();
        if (gender != null) {
            queryWrapper.eq("gender", gender);
        }
        if (area != null) {
            queryWrapper.eq("area", area);
        }

        return Result.success(artistMapper.selectCount(queryWrapper));
    }
    /**
     * 获取所有歌手列表（含详情）
     *
     * @param artistDTO artistDTO
     * @return 歌手列表
     */
    @Override
    @Cacheable(key = "#artistDTO.pageNum + '-' + #artistDTO.pageSize + '-' + #artistDTO.artistName + '-' + #artistDTO.gender + '-' + #artistDTO.area + '-admin'")
    public Result<PageResult<Artist>> getAllArtistsAndDetail(ArtistDTO artistDTO) {
        // 分页查询
        Page<Artist> page = new Page<>(artistDTO.getPageNum(), artistDTO.getPageSize());
        QueryWrapper<Artist> queryWrapper = new QueryWrapper<>();
        // 根据 artistDTO 的条件构建查询条件
        if (artistDTO.getArtistName() != null) {
            queryWrapper.like("name", artistDTO.getArtistName());
        }
        if (artistDTO.getGender() != null) {
            queryWrapper.eq("gender", artistDTO.getGender());
        }
        if (artistDTO.getArea() != null) {
            queryWrapper.like("area", artistDTO.getArea());
        }

        // 倒序排序
        queryWrapper.orderByDesc("id");

        IPage<Artist> artistPage = artistMapper.selectPage(page, queryWrapper);
        if (artistPage.getRecords().size() == 0) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, new PageResult<>(0L, null));
        }

        return Result.success(new PageResult<>(artistPage.getTotal(), artistPage.getRecords()));
    }

    /**
     * 更新歌手
     *
     * @param artistUpdateDTO 歌手更新DTO
     * @return 更新结果
     */
    @Override
    @CacheEvict(cacheNames = "artistCache", allEntries = true)
    public Result updateArtist(ArtistUpdateDTO artistUpdateDTO) {
        Long artistId = artistUpdateDTO.getArtistId();

        Artist artistByArtistName = artistMapper.selectOne(new QueryWrapper<Artist>().eq("name", artistUpdateDTO.getArtistName()));
        if (artistByArtistName != null && !artistByArtistName.getArtistId().equals(artistId)) {
            return Result.error(MessageConstant.ARTIST + MessageConstant.ALREADY_EXISTS);
        }

        Artist artist = new Artist();
        BeanUtils.copyProperties(artistUpdateDTO, artist);
        if (artistMapper.updateById(artist) == 0) {
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }
    /**
     * 更新歌手头像
     *
     * @param artistId 歌手id
     * @param avatar   头像
     * @return 更新结果
     */
    @Override
    @CacheEvict(cacheNames = "artistCache", allEntries = true)
    public Result updateArtistAvatar(Long artistId, String avatar) {
        Artist artist = artistMapper.selectById(artistId);
        String avatarUrl = artist.getAvatar();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            minioService.deleteFile(avatarUrl);
        }

        artist.setAvatar(avatar);
        if (artistMapper.updateById(artist) == 0) {
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }
    /**
     * 删除歌手
     *
     * @param artistId 歌手id
     * @return 删除结果
     */
    @Override
    @CacheEvict(cacheNames = "artistCache", allEntries = true)
    public Result deleteArtist(Long artistId) {
        // 1. 查询歌手信息，获取头像 URL
        Artist artist = artistMapper.selectById(artistId);
        if (artist == null) {
            return Result.error(MessageConstant.ARTIST + MessageConstant.NOT_FOUND);
        }
        String avatarUrl = artist.getAvatar();

        // 2. 先删除 MinIO 里的头像文件
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            minioService.deleteFile(avatarUrl);
        }

        // 3. 删除数据库中的歌手信息
        if (artistMapper.deleteById(artistId) == 0) {
            return Result.error(MessageConstant.DELETE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS);
    }

    /**
     * 批量删除歌手
     *
     * @param artistIds 歌手id列表
     * @return 删除结果
     */
    @Override
    @CacheEvict(cacheNames = {"artistCache", "songCache"}, allEntries = true)
    public Result deleteArtists(List<Long> artistIds) {
        // 1. 查询歌手信息，获取头像 URL 列表
        List<Artist> artists = artistMapper.selectByIds(artistIds);
        List<String> avatarUrlList = artists.stream()
                .map(Artist::getAvatar)
                .filter(avatarUrl -> avatarUrl != null && !avatarUrl.isEmpty())
                .toList();

        // 2. 先删除 MinIO 里的头像文件
        for (String avatarUrl : avatarUrlList) {
            minioService.deleteFile(avatarUrl);
        }

        // 3. 删除数据库中的歌手信息
        if (artistMapper.deleteByIds(artistIds) == 0) {
            return Result.error(MessageConstant.DELETE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS);
    }

    /**
     * 获取所有歌手id和歌手名称
     *
     * @return 歌手名称列表
     */
    @Override
    @Cacheable(key = "'allArtistNames'")
    public Result<List<ArtistNameVO>> getAllArtistNames() {
        List<Artist> artists = artistMapper.selectList(new QueryWrapper<Artist>().orderByDesc("id"));
        if (artists.isEmpty()) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, null);
        }

        List<ArtistNameVO> artistNameVOList = artists.stream()
                .map(artist -> {
                    ArtistNameVO artistNameVO = new ArtistNameVO();
                    artistNameVO.setArtistId(artist.getArtistId());
                    artistNameVO.setArtistName(artist.getArtistName());
                    return artistNameVO;
                }).toList();

        return Result.success(artistNameVOList);
    }
}
