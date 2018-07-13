package com.example.demo.sevice;



import java.util.List;

/**
 * 抽象类集成 Repository
 * 主要通过取读配置中的javabean实现基本的增删改查
 *@apiNote  T 实体类名称
 * @apiNote M 实体类对应的数据库接口
 * made by yanghaixin 2017/11/28
 *
 * */

public abstract class EntityDao<T ,M extends PagingAndSortingRepository<T,Long>>  {
    private M m;
    private  T t;
    public   void setT(T t){
        this.t =t;

    }
    //java bean must be inject
    public  void setRepository(M repository){
        this.m =repository;
    }

    public  M getRepository(){
        return this.m;
    }

    public void save(T t){
        m.save(t);
    }

    public  void save(Iterable<T> t){
        m.save(t);
    }




    public void delete(Long var1){
       m.delete(var1);

   }

    public T findOne(Long var1){
      T t=  m.findOne(var1);
        return  t;
    }

    public List<T>  findAll(){
        return (List<T>) m.findAll();
    }



    public Page<T> findAll(PageRequest pageable){
        return  m.findAll(pageable);
    }


}
