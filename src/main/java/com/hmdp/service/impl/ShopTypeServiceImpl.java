package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_LIST_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result queryShopType() {
        // redis中的key
        String key = CACHE_SHOP_TYPE_LIST_KEY;
        // redis中的value
        List<String> shopTypeJSONList = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (shopTypeJSONList != null && !shopTypeJSONList.isEmpty()) {
            ArrayList<ShopType> shopTypes = new ArrayList<>();
            for (String str : shopTypeJSONList) {
                shopTypes.add(JSONUtil.toBean(str, ShopType.class));
            }
            return Result.ok(shopTypes);
        }
        // 从数据库中查询得到的结果
        List<ShopType> shopTypeList = query().orderByAsc("sort").list();
        if (shopTypeJSONList == null || shopTypeList.isEmpty()) {
            return Result.fail("分类不存在！");
        }
        for (ShopType shopType : shopTypeList) {
            stringRedisTemplate.opsForList().rightPushAll(key, JSONUtil.toJsonStr(shopType));
        }
        return Result.ok(shopTypeList);
    }
}
