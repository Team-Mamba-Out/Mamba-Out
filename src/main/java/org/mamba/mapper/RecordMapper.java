package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.mamba.entity.Record;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RecordMapper {
    /**
     * Obtains the record list based on the conditions given.
     *
     * @param roomId    the room id
     * @param userId    the user id
     * @param startTime the start time (the query result should be later than this)
     * @param endTime   the end time (the query result should be earlier than this)
     * @param pageSize  the size of each page
     * @param offset    the offset
     * @return the list of all the records satisfying the condition(s)
     */
    @Select({
            "<script>",
            "select * from mamba.record where 1=1",
            "<if test='id != null'>",
            "and id = #{id}",
            "</if>",
            "<if test='roomId != null'>",
            "and roomId = #{roomId}",
            "</if>",
            "<if test='userId != null'>",
            "and userId = #{userId}",
            "</if>",
            "<if test='startTime != null'>",
            "and startTime >= #{startTime}",
            "</if>",
            "<if test='endTime != null'>",
            "and endTime <= #{endTime}",
            "</if>",
            "order by recordTime desc",
            "<if test='pageSize != null'>",
            "limit #{pageSize}",
            "</if>",
            "<if test='offset != null'>",
            "offset #{offset}",
            "</if>",
            "</script>"
    })
    List<Record> getRecords(Integer id, Integer roomId, Integer userId, LocalDateTime startTime, LocalDateTime endTime, Integer pageSize, Integer offset);

    /**
     * Insert a new record.
     *
     * @param roomId     Room ID
     * @param userId     User ID
     * @param startTime  Start Time
     * @param endTime    End Time
     * @param recordTime Record TIme
     */
    @Insert("insert into mamba.record(roomId, userId, startTime, endTime, recordTime) values (#{roomId}, #{userId}, #{startTime}, #{endTime}, #{recordTime})")
    void createRecord(Integer roomId, Integer userId, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime recordTime);

    /**
     * Deletes the record specified by id.
     *
     * @param id the provided id
     */
    @Delete("delete from mamba.record where id = #{id}")
    void deleteRecordById(Integer id);
}


///**
// * Obtains the record specified by ID given.
// *
// * @param id the provided id
// * @return the corresponding record, could be null
// */
//@Select("select * from mamba.record where id = #{id};")
//Record getRecordById(int id);
//
///**
// * Obtains the list of records specified by the User ID given.
// *
// * @param userId the provided User ID
// * @return the corresponding record list
// */
//@Select("select * from mamba.record where userId = #{userId} order by recordTime desc;")
//List<Record> getRecordsByUserId(int userId);
//
///**
// * Obtains the list of records specified by the Room ID given.
// *
// * @param roomId the provided Room ID
// * @return the corresponding record list
// */
//@Select("select * from mamba.record where roomId = #{roomId} order by recordTime desc;")
//List<Record> getRecordsByRoomId(int roomId);
//
///**
// * Obtains the list of records specified by the start time & end time range.
// * Only returns records that have the start time later than the time provided
// * and the end time earlier than the time provided.
// *
// * @param startTime the provided start time
// * @param endTime   the provided end time
// * @return the corresponding record list
// */
//@Select("select * from mamba.record where startTime >= #{startTime} and endTime <= #{endTime} order by recordTime desc;")
//List<Record> getRecordsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
