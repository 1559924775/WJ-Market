package com.uestc.dao;

import com.uestc.domain.TbBrokerMessagelog;
import com.uestc.domain.TbBrokerMessagelogExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TbBrokerMessagelogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_broker_messagelog
     *
     * @mbg.generated Sun Jun 16 11:16:25 GMT+08:00 2019
     */
    long countByExample(TbBrokerMessagelogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_broker_messagelog
     *
     * @mbg.generated Sun Jun 16 11:16:25 GMT+08:00 2019
     */
    int deleteByExample(TbBrokerMessagelogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_broker_messagelog
     *
     * @mbg.generated Sun Jun 16 11:16:25 GMT+08:00 2019
     */
    int insert(TbBrokerMessagelog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_broker_messagelog
     *
     * @mbg.generated Sun Jun 16 11:16:25 GMT+08:00 2019
     */
    int insertSelective(TbBrokerMessagelog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_broker_messagelog
     *
     * @mbg.generated Sun Jun 16 11:16:25 GMT+08:00 2019
     */
    List<TbBrokerMessagelog> selectByExample(TbBrokerMessagelogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_broker_messagelog
     *
     * @mbg.generated Sun Jun 16 11:16:25 GMT+08:00 2019
     */
    int updateByExampleSelective(@Param("record") TbBrokerMessagelog record, @Param("example") TbBrokerMessagelogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_broker_messagelog
     *
     * @mbg.generated Sun Jun 16 11:16:25 GMT+08:00 2019
     */
    int updateByExample(@Param("record") TbBrokerMessagelog record, @Param("example") TbBrokerMessagelogExample example);
}