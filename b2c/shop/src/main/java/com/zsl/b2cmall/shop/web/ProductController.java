package com.zsl.b2cmall.shop.web;

import com.zsl.b2cmall.common.response.BaseResponseVO;
import com.zsl.b2cmall.shop.dao.ProductMapper;
import com.zsl.b2cmall.shop.entity.ProductPO;
import com.zsl.b2cmall.shop.request.BatchUpdateRequestVO;
import com.zsl.b2cmall.shop.request.CreateProductRequestVO;
import com.zsl.b2cmall.shop.request.ProductQueryVO;
import com.zsl.b2cmall.shop.service.ExcelService;
import com.zsl.b2cmall.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ExcelService excelService;

    /**
     * 新建商品
     * 使用模板方法模式：实物商品校验库存，虚拟商品校验合规
     */
    @PostMapping("/create")
    public BaseResponseVO createProduct(@RequestBody CreateProductRequestVO request) {
        return productService.createProduct(request);
    }

    /**
     * 商品列表（兼容旧接口）
     */
    @GetMapping("/list")
    public BaseResponseVO listProducts(@RequestParam Integer shopId) {
        return productService.listProducts(shopId);
    }

    /**
     * 商品管理分页查询（名称/类型/类别/状态/配送方式/分类 多条件筛选）
     */
    @GetMapping("/page")
    public BaseResponseVO page(ProductQueryVO query) {
        return productService.page(query);
    }

    /**
     * 商品详情
     */
    @GetMapping("/detail")
    public BaseResponseVO detail(@RequestParam Integer id) {
        return productService.detail(id);
    }

    /**
     * 编辑商品
     */
    @PostMapping("/update")
    public BaseResponseVO update(@RequestBody CreateProductRequestVO request) {
        return productService.update(request);
    }

    /**
     * 上架 / 下架
     */
    @PostMapping("/status")
    public BaseResponseVO status(@RequestBody Map<String, Object> body) {
        Integer id = Integer.valueOf(String.valueOf(body.get("id")));
        Integer shopId = Integer.valueOf(String.valueOf(body.get("shopId")));
        Integer status = Integer.valueOf(String.valueOf(body.get("status")));
        return productService.changeStatus(id, shopId, status);
    }

    /**
     * 删除商品（已产生订单禁止删除，前端二次确认）
     */
    @DeleteMapping("/delete")
    public BaseResponseVO delete(@RequestParam Integer id, @RequestParam Integer shopId) {
        return productService.delete(id, shopId);
    }

    /**
     * 批量设置：批量修改状态、配送方式、分类、排序、库存等
     */
    @PostMapping("/batch")
    public BaseResponseVO batch(@RequestBody BatchUpdateRequestVO request) {
        return productService.batchUpdate(request);
    }

    /**
     * 筛选下拉选项
     */
    @GetMapping("/options")
    public BaseResponseVO options(@RequestParam Integer shopId) {
        return productService.options(shopId);
    }

    /**
     * Excel 导出当前筛选结果
     */
    @GetMapping("/export")
    public void export(ProductQueryVO query, HttpServletResponse response) {
        if (query.getPageNum() == null) {
            query.setPageNum(1);
        }
        query.setPageSize(100000);
        List<ProductPO> list = productMapper.queryAll(query);
        excelService.exportProducts(list, response);
    }

    /**
     * Excel 导入：批量新增 / 更新商品
     */
    @PostMapping("/import")
    public BaseResponseVO importExcel(@RequestParam("file") MultipartFile file, @RequestParam Integer shopId) {
        try {
            int[] result = excelService.importProducts(file, shopId);
            Map<String, Object> data = new HashMap<>();
            data.put("inserted", result[0]);
            data.put("updated", result[1]);
            return BaseResponseVO.success("导入成功：新增 " + result[0] + " 条，更新 " + result[1] + " 条", data);
        } catch (Exception e) {
            return BaseResponseVO.fail("导入失败：" + e.getMessage());
        }
    }

    // ==================== C 端 ====================

    /** C 端一级分类（去重） */
    @GetMapping("/mall/categories")
    public BaseResponseVO mallCategories() {
        return productService.mallCategories();
    }

    /** C 端商品列表：分类 + 排序 */
    @GetMapping("/mall/list")
    public BaseResponseVO mallList(@RequestParam(required = false) String category,
                                   @RequestParam(required = false) String sort) {
        return productService.mallList(category, sort);
    }

    /** C 端商品详情 */
    @GetMapping("/mall/detail")
    public BaseResponseVO mallDetail(@RequestParam Integer id) {
        return productService.mallDetail(id);
    }
}
