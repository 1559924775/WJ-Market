package com.uestc.dao;

import com.uestc.domain.TbPackage;
import com.uestc.domain.TbPackageExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TbPackageMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_package
     *
     * @mbg.generated Sun Jun 16 15:40:20 GMT+08:00 2019
     */
    long countByExample(TbPackageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_package
     *
     * @mbg.generated Sun Jun 16 15:40:20 GMT+08:00 2019
     */
    int deleteByExample(TbPackageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_package
     *
     * @mbg.generated Sun Jun 16 15:40:20 GMT+08:00 2019
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_package
     *
     * @mbg.generated Sun Jun 16 15:40:20 GMT+08:00 2019
     */
    int insert(TbPackage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_package
     *
     * @mbg.generated Sun Jun 16 15:40:20 GMT+08:00 2019
     */
    int insertSelective(TbPackage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_package
     *
     * @mbg.generated Sun Jun 16 15:40:20 GMT+08:00 2019
     */
    List<TbPackage> selectByExample(TbPackageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_package
     *
     * @mbg.generated Sun Jun 16 15:40:20 GMT+08:00 2019
     */
    TbPackage selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_package
     *
     * @mbg.generated Sun Jun 16 15:40:20 GMT+08:00 2019
     */
    int updateByExampleSelective(@Param("record") TbPackage record, @Param("example") TbPackageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_package
     *
     * @mbg.generated Sun Jun 16 15:40:20 GMT+08:00 2019
     */
    int updateByExample(@Param("record") TbPackage record, @Param("example") TbPackageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_package
     *
     * @mbg.generated Sun Jun 16 15:40:20 GMT+08:00 2019
     */
    int updateByPrimaryKeySelective(TbPackage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_package
     *
     * @mbg.generated Sun Jun 16 15:40:20 GMT+08:00 2019
     */
    int updateByPrimaryKey(TbPackage record);
}