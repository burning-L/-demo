package com.example.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.StyleMapper;
import com.example.mode.entity.Style;
import com.example.service.IStyleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>

 */
@Service
public class StyleServiceImpl extends ServiceImpl<StyleMapper, Style> implements IStyleService {

}
