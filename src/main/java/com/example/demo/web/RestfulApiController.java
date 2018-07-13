package com.example.demo.web;


import com.example.demo.utils.InvokeUtils;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * post/创建
 * delete/删除
 * put/更新或创建
 * get/查看
 * 由于Tomcat8版本升级service方法中不在支持put和delete，坑爹！！所以不适用
 * made by yanghaixin 2017/11/21
 * */


@RestController
@RequestMapping(value = "/api/v1")
public class RestfulApiController {

    @Autowired
    JdbcTemplate jdbcTemplate;
    //批量插入的大小
    public volatile int batchSize =50;


    /**
     * @param dataTable 数据表名称
     */
    @RequestMapping(value = "/delete/{dataTable}")
    @Transactional
    public String deleteEntity( @PathVariable("dataTable") String dataTable,  @RequestParam("id") Long id) {
        ApplicationContext ctx = ApplicationContextHolder.getInstance().getApplicationContext();
        EntityDao entityDao = new EntityDao() {
            @Override
            public void setRepository(PagingAndSortingRepository repository) {
                super.setRepository(repository);
            }

            @Override
            public void delete(Long var1) {
                super.delete(var1);
            }
        };
        try {
            entityDao.setRepository((PagingAndSortingRepository) ctx.getBean(dataTable + "Repository"));
            entityDao.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
        return "success";
    }


    /**
     * 插入实体类
     *
     * @param dataTable 数据表名称
     */
    @RequestMapping(value = "/insert/{dataTable}")
    @Transactional
      public String insertEntity( @PathVariable String dataTable,  @RequestParam Map<String, Object> checkValue) {
        ApplicationContext ctx = ApplicationContextHolder.getInstance().getApplicationContext();
        EntityDao entityDao = new EntityDao() {
            @Override
            public void setT(Object o) {
                super.setT(o);
            }

            @Override
            public void setRepository(PagingAndSortingRepository repository) {
                super.setRepository(repository);
            }

            @Override
            public void save(Object o) {
                super.save(o);
            }
        };


        try {
            Class<?> Clazz = Class.forName("cn.edu.shou.OceanEconomic.domain." + dataTable);
            entityDao.setRepository((PagingAndSortingRepository) ctx.getBean(dataTable + "Repository"));
            Object obj = Clazz.newInstance();
            for (Map.Entry<String, Object> entry : checkValue.entrySet()) {
                String methodSuffix = entry.getKey().replaceFirst(entry.getKey().substring(0, 1), entry.getKey().substring(0, 1).toUpperCase());
                Method[] Methods = Clazz.getDeclaredMethods();
                obj=InvokeUtils.methodInvoke(Methods,methodSuffix,obj,entry);
            }

            entityDao.save(obj);
        } catch (Exception e) {
            System.out.println(e);
            return "catch exception"+ e.getLocalizedMessage() + ",ERROR";
        }
        return "success";

    }

    /**
     * 批量插入实体类
     *
     * @param dataTable 数据表名称
     */
    @RequestMapping(value = "/batchInsert/{dataTable}")
    @Transactional
      public String batchInsertEntity( @PathVariable String dataTable,@RequestBody List<Map<String, Object>> totalValues) {
        ApplicationContext ctx = ApplicationContextHolder.getInstance().getApplicationContext();
        EntityDao entityDao = new EntityDao() {
            @Override
            public void setT(Object o) {
                super.setT(o);
            }

            @Override
            public void setRepository(PagingAndSortingRepository repository) {
                super.setRepository(repository);
            }

            @Override
            public void save(Iterable o) {
                super.save(o);
            }
        };
        try {
            Class<?> Clazz = Class.forName("cn.edu.shou.OceanEconomic.domain." + dataTable);
            entityDao.setRepository((PagingAndSortingRepository) ctx.getBean(dataTable + "Repository"));
            List<Object> objectList = new ArrayList<>();
            for(int i=0;i<totalValues.size();i++){
                Object obj = Clazz.newInstance();
                for (Map.Entry<String, Object> entry : totalValues.get(i).entrySet()) {
                    String methodSuffix = entry.getKey().replaceFirst(entry.getKey().substring(0, 1), entry.getKey().substring(0, 1).toUpperCase());
                    Method[] Methods = Clazz.getDeclaredMethods();
                    obj =InvokeUtils.methodInvoke(Methods,methodSuffix,obj,entry);
                }
                objectList.add(obj);
                if(i!=0 && (i+1)%batchSize==0){
                    entityDao.save(objectList);
                    objectList.clear();
                }
            }


        } catch (Exception e) {
            System.out.println(e);
            return "catch exception"+ e.getLocalizedMessage() + ",ERROR";
        }
        return "success";

    }




    /**
     * 更新实体类
     *
     * @param dataTable 数据表名称
     */

    @RequestMapping(value = "/update/{dataTable}")
    @Modifying
    @Transactional
    public String updateEntity(@PathVariable String dataTable,  @RequestParam Map<String, Object> checkValue) {
        ApplicationContext ctx = ApplicationContextHolder.getInstance().getApplicationContext();
        EntityDao entityDao = new EntityDao() {
            @Override
            public void setT(Object o) {
                super.setT(o);
            }

            @Override
            public void setRepository(PagingAndSortingRepository repository) {
                super.setRepository(repository);
            }

            @Override
            public void save(Object o) {
                super.save(o);
            }

            @Override
            public Object findOne(Long var1) {
                return super.findOne(var1);
            }
        };
        try {

            Class<?> Clazz = Class.forName("cn.edu.shou.OceanEconomic.domain." + dataTable);
            entityDao.setRepository((PagingAndSortingRepository) ctx.getBean(dataTable + "Repository"));
            Object obj = entityDao.findOne(Long.parseLong(String.valueOf(checkValue.get("id"))));
            for (Map.Entry<String, Object> entry : checkValue.entrySet()) {
                if (!entry.getKey().equals("id")) {
                    String methodSuffix = entry.getKey().replaceFirst(entry.getKey().substring(0, 1), entry.getKey().substring(0, 1).toUpperCase());
                    Method[] Methods = Clazz.getDeclaredMethods();
                    obj =InvokeUtils.methodInvoke(Methods,methodSuffix,obj,entry);
                }
            }
            entityDao.save(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
        return "success";
    }


    /**
     * 批量更新实体类
     *
     * @param dataTable 数据表名称
     */
    @RequestMapping(value = "/batchUpdate/{dataTable}")
    @Transactional
     public String batchUpdateEntity( @PathVariable String dataTable, @RequestBody List<Map<String, Object>> totalValues) {
        ApplicationContext ctx = ApplicationContextHolder.getInstance().getApplicationContext();
        EntityDao entityDao = new EntityDao() {
            @Override
            public void setT(Object o) {
                super.setT(o);
            }

            @Override
            public void setRepository(PagingAndSortingRepository repository) {
                super.setRepository(repository);
            }

            @Override
            public void save(Iterable o) {
                super.save(o);
            }
        };
        try {
            Class<?> Clazz = Class.forName("cn.edu.shou.OceanEconomic.domain." + dataTable);
            entityDao.setRepository((PagingAndSortingRepository) ctx.getBean(dataTable + "Repository"));
            List<Object> objectList = new ArrayList<>();
            for(int i=0;i<totalValues.size();i++){
                Object obj = entityDao.findOne(Long.parseLong(String.valueOf(totalValues.get(i).get("id"))));
                for (Map.Entry<String, Object> entry : totalValues.get(i).entrySet()) {
                    if (!entry.getKey().equals("id")) {
                        String methodSuffix = entry.getKey().replaceFirst(entry.getKey().substring(0, 1), entry.getKey().substring(0, 1).toUpperCase());
                        Method[] Methods = Clazz.getDeclaredMethods();
                        obj =InvokeUtils.methodInvoke(Methods,methodSuffix,obj,entry);
                    }
                }
                objectList.add(obj);
                if(i!=0 && (i+1)%batchSize==0){
                    entityDao.save(objectList);
                    objectList.clear();
                }
            }


        } catch (Exception e) {
            System.out.println(e);
            return "catch exception"+ e.getLocalizedMessage() + ",ERROR";
        }
        return "success";

    }
    /**
     * 分页查询
     *
     * @param dataTable 数据表名称
     */
    @RequestMapping(value = "/find/{dataTable}")
     public Map<String, Object> findAllEntity( @PathVariable String dataTable, @RequestParam Map<String, Object> data) {
        ApplicationContext ctx = ApplicationContextHolder.getInstance().getApplicationContext();
        PageRequest pageable = new PageRequest(Integer.valueOf(data.get("page").toString()) - 1, Integer.valueOf(data.get("pageSize").toString()));
        EntityDao entityDao = new EntityDao() {
            @Override
            public void setRepository(PagingAndSortingRepository repository) {
                super.setRepository(repository);
            }

            @Override
            public Page findAll(PageRequest pageable) {
                return super.findAll(pageable);
            }
        };
        entityDao.setRepository((PagingAndSortingRepository) ctx.getBean(dataTable + "Repository"));
        Page<Object> page2 = entityDao.findAll(pageable);
        List<Object> returnData = page2.getContent();
        Integer totalNum = entityDao.findAll().size();
        Map<String, Object> result = new HashMap<>();

        result.put("data", returnData);
        result.put("total", totalNum);
        return result;
    }


    /**
     * 根据模糊字段，精确字段查询
     */
    @RequestMapping(value = "/query/{dataTable}")
    public List<Map<String, Object>> queryEntity( @PathVariable String dataTable, @RequestParam Map<String, Object> checkValue) {

        String queryForList = "select * from " + dataTable + " where ";
        int initQueryLength = queryForList.length();
        for (Map.Entry<String, Object> entry : checkValue.entrySet()) {
            if (!(entry.getValue() == null || entry.getValue().equals(""))) {
                queryForList += " " + entry.getKey() + " like  '%" + entry.getValue() + "%' and";
            }
        }
        queryForList = queryForList.substring(0, queryForList.length() - 3) + ";";
        if (initQueryLength + 1 > queryForList.length()) {
            queryForList = queryForList.substring(0, queryForList.length() - 5);
        }
        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(queryForList);
        return queryResult;
    }


    /**
     * 严格精确查询
     */
    @RequestMapping(value = "/queryAccurate/{dataTable}")
     public List<Map<String, Object>> queryAccurateEntity(@PathVariable String dataTable,  @RequestParam Map<String, Object> checkValue) {

        String queryForList = "select * from " + dataTable + " where ";
        int initQueryLength = queryForList.length();
        for (Map.Entry<String, Object> entry : checkValue.entrySet()) {
            if (!(entry.getValue() == null || entry.getValue() .equals(""))) {
                queryForList += " " + entry.getKey() + " =  '" + entry.getValue() + "' and";
            }
        }
        queryForList = queryForList.substring(0, queryForList.length() - 3) + ";";
        if (initQueryLength + 1 > queryForList.length()) {
            queryForList = queryForList.substring(0, queryForList.length() - 5);
        }
        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(queryForList);
        return queryResult;
    }

    /**
     * 数量查询
     */
    @RequestMapping(value = "/queryCount/{dataTable}")
     public Integer queryCountEntity( @PathVariable String dataTable,  @RequestParam Map<String, Object> checkValue) {

        String queryForList = "select count(*) from " + dataTable + " where ";
        int initQueryLength = queryForList.length();
        for (Map.Entry<String, Object> entry : checkValue.entrySet()) {
            if (!(entry.getValue() == null || entry.getValue().equals(""))) {
                queryForList += " " + entry.getKey() + " like  '" + entry.getValue() + "%' and";
            }
        }
        queryForList = queryForList.substring(0, queryForList.length() - 3) + ";";
        if (initQueryLength + 1 > queryForList.length()) {
            queryForList = queryForList.substring(0, queryForList.length() - 5);
        }
        Integer queryResult = jdbcTemplate.queryForObject(queryForList,Integer.class);

        return queryResult;
    }


    /**
     * 找不同
     */
    @RequestMapping(value = "/queryDiff/{dataTable}/{distinctColumn}")
     public List queryDiffEntity(@PathVariable String dataTable, @RequestParam Map<String, Object> checkValue, @PathVariable String  distinctColumn) {

        String queryForList = "select distinct ("+distinctColumn+") from " + dataTable + " where ";
        int initQueryLength = queryForList.length();
        for (Map.Entry<String, Object> entry : checkValue.entrySet()) {
            if (!(entry.getValue() == null || entry.getValue().equals(""))) {
                queryForList += " " + entry.getKey() + " =  '" + entry.getValue() + "' and";
            }
        }
        queryForList = queryForList.substring(0, queryForList.length() - 3) + ";";
        if (initQueryLength + 1 > queryForList.length()) {
            queryForList = queryForList.substring(0, queryForList.length() - 5);
        }
        return jdbcTemplate.queryForList(queryForList);
    }

}