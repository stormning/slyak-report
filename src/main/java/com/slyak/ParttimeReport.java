package com.slyak;

import com.google.common.collect.Sets;
import com.slyak.module.ParttimeOrder;
import com.slyak.module.UserInfo;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.ObjectUtils.getDisplayString;

/**
 * .
 * <p/>
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 2015/1/6
 */
public class ParttimeReport extends SpringSupport {

    @Autowired
    private NamedParameterJdbcOperations jdbcOperations;

    public String excel() throws IOException, WriteException {
        WritableWorkbook rwb = null;
        try {
            String in = "d:\\rck.xls";
            String out = "d:\\rck_out.xls";
            Workbook wb = Workbook.getWorkbook(new File(in));
            rwb = Workbook.createWorkbook(new File(out), wb);
            WritableSheet sheet = rwb.getSheet(0);
            int row = 2;
            //cell 列号 行号
            String index = null;
            long begin = new SimpleDateFormat("yyyy-MM-dd").parse("2014-10-30").getTime();
            long end = new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-04").getTime();
            do {
                String mobile = StringUtils.trimToNull(sheet.getCell(4, row).getContents());
                index = StringUtils.trimToNull(sheet.getCell(0, row).getContents());
                if (index != null) {
                    UserInfo userInfo = getUserInfo(mobile);
                    if (userInfo != null) {
                        //社区ID
                        Long uid = userInfo.getId();
                        sheet.addCell(new Label(2, row, getDisplayString(uid)));
                        //性别
                        sheet.addCell(new Label(3, row, getDisplayString(userInfo.getGender())));
                        //身份证号
                        sheet.addCell(new Label(5, row, getDisplayString(userInfo.getIdNo())));
                        //入学年份
                        sheet.addCell(new Label(7, row, getDisplayString(userInfo.getYear())));

                        List<ParttimeOrder> parttimeOrders = getParttimeOrders(uid, begin, end);
                        Set<Long> canceledPtIds = Sets.newHashSet();
                        int canceled = 0;
                        for (ParttimeOrder po : parttimeOrders) {
                            if (po.getStatus() == 1) {
                                canceledPtIds.add(po.getParttimeId());
                                canceled++;
                            }
                        }
                        //兼职报名次数
                        sheet.addCell(new Label(11, row, getDisplayString(parttimeOrders.size())));
                        //取消报名次数
                        sheet.addCell(new Label(12, row, getDisplayString(canceled)));
                        //取消报名岗位数
                        sheet.addCell(new Label(13, row, getDisplayString(canceledPtIds.size())));
                    }
                }
                row++;
            } while (index != null);
            rwb.write();
        } catch (Exception e) {
            return e.getLocalizedMessage();
        } finally {
            if (rwb != null) {
                rwb.close();
            }
        }
        return "ok";
    }

    private UserInfo getUserInfo(String mobile) {
        try {
            String sql = "SELECT ur.id, ur.real_name realName, CASE u.gender WHEN 0 THEN '男' ELSE '女' END gender, ur.id_no idNo, u.enter_year YEAR, ur.mobile FROM m_user_real ur, m_user u WHERE ur.id = u.id and ur.mobile = :mobile";
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("mobile", mobile);
            return jdbcOperations.queryForObject(sql, params, new BeanPropertyRowMapper<UserInfo>(UserInfo.class));
        } catch (Exception e) {
            System.out.println(mobile);
            return null;
        }
    }

    private List<ParttimeOrder> getParttimeOrders(long userId, long beginAt, long endAt) {
        String sql = "SELECT user_id userId,status,parttime_id parttimeId FROM m_parttime_order where user_id = :userId and order_at>=:beginAt AND order_at<=:endAt ";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("beginAt", beginAt);
        params.put("endAt", endAt);
        return jdbcOperations.query(sql, params, new BeanPropertyRowMapper<ParttimeOrder>(ParttimeOrder.class));
    }

    public static void main(String[] args) {
        ParttimeReport reportMain = new ParttimeReport();
        try {
            reportMain.excel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
