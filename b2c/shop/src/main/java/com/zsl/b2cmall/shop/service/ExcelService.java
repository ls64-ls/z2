package com.zsl.b2cmall.shop.service;

import com.zsl.b2cmall.shop.dao.ProductMapper;
import com.zsl.b2cmall.shop.entity.OrderPO;
import com.zsl.b2cmall.shop.entity.ProductPO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入导出（Apache POI）
 */
@Service
public class ExcelService {

    private static final String[] PRODUCT_HEADERS = {
            "商品ID", "商品名称", "市场价", "销售价", "商品销量", "注水销量", "仓库库存", "门店库存",
            "商品类型", "商品类别", "商品分类", "状态", "配送方式", "排序", "库存预警阈值"
    };

    private static final String[] ORDER_HEADERS = {
            "订单编号", "下单时间", "商品名称", "成交单价", "数量", "实付金额", "支付方式",
            "买家", "收货人", "联系电话", "订单状态", "售后状态", "物流类型", "物流单号", "门店名称"
    };

    @Autowired
    private ProductMapper productMapper;

    /** 导出商品列表 */
    public void exportProducts(List<ProductPO> products, HttpServletResponse response) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("商品列表");
            CellStyle head = headStyle(wb);
            Row header = sheet.createRow(0);
            for (int i = 0; i < PRODUCT_HEADERS.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(PRODUCT_HEADERS[i]);
                c.setCellStyle(head);
                sheet.setColumnWidth(i, 16 * 256);
            }
            int rowIdx = 1;
            for (ProductPO p : products) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getId() == null ? 0 : p.getId());
                row.createCell(1).setCellValue(p.getName() == null ? "" : p.getName());
                row.createCell(2).setCellValue(p.getMarketPrice() == null ? 0 : p.getMarketPrice().doubleValue());
                row.createCell(3).setCellValue(p.getSalePrice() == null ? 0 : p.getSalePrice().doubleValue());
                row.createCell(4).setCellValue(p.getSales() == null ? 0 : p.getSales());
                row.createCell(5).setCellValue(p.getFakeSales() == null ? 0 : p.getFakeSales());
                row.createCell(6).setCellValue(p.getWarehouseStock() == null ? 0 : p.getWarehouseStock());
                row.createCell(7).setCellValue(p.getStoreStock() == null ? 0 : p.getStoreStock());
                row.createCell(8).setCellValue(p.getProductType() == null ? "普通商品" : p.getProductType());
                row.createCell(9).setCellValue(p.getCategoryLabel());
                row.createCell(10).setCellValue(p.getCategory() == null ? "" : p.getCategory());
                row.createCell(11).setCellValue(p.getStatus() != null && p.getStatus() == 1 ? "上架" : "下架");
                row.createCell(12).setCellValue(p.getDeliveryTypes() == null ? "" : p.getDeliveryTypes());
                row.createCell(13).setCellValue(p.getSort() == null ? 0 : p.getSort());
                row.createCell(14).setCellValue(p.getStockThreshold() == null ? 0 : p.getStockThreshold());
            }
            write(wb, response, "商品列表.xlsx");
        } catch (Exception e) {
            throw new RuntimeException("导出商品失败：" + e.getMessage(), e);
        }
    }

    /** 导入商品：有 ID 且存在则更新，否则新增 */
    public int[] importProducts(MultipartFile file, Integer shopId) throws Exception {
        int inserted = 0;
        int updated = 0;
        try (InputStream in = file.getInputStream(); Workbook wb = WorkbookFactory.create(in)) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String name = str(row, 1);
                if (name.isEmpty()) {
                    continue;
                }
                ProductPO p = new ProductPO();
                Integer id = intVal(row, 0);
                p.setName(name);
                p.setMarketPrice(dec(row, 2));
                p.setSalePrice(dec(row, 3));
                if (p.getSalePrice() == null) {
                    p.setSalePrice(BigDecimal.ZERO);
                }
                p.setPrice(p.getSalePrice());
                p.setSales(intValOrZero(row, 4));
                p.setFakeSales(intValOrZero(row, 5));
                p.setWarehouseStock(intValOrZero(row, 6));
                p.setStoreStock(intValOrZero(row, 7));
                p.setStock(p.getWarehouseStock() + p.getStoreStock());
                String productType = str(row, 8);
                p.setProductType(productType.isEmpty() ? "普通商品" : productType);
                String label = str(row, 9);
                p.setType("虚拟商品".equals(label) ? 2 : 1);
                String category = str(row, 10);
                p.setCategory(category.isEmpty() ? "其他" : category);
                p.setStatus("上架".equals(str(row, 11)) ? 1 : 0);
                String delivery = str(row, 12);
                p.setDeliveryTypes(delivery.isEmpty() ? "快递配送" : delivery);
                p.setSort(intValOrZero(row, 13));
                p.setStockThreshold(intValOrZero(row, 14));
                String desc = str(row, 15);
                if (!desc.isEmpty()) {
                    p.setDescription(desc);
                }

                ProductPO existing = id == null ? null : productMapper.findById(id);
                if (existing != null) {
                    p.setId(id);
                    p.setShopId(shopId);
                    // 表格中未包含的字段保留原值
                    p.setCoverImage(existing.getCoverImage());
                    p.setDescription(desc.isEmpty() ? existing.getDescription() : desc);
                    productMapper.update(p);
                    updated++;
                } else {
                    p.setShopId(shopId);
                    p.setCoverImage(null);
                    p.setCreatedAt(null);
                    productMapper.insert(p);
                    inserted++;
                }
            }
        }
        return new int[]{inserted, updated};
    }

    /** 导出订单列表 */
    public void exportOrders(List<OrderPO> orders, HttpServletResponse response) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("订单列表");
            CellStyle head = headStyle(wb);
            Row header = sheet.createRow(0);
            for (int i = 0; i < ORDER_HEADERS.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(ORDER_HEADERS[i]);
                c.setCellStyle(head);
                sheet.setColumnWidth(i, 18 * 256);
            }
            int rowIdx = 1;
            for (OrderPO o : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nvl(o.getOrderNo()));
                row.createCell(1).setCellValue(nvl(o.getCreatedAt()));
                row.createCell(2).setCellValue(nvl(o.getProductName()));
                BigDecimal amount = o.getAmount() == null ? BigDecimal.ZERO : o.getAmount();
                int qty = o.getQuantity() == null || o.getQuantity() == 0 ? 1 : o.getQuantity();
                row.createCell(3).setCellValue(amount.divide(new BigDecimal(qty), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                row.createCell(4).setCellValue(qty);
                row.createCell(5).setCellValue(amount.doubleValue());
                row.createCell(6).setCellValue(nvl(o.getPayType()));
                row.createCell(7).setCellValue(nvl(o.getBuyerNick()));
                row.createCell(8).setCellValue(nvl(o.getBuyerName()));
                row.createCell(9).setCellValue(nvl(o.getBuyerPhone()));
                row.createCell(10).setCellValue(o.getStatusLabel());
                row.createCell(11).setCellValue(o.getAfterSaleLabel());
                row.createCell(12).setCellValue(nvl(o.getLogisticsType()));
                row.createCell(13).setCellValue(nvl(o.getDeliveryNo()));
                row.createCell(14).setCellValue(nvl(o.getStoreName()));
            }
            write(wb, response, "订单列表.xlsx");
        } catch (Exception e) {
            throw new RuntimeException("导出订单失败：" + e.getMessage(), e);
        }
    }

    private CellStyle headStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private void write(Workbook wb, HttpServletResponse response, String fileName) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encoded = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + encoded);
        try (OutputStream out = response.getOutputStream()) {
            wb.write(out);
            out.flush();
        }
    }

    private String str(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private Integer intVal(Row row, int idx) {
        String v = str(row, idx);
        if (v.isEmpty()) {
            return null;
        }
        try {
            return (int) Double.parseDouble(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int intValOrZero(Row row, int idx) {
        Integer v = intVal(row, idx);
        return v == null ? 0 : v;
    }

    private BigDecimal dec(Row row, int idx) {
        String v = str(row, idx);
        if (v.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String nvl(String v) {
        return v == null ? "" : v;
    }
}
